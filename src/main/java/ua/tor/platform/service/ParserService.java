package ua.tor.platform.service;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import ua.tor.platform.persistent.ParsedVacancy;
import ua.tor.platform.persistent.Status;
import ua.tor.platform.persistent.StopWord;
import ua.tor.platform.persistent.Vacancy;
import ua.tor.platform.repository.IParsedVacancyRepository;
import ua.tor.platform.repository.IStopWordRepository;
import ua.tor.platform.repository.IVacancyRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author alex
 */
@Component
public class ParserService {

    private static final Logger LOGGER = Logger.getLogger(ParserService.class);

    private final IParsedVacancyRepository parsedVacancyRepository;
    private final IVacancyRepository vacancyRepository;
    private final IStopWordRepository stopWordRepository;


    public ParserService(IParsedVacancyRepository parsedVacancyRepository, IVacancyRepository vacancyRepository, IStopWordRepository stopWordRepository) {
        this.parsedVacancyRepository = parsedVacancyRepository;
        this.vacancyRepository = vacancyRepository;
        this.stopWordRepository = stopWordRepository;
    }

    /**
     * The method will remove all words that are repeated and all symbolics;
     */
    public void parseVacancies() {
        StopWatch watcher = new StopWatch();
        watcher.start();
        LOGGER.info("Getting vacancies from DB by crawler id");

        List<Vacancy> batchOfVacancy = vacancyRepository.findByStatus(Status.NEW);
        changeStatusForVacancy(batchOfVacancy, Status.IN_PROCESS);

        LOGGER.info("Start chopping.. ");
        List<ParsedVacancy> parsedVacancies = chopChop(batchOfVacancy);
        LOGGER.info("Finish chopping.. ");

        changeStatusForVacancy(batchOfVacancy, Status.PROCESSED);
        LOGGER.info("Start saving to DB");

        saveOrUpdate(parsedVacancies);

        watcher.stop();
        LOGGER.info("Total time in seconds " + watcher.getTime(TimeUnit.SECONDS));
    }

    private void changeStatusForVacancy(List<Vacancy> batchOfVacancy, Status status) {
        for (Vacancy vacancy : batchOfVacancy) {
            vacancy.setStatus(status);
        }
        vacancyRepository.saveAll(batchOfVacancy);
    }

    private List<ParsedVacancy> chopChop(List<Vacancy> batchOfVacancy) {

        List<StopWord> listOfStopWords = getListOfStopWords();
        List<String> listOfStopWordsTypeString = getListOfStopWords(listOfStopWords);

        List<ParsedVacancy> bathOfParsedVacancy = new ArrayList<>();

        for (Vacancy vacancy : batchOfVacancy) {

            ParsedVacancy parsedVacancy = new ParsedVacancy();
            Set<String> cleanDescription = new HashSet<>();

            String description = vacancy.getDescription();
            description = description.replaceAll("[^a-zA-Z0-9'-]", " ");
            String[] splitedDescription = description.split("\\s+");

            for (String word : splitedDescription) {
                if (!word.isEmpty()) {
                    cleanDescription.add(word.toLowerCase());
                }
            }

            cleanDescription.removeAll(listOfStopWordsTypeString);
            parsedVacancy.setCrawlerId(vacancy.getCrawlerId());
            parsedVacancy.setDescription(cleanDescription);
            parsedVacancy.setStatus(Status.NEW);

            bathOfParsedVacancy.add(parsedVacancy);
        }
        return bathOfParsedVacancy;
    }


    private List<StopWord> getListOfStopWords() {
        return stopWordRepository.findAll();
    }

    private List<String> getListOfStopWords(List<StopWord> list) {
        List<String> listOfStopWordsTypeString = new ArrayList<>();
        for (StopWord stopWord : list) {
            listOfStopWordsTypeString.add(stopWord.getKey());
        }
        return listOfStopWordsTypeString;
    }

    private void saveOrUpdate(List<ParsedVacancy> parsedVacancies) {
        parsedVacancyRepository.saveAll(parsedVacancies);
    }
}
