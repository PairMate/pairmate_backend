package pairmate.common_libs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDto {
    private double averageStarRating;
    private Long reviewCount;
}

