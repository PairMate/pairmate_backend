package pairmate.pay_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.pay_service.domain.common.BaseEntity;
import pairmate.user_service.domain.Users;

import java.time.LocalDate;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "childCards")
public class ChildCards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false)
    private Long cardId;

    private Long userId;

    @Column(name = "card_num", nullable = false, length = 100)
    private String cardNum;

    @Column(name = "pay_password", nullable = false, length = 100)
    private String payPassword;

    @Column(name = "valid_date", nullable = false)
    private LocalDate validDate;
}