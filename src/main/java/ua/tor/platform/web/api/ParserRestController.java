package ua.tor.platform.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tor.platform.config.ApiVersion;
import ua.tor.platform.service.ParserService;

@RestController
@RequestMapping(ApiVersion.V1)
public class ParserRestController {

    private final ParserService parserService;

    public ParserRestController(ParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/parser")
    public ResponseEntity<?> runParser() {
        parserService.parseVacancies();
        return ResponseEntity.ok().build();
    }
}
