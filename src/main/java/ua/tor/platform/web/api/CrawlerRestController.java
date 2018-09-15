package ua.tor.platform.web.api;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tor.platform.config.ApiVersion;
import ua.tor.platform.service.CrawlerService;
import ua.tor.platform.web.api.dto.CrawlerRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(ApiVersion.V1)
public class CrawlerRestController {

    private final CrawlerService crawlerService;

    public CrawlerRestController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping("/crawler")
    public ResponseEntity<?> runCrawler(@RequestBody @Valid CrawlerRequest crawlerRequest) throws InterruptedException, ExecutionException, IOException {
        ObjectId id = crawlerService.startCrawling(crawlerRequest);
        return ResponseEntity.ok(id);
    }
}
