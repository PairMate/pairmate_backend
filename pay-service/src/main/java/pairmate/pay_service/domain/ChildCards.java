package pairmate.pay_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.pay_service.domain.common.BaseEntity;

import java.time.LocalDate;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "child_cards")
public class ChildCards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    private Long userId;

    @Column(nullable = false, unique = true, length = 16)
    private String cardNum;

    @Column(nullable = false, length = 3)
    private String cvc;

    @Column(nullable = false)
    private LocalDate expireDate;

    @Column(name = "day_limit", columnDefinition = "INT DEFAULT 0", nullable = false)
    @Builder.Default
    private int dayLimit = 0;

    @Column(nullable = false, length = 2)
    private String password;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    @Builder.Default
    private int cash = 0;

    public void updateDayLimit(int limit) {
        this.dayLimit = limit;
    }

    /** 잔액 변경 */
    public void updateCash(int cash) {
        this.cash = cash;
    }
}
