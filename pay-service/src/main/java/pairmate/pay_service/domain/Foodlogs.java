package pairmate.pay_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "foodlogs")
public class Foodlogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_log_id", nullable = false)
    private Long foodLogId;

    // 메뉴 FK
    private Long menuId;

    // 유저 FK
    private Long userId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
}
