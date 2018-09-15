package ua.tor.platform.model;


import java.time.LocalDate;
import java.util.List;

public class Skill {

    private String skillName;
    private long amountOfVacancies;
    private LocalDate date;
    private List<Subskill> subskills;

    public Skill(String skillName, long amountOfVacancies, LocalDate date, List<Subskill> subskills) {
        this.skillName = skillName;
        this.amountOfVacancies = amountOfVacancies;
        this.date = date;
        this.subskills = subskills;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public long getAmountOfVacancies() {
        return amountOfVacancies;
    }

    public void setAmountOfVacancies(long amountOfVacancies) {
        this.amountOfVacancies = amountOfVacancies;
    }

    public List<Subskill> getSubskills() {
        return subskills;
    }

    public void setSubskills(List<Subskill> subskills) {
        this.subskills = subskills;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "skillName='" + skillName + '\'' +
                ", amountOfVacancies=" + amountOfVacancies +
                ", date=" + date +
                '}';
    }
}
