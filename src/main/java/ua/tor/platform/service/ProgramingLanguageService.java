package ua.tor.platform.service;

import org.springframework.stereotype.Service;
import ua.tor.platform.persistent.ProgramingLanguage;
import ua.tor.platform.repository.IProgrammingLanguageRepository;

import java.util.List;

@Service
public class ProgramingLanguageService {

    private final IProgrammingLanguageRepository programmingLanguage;

    public ProgramingLanguageService(IProgrammingLanguageRepository programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public List<ProgramingLanguage> getAll() {
        return programmingLanguage.findAll();
    }
}
