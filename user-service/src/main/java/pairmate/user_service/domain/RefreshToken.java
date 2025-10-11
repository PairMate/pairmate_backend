package pairmate.user_service.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*3)
public class RefreshToken {

    @Id
    private String tokenId;

    private Long userId;

    private String refreshToken;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiration;

    private String accessToken;
}
