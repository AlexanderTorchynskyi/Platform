package ua.tor.platform.web.api;


import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.tor.platform.model.Skill;
import ua.tor.platform.service.CrawlerService;
import ua.tor.platform.service.IncrementorService;
import ua.tor.platform.service.ParserService;
import ua.tor.platform.service.StopWordService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @author alex
 */
@RestController
@RequestMapping("/api")
public class PlatformController {

    private static final Logger LOGGER = Logger.getLogger(PlatformController.class);

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private ParserService parserService;
    @Autowired
    private StopWordService stopWordService;
    @Autowired
    private IncrementorService incrementorService;


    /**
     * Method will start crawler with specific word
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @RequestMapping(value = "crawler/start", method = RequestMethod.GET)
    public ResponseEntity<?> runCrawler(@RequestParam(value = "searchword") String searchword) {
        try {
            ObjectId id = crawlerService.startCrawling(searchword);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method will start parsing vacancies by specific crawler;
     */
    @RequestMapping(value = "parser/start", method = RequestMethod.GET)
    public ResponseEntity<?> runParser() {
        try {
            // TODO return amount of parsed vacancies;
            parserService.parseVacancies();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "stopwords/load", method = RequestMethod.GET)
    public ResponseEntity<?> loadStopWords() {
        try {
            boolean isDownloaded = stopWordService.loadStopWords();
            return new ResponseEntity<>(isDownloaded, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "incrementor/start")
    public ResponseEntity<?> runIncrementor(
            @RequestParam(value = "crawler_id") ObjectId crawlerId) {
        try {
            // TODO return a link that follows a google dock with rendered skills;
            incrementorService.getVacancies(crawlerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public List<Skill> getVacancies(@RequestParam("skill") String search) throws IOException, GeneralSecurityException {
        List<Skill> skills = incrementorService.getSkills(search);
        return skills;
    }
}
