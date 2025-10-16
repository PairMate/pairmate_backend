package pairmate.store_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter // @ModelAttribute 바인딩을 위해 Setter가 반드시 필요합니다.
@Schema(description = "가게 등록 요청 DTO")
public class StoreRegisterRequest {

    @Schema(description = "가게 카테고리 ID", example = "1")
    private Long storeCategoryId;

    @Schema(description = "가게 이름", example = "맛있는 파스타집")
    private String storeName;

    @Schema(description = "가게 연락처", example = "02-1234-5678")
    private String storeContactNumber;

    @Schema(description = "가게 대표 이미지 URL", example = "https://example.com/image.jpg")
    private String storeMainImageUrl;

//    @Schema(description = "가게 주소", example = "서울시 강남구 테헤란로 123")
//    private Point storeLocate;

    @Schema(description = "가게 위치 - 위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "가게 위치 - 경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "가게 타입 (예: 한식, 양식, 카페)", example = "양식")
    private String storeType;

    @Schema(description = "영업 시작 시간 (HH:mm:ss 형식)", example = "10:00:00", type = "string")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime storeOpenTime;

    @Schema(description = "영업 종료 시간 (HH:mm:ss 형식)", example = "22:00:00", type = "string")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime storeCloseTime;

    @Schema(description = "가게 상세 설명", example = "정통 이탈리안 셰프가 만드는 파스타 전문점입니다.")
    private String storeContent;

    @Schema(description = "수용 가능 인원", example = "20")
    private Integer freePeople;

    @Schema(description = "등록할 메뉴 목록")
    private List<MenuDto> menus;

    // 메뉴 리스트를 받기 위한 내부 static 클래스
    @Getter
    @Setter
    public static class MenuDto {
        @Schema(description = "메뉴 이름", example = "까르보나라")
        private String menuName;

        @Schema(description = "메뉴 가격", example = "15000")
        private Integer menuPrice;
    }
}