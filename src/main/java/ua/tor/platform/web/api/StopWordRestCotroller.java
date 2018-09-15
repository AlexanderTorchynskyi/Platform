package ua.tor.platform.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tor.platform.config.ApiVersion;
import ua.tor.platform.service.StopWordService;

@RestController
@RequestMapping(ApiVersion.V1)
public class StopWordRestCotroller {

    private final StopWordService stopWordService;

    public StopWordRestCotroller(StopWordService stopWordService) {
        this.stopWordService = stopWordService;
    }

    @PostMapping("/stopwords")
    public ResponseEntity<?> loadStopWords() {
        stopWordService.loadStopWords();
        return ResponseEntity.ok().build();
    }
}
