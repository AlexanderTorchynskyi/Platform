package ua.tor.platform.api;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.tor.platform.service.CrawlerService;


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

	/**
	 * Method will start crawler with specific word
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping(value = "crawler/start", method = RequestMethod.GET)
	public void run(@RequestParam(value = "searchword") String searchword)
			throws IOException, InterruptedException, ExecutionException {
		crawlerService.startCrawling(searchword);
	}
}
