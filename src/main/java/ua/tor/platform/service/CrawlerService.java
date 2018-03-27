package ua.tor.platform.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.tor.platform.model.Crawler;
import ua.tor.platform.model.Status;
import ua.tor.platform.model.Vacancy;
import ua.tor.platform.repository.ICrawlerRepository;
import ua.tor.platform.repository.IVacancyRepository;
import ua.tor.platform.service.robotaua.RabotaDescription;
import ua.tor.platform.service.robotaua.RabotaLinks;
import ua.tor.platform.service.robotaua.RabotaPageBasicInformation;

/**
 * 
 * @author alex
 *
 */
@Component
public class CrawlerService {

	private static final Logger LOGGER = Logger.getLogger(CrawlerService.class);

	@Autowired
	private IVacancyRepository vacancyRepository;
	@Autowired
	private ICrawlerRepository crawlerRepository;
	private static final int amoutOfThreads = 8;
	private String searchword;
	private RabotaPageBasicInformation robota;
	private Crawler crawler;

	public void startCrawling(String searchword)
			throws IOException, InterruptedException, ExecutionException {
		this.searchword = searchword;
		StopWatch s = new StopWatch();
		crawler = new Crawler();
		crawler.setSearchCondition(searchword);
		crawler.setStatus(Status.NEW);
		crawler.setCreatedDate(new Date());
		crawler.setModifiedDate(new Date());

		crawler = saveOrUpdateCrawler(crawler);

		LOGGER.info("startet Crawling with params: searchWord " + searchword + " amount of threads "
				+ amoutOfThreads);

		s.start();

		robota = new RabotaPageBasicInformation(searchword, amoutOfThreads);
		crawler.setStatus(Status.IN_PROCESS);
		saveOrUpdateCrawler(crawler);
		List<Integer> listOfPagesRobota = robota.getListOfPages();
		ExecutorService ex = Executors.newWorkStealingPool();

		List<Future<List<Vacancy>>> linksRobota =
				ex.invokeAll(getListOfLinksTaskRobota(listOfPagesRobota, searchword));

		List<Callable<List<Vacancy>>> listOfTasksAll =
				new ArrayList<Callable<List<Vacancy>>>(getListOfDescriptionTaskRobota(linksRobota));

		List<Future<List<Vacancy>>> listOfVacancies = ex.invokeAll(listOfTasksAll);

		stop(ex);

		saveIntoDB(listOfVacancies);
		s.stop();
		crawler.setStatus(Status.PROCESSED);
		saveOrUpdateCrawler(crawler);

		LOGGER.info("Total time in seconds " + s.getTime(TimeUnit.SECONDS));
	}

	private void saveIntoDB(List<Future<List<Vacancy>>> entities) {
		LOGGER.info("Start saving into DB ");
		for (Future<List<Vacancy>> entity : entities) {
			try {
				vacancyRepository.saveAll(entity.get());
			} catch (InterruptedException | ExecutionException e) {
				crawler.setStatus(Status.FAILED);
				LOGGER.error(e);
				e.printStackTrace();
			}
		}
		LOGGER.info("Stop saving into DB ");
	}


	/*
	 * 5 threads will use 5 task so each has it's own pages and elements 1 - 4 threads are simple
	 * case each has the same amount of urls; The fifth might have less elements because we don't
	 * know how many elements on the last page;
	 */
	private List<Callable<List<Vacancy>>> getListOfLinksTaskRobota(List<Integer> listOfPages,
			String searchWord) {

		int indexOfLastPages = listOfPages.size() - 1;
		RabotaLinks taskLink;
		List<Callable<List<Vacancy>>> listTasksRobotaLinks = new ArrayList<>();
		for (int i = 0; i < listOfPages.size(); i++) {
			if (listOfPages.get(i) != 0) {
				if (i == 0) {
					taskLink = new RabotaLinks(1, listOfPages.get(i), searchWord, crawler.getId());
					listTasksRobotaLinks.add(taskLink);
				} else if (i == indexOfLastPages) {
					taskLink = new RabotaLinks(listOfPages.get(i - 1) * indexOfLastPages + 1,
							(listOfPages.get(i - 1) * indexOfLastPages) + listOfPages.get(i),
							searchWord, crawler.getId());
					listTasksRobotaLinks.add(taskLink);
				} else {
					taskLink = new RabotaLinks(listOfPages.get(i) * i + 1,
							listOfPages.get(i) * (i + 1), searchWord, crawler.getId());
					listTasksRobotaLinks.add(taskLink);
				}
			}
		}
		return listTasksRobotaLinks;
	}

	private List<Callable<List<Vacancy>>> getListOfDescriptionTaskRobota(
			List<Future<List<Vacancy>>> linksRobota)
			throws InterruptedException, ExecutionException {

		RabotaDescription taskRobotaDescription;
		List<Callable<List<Vacancy>>> listTaskRobotaDescription = new ArrayList<>();

		for (Future<List<Vacancy>> f : linksRobota) {
			taskRobotaDescription = new RabotaDescription(f.get());
			listTaskRobotaDescription.add(taskRobotaDescription);
		}
		return listTaskRobotaDescription;
	}


	private static void stop(ExecutorService executor) {
		try {
			executor.shutdown();
			executor.awaitTermination(360, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			System.err.println("termination interrupted");
		} finally {
			if (!executor.isTerminated()) {
				LOGGER.error("killing non-finished tasks");
			}
			executor.shutdownNow();
		}
	}

	private Crawler saveOrUpdateCrawler(Crawler crawler) {
		return crawlerRepository.save(crawler);
	}
}
