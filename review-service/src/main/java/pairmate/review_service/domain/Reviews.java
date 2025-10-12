package pairmate.review_service.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.review_service.domain.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "reviews")
public class Reviews extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    // 가게ID를 임시로(?) 저장해 두기 위한 컬럼이에요
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "star_rating", nullable = false)
    private Float starRating;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "content", length = 200, nullable = false)
    private String content;

    // setter 메서드 추가
    public void setStarRating(Float starRating) {
        this.starRating = starRating;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // update 메서드도 활용 가능
    public void update(Float starRating, LocalDateTime visitDate, String content) {
        this.starRating = starRating;
        this.visitDate = visitDate;
        this.content = content;
    }
}
