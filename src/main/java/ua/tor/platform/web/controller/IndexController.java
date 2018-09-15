package ua.tor.platform.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.tor.platform.persistent.ProgramingLanguage;
import ua.tor.platform.service.ProgramingLanguageService;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    private final ProgramingLanguageService programingLanguageService;

    public IndexController(ProgramingLanguageService programingLanguageService) {
        this.programingLanguageService = programingLanguageService;
    }

    @GetMapping
    public String getIndex() {
        return "adminPage/main/index";
    }

    @GetMapping("lang/getAll")
    public String getAllLanguages(Model model) {
        List<ProgramingLanguage> programingLanguages = programingLanguageService.getAll();
        model.addAttribute(programingLanguages);
        return "adminPage/main/allLanguages";
    }


    @GetMapping("lang")
    public String getAllSubskills(@RequestParam("name") String name, Model model) {
        List<ProgramingLanguage> programingLanguages = programingLanguageService.getAll();
        model.addAttribute(programingLanguages);
        return "adminPage/main/allLanguages";
    }

}
