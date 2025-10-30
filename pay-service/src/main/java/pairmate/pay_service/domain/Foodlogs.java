package pairmate.pay_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        @Column(name = "store_name")
        private String storeName;

        @Column(name = "is_used", nullable = false)
        private Boolean isUsed;

        @Column(name = "used_at", nullable = true)
        private LocalDateTime usedAt;

        @Column(length = 30)
        private String category;

        @Column(name = "store_type", length = 50)
        private String storeType;

        @OneToMany(mappedBy = "foodlogs", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<FoodLogMenus> menus = new ArrayList<>();

        public void addMenu(FoodLogMenus menu) {
            this.menus.add(menu);
            menu.setFoodlogs(this);
    }
}
