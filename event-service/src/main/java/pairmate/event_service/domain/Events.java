package pairmate.event_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.event_service.domain.common.BaseEntity;

import java.time.LocalDate;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "events")
public class Events extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_name", nullable = false, length = 30)
    private String eventName;

    @Column(name = "content", length = 255, nullable = true)
    private String content;

    @Column(name = "join_store", length = 100, nullable = true)
    private String joinStore;

    @Column(name = "join_num", nullable = false)
    private Integer joinNum;

    @Column(name = "event_reward", length = 255, nullable = true)
    private String eventReward;

    @Column(name = "event_join_method", length = 255, nullable = true)
    private String eventJoinMethod;

    @Column(name = "event_start", nullable = false)
    private LocalDate eventStart;

    @Column(name = "event_end", nullable = false)
    private LocalDate eventEnd;
}
