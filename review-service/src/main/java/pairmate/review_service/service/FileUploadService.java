package pairmate.review_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 파일을 NCP Object Storage에 업로드하고 파일 URL을 반환
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "업로드할 파일이 없습니다.");
        }

        // 파일 원본 이름
        String originalFileName = file.getOriginalFilename();

        // 고유한 파일 이름 생성 (UUID 사용)
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 파일 메타데이터 생성 (파일 크기, 타입 등)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            /**
             * S3(NCP)에 파일 업로드
             * (bucketName, 고유한 파일 이름, 파일 InputStream, 메타데이터)
             */
            amazonS3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다.");
        }

        // 업로드된 파일의 URL 반환
        return amazonS3Client.getUrl(bucketName, uniqueFileName).toString();
    }
}
