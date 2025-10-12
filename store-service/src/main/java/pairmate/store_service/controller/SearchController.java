package pairmate.store_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}