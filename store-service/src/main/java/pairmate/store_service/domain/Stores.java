package pairmate.store_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.store_service.domain.common.BaseEntity;

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

    // 가게 카테고리 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_category_id", nullable = false)
    private StoreCategories storeCategory;

    @Column(name = "store_name", nullable = false, length = 50)
    private String storeName;

    @Column(name = "store_contact_number", length = 50)
    private String storeContactNumber;

    @Column(name = "store_main_image_url", length = 100)
    private String storeMainImageUrl;

    // 공간 타입이면 JTS 라이브러리 등으로 매핑 필요, 현재는 String으로 임시 처리
    @Column(name = "store_locate", nullable = false)
    private String storeLocate;

    @Column(name = "store_type", length = 200, nullable = true)
    private String storeType;

    @Column(name = "store_open_time", nullable = false)
    private LocalTime storeOpenTime;

    @Column(name = "store_close_time", nullable = false)
    private LocalTime storeCloseTime;

    @Column(name = "store_content", length = 200, nullable = true)
    private String storeContent;

    @Column(name = "free_people", nullable = true)
    private Integer freePeople;
}
