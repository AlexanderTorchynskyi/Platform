package ua.tor.platform.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.tor.platform.config.ApiVersion;
import ua.tor.platform.model.Skill;
import ua.tor.platform.service.IncrementorService;
import ua.tor.platform.web.api.dto.IncrementorRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.V1)
public class IncrementorRestController {

    private final IncrementorService incrementorService;

    public IncrementorRestController(IncrementorService incrementorService) {
        this.incrementorService = incrementorService;
    }

    @PostMapping("/incrementor")
    public ResponseEntity<?> runIncrementor(@RequestBody @Valid IncrementorRequest incrementorRequest) {
        incrementorService.getVacancies(incrementorRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> getVacancies(@RequestParam("skill") String search) {
        List<Skill> skills = incrementorService.getSkills(search);
        return ResponseEntity.ok(skills);
    }
}
