package ua.tor.platform.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.tor.platform.model.Skill;
import ua.tor.platform.service.IncrementorService;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    private final IncrementorService incrementorService;

    public IndexController(IncrementorService incrementorService) {
        this.incrementorService = incrementorService;
    }

    @GetMapping
    public String getIndex() {
        return "adminPage/main/index";
    }

    @GetMapping("lang/search")
    public String getAllSkills(@RequestParam("name") String skill, Model model) {
        List<Skill> skills = incrementorService.getSkills(skill);
        model.addAttribute(skills);
        return "adminPage/main/skills";
    }

    @GetMapping("lang/skill")
    public String getSkillWithSubskills(@RequestParam("name") String skill, Model model) {
        List<Skill> skills = incrementorService.getSkills(skill);
        model.addAttribute(skills);
        return "adminPage/main/renederSkills";
    }
}
