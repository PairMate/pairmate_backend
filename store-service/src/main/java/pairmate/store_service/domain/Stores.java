package pairmate.store_service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import pairmate.store_service.domain.common.BaseEntity;
import org.locationtech.jts.geom.Point;

import java.time.LocalTime;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "stores")
public class Stores extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    // 유저ID를 임시로(?) 저장해 두기 위한 컬럼이에요
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 가게 카테고리 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_category_id", nullable = false)
    private StoreCategories storeCategory;

    @Column(name = "store_name", nullable = false, length = 50)
    private String storeName;

    @Column(name = "store_contact_number", length = 50)
    @Schema(name = "가게 전화 번호")
    private String storeContactNumber;

    @Column(name = "store_main_image_url", length = 100)
    private String storeMainImageUrl;

//    // 공간 타입이면 JTS 라이브러리 등으로 매핑 필요, 현재는 String으로 임시 처리
//    @Column(name = "store_locate", nullable = false, columnDefinition = "POINT")
//    private Point storeLocate;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "store_type", length = 200, nullable = true)
    private String storeType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON 변환 시 포맷 지정
    @Column(name = "store_open_time", nullable = false)
    private LocalTime storeOpenTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "store_close_time", nullable = false)
    private LocalTime storeCloseTime;

    @Column(name = "store_content", length = 200, nullable = true)
    private String storeContent;

    @Column(name = "free_people", nullable = true)
    private Integer freePeople;

    // 가게 정보 업데이트 메서드 (이미지 URL 포함)
    public void updateStoreInfo(
            StoreCategories storeCategory,
            String storeName,
            String storeContactNumber,
            Double longitude,
            Double latitude,
            String storeType,
            LocalTime storeOpenTime,
            LocalTime storeCloseTime,
            String storeContent,
            Integer freePeople,
            String storeMainImageUrl
    ) {
        this.storeCategory = storeCategory;
        this.storeName = storeName;
        this.storeContactNumber = storeContactNumber;
        this.longitude = longitude;
        this.latitude = latitude;
        this.storeType = storeType;
        this.storeOpenTime = storeOpenTime;
        this.storeCloseTime = storeCloseTime;
        this.storeContent = storeContent;
        this.freePeople = freePeople;
        if (storeMainImageUrl != null) {
            this.storeMainImageUrl = storeMainImageUrl;
        }
    }
}
