package ua.tor.platform.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.tor.platform.model.Skill;
import ua.tor.platform.model.Subskill;
import ua.tor.platform.persistent.Crawler;
import ua.tor.platform.persistent.ParsedVacancy;
import ua.tor.platform.repository.IParsedVacancyRepository;
import ua.tor.platform.web.api.dto.IncrementorRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alex
 */
@Component
public class IncrementorService {

    private static final Logger logger = LoggerFactory.getLogger(IncrementorService.class);

    private static final String SAMPLE_CSV_FILE = "dump.csv";
    private static final Integer DEFAULT_WEIGHT = 1;

    private IParsedVacancyRepository parsedVacancy;
    private CrawlerService crawlerService;

    public IncrementorService(IParsedVacancyRepository parsedVacancy, CrawlerService crawlerService) {
        this.parsedVacancy = parsedVacancy;
        this.crawlerService = crawlerService;
    }

    public Map<String, Integer> getVacancies(IncrementorRequest incrementorRequest) {
        List<ParsedVacancy> listOfParsedVacancies = parsedVacancy.findByCrawlerId(incrementorRequest.getObjectId());
        return getCollection(listOfParsedVacancies);
    }

    private Map<String, Integer> getCollection(List<ParsedVacancy> lsOfVacancies) {

        Map<String, Integer> qntCounter = new HashMap<>();
        for (ParsedVacancy words : lsOfVacancies) {
            for (String word : words.getDescription()) {
                if (qntCounter.containsKey(word)) {
                    qntCounter.put(word, qntCounter.get(word) + 1);
                } else {
                    qntCounter.put(word, DEFAULT_WEIGHT);
                }
            }
        }
        return sortMap(qntCounter);
    }

    private Map<String, Integer> sortMap(Map<String, Integer> map) {
        Map<String, Integer> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    public List<Skill> getSkills(String searchWord) {
        List<Crawler> allBySearchCondition = crawlerService.findAllBySearchCondition(searchWord);
        List<Skill> skillList = new ArrayList<>();

        for (Crawler crawler : allBySearchCondition) {

            List<Subskill> subskills = new ArrayList<>();
            Map<String, Integer> vacancies = getVacancies(new IncrementorRequest(crawler.getId()));

            vacancies.forEach((k, v) -> {
                Subskill subskill = new Subskill();
                subskill.setName(k);
                subskill.setPersents((double) v / getAmountOfParsedVacancyByCrawlerId(crawler.getId()) * 100.0);
                subskills.add(subskill);
            });

            Skill skill = new Skill(getSkillByCrawlerId(crawler.getId()), getAmountOfParsedVacancyByCrawlerId(crawler.getId()), crawler.getCreatedDate(), subskills);
            skillList.add(skill);
            logger.info("skill added {}", skill);
        }
        return skillList;
    }

    private void writeToCsv(Map<String, Integer> map, ObjectId crawlerId) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("Skill", "Quantity"));) {
            csvPrinter.printRecord(getSkillByCrawlerId(crawlerId),
                    getAmountOfParsedVacancyByCrawlerId(crawlerId));
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }
            csvPrinter.flush();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private String getSkillByCrawlerId(ObjectId crawlerId) {
        Crawler crawler = crawlerService.findOneById(crawlerId);
        return crawler.getSearchCondition();
    }

    private long getAmountOfParsedVacancyByCrawlerId(ObjectId crawlerId) {
        return parsedVacancy.countByCrawlerId(crawlerId);
    }
}
