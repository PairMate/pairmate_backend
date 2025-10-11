package pairmate.store_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pairmate.store_service.service.SearchService;
import pairmate.store_service.web.dto.SearchResponse;

@Controller
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public SearchResponse search(@RequestParam("q") String query) {
        return searchService.search(query);
    }
}