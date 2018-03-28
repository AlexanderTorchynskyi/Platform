package ua.tor.platform.api;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.tor.platform.service.CrawlerService;
import ua.tor.platform.service.ParserService;
import ua.tor.platform.service.StopWordService;


/**
 * 
 * @author alex
 *
 */
@RestController
@RequestMapping("platform/rest/")
public class PlatformController {

	private static final Logger LOGGER = Logger.getLogger(PlatformController.class);

	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private ParserService parserService;
	@Autowired
	private StopWordService stopWordService;

	/**
	 * Method will start crawler with specific word
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping(value = "crawler/start", method = RequestMethod.GET)
	public ResponseEntity<?> runCrawler(@RequestParam(value = "searchword") String searchword)
			throws IOException, InterruptedException, ExecutionException {
		crawlerService.startCrawling(searchword);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Method will start parsing vacancies by specific crawler;
	 * 
	 * @param crawlerId
	 */
	@RequestMapping(value = "parser/start", method = RequestMethod.GET)
	public ResponseEntity<?> runParser(@RequestParam(value = "crawler_id") ObjectId crawlerId) {
		parserService.parseVacancies(crawlerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Method will start parsing vacancies by specific crawler;
	 * 
	 * @param crawlerId
	 */
	@RequestMapping(value = "stopwords/load", method = RequestMethod.GET)
	public ResponseEntity<?> loadStopWords() {
		stopWordService.loadStopWords();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
