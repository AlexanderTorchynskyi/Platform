package ua.tor.platform.service;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IParsedVacancyRepository parsedVacancyRepository;
    @Autowired
    private IVacancyRepository vacancyRepository;
    @Autowired
    private IStopWordRepository stopWordRepository;

    private List<Vacancy> batchOfVacancy;
    private List<ParsedVacancy> bathOfParsedVacancy;
    private List<StopWord> listOfStopWords;
    private List<String> listOfStopWordsTypeString;

    /**
     * The method will remove all words that are repeated and all symbolics;
     *
     */
    public void parseVacancies() {
        StopWatch watcher = new StopWatch();
        watcher.start();
        LOGGER.info("Getting vacancies from DB by crawler id");
        batchOfVacancy = vacancyRepository.findByStatus(Status.NEW);
        changeStatusForVacancy(Status.IN_PROCESS);
        LOGGER.info("Start chopping.. ");
        chopChop();
        LOGGER.info("Finish chopping.. ");
        changeStatusForVacancy(Status.PROCESSED);
        LOGGER.info("Start saving to DB");
        saveOrUpdate(bathOfParsedVacancy);
        watcher.stop();
        LOGGER.info("Total time in seconds " + watcher.getTime(TimeUnit.SECONDS));
    }

    private void changeStatusForVacancy(Status status) {
        for (Vacancy vacancy : batchOfVacancy) {
            vacancy.setStatus(status);
        }
        vacancyRepository.saveAll(batchOfVacancy);
    }

    private void chopChop() {
        String description;
        ParsedVacancy parsedVacancy;

        listOfStopWords = getListOfStopWords();
        getListOfStopWords(listOfStopWords);
        String[] splitedDescription;
        bathOfParsedVacancy = new ArrayList<>();
        Set<String> cleanDescription;

        for (Vacancy vacancy : batchOfVacancy) {

            parsedVacancy = new ParsedVacancy();
            cleanDescription = new HashSet<>();

            description = vacancy.getDescription();
            description = description.replaceAll("[^a-zA-Z0-9'-]", " ");
            splitedDescription = description.split("\\s+");

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
    }

    private List<StopWord>  getListOfStopWords() {
        return stopWordRepository.findAll();
    }

    private List<String> getListOfStopWords(List<StopWord> list) {
        listOfStopWordsTypeString = new ArrayList<>();
        for (StopWord stopWord : list) {
            listOfStopWordsTypeString.add(stopWord.getKey());
        }
        return listOfStopWordsTypeString;
    }

    private void saveOrUpdate(List<ParsedVacancy> parsedVacancies) {
        parsedVacancyRepository.saveAll(parsedVacancies);
    }
}
