package pairmate.event_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pairmate.event_service.domain.common.BaseEntity;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "eventJoinStores")
public class EventJoinStores extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_join_id", nullable = false)
    private Long eventJoinId;


    private Long storeId;

    private Long eventId;

}
