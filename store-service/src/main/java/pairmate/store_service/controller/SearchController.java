package pairmate.store_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.store_service.service.SearchService;
import pairmate.store_service.dto.SearchResponse;

@Controller
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 관련 API")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public SearchResponse search(@RequestParam("q") String query) {
        return searchService.search(query);
    }

    /**
     * Authorization 헤더에서 "Bearer " 제거
     */
    private String extractTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return header.substring(7);
    }

}