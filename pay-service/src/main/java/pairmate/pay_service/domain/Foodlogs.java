package pairmate.pay_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.store_service.domain.Menus;
import pairmate.user_service.domain.Users;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menus menu;

    // 유저 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
}
