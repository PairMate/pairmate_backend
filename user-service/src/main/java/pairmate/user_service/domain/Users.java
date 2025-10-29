package pairmate.user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.user_service.domain.common.BaseEntity;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;


    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Column(name = "profile_url", length = 255, nullable = true)
    private String profileUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private String isActive = "ACTIVE";

    @Column(name = "user_role", length = 15, nullable = false)
    private String userRole;

    @Column(name = "user_type", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
