package ua.tor.platform.service;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ua.tor.platform.persistent.Crawler;
import ua.tor.platform.persistent.Status;
import ua.tor.platform.persistent.Vacancy;
import ua.tor.platform.repository.ICrawlerRepository;
import ua.tor.platform.service.robotaua.RabotaDescription;
import ua.tor.platform.service.robotaua.RabotaLinks;
import ua.tor.platform.service.robotaua.RabotaPageBasicInformation;
import ua.tor.platform.web.api.dto.CrawlerRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author alex
 */
@Component
public class CrawlerService {

    private static final Logger LOGGER = Logger.getLogger(CrawlerService.class);
    private static final int AMOUT_OF_THREADS = 8;

    private final VacancyService vacancyRepository;
    private final ICrawlerRepository crawlerRepository;

    private Crawler crawler;

    public CrawlerService(VacancyService vacancyRepository, ICrawlerRepository crawlerRepository) {
        this.vacancyRepository = vacancyRepository;
        this.crawlerRepository = crawlerRepository;
    }

    public List<Crawler> findAllBySearchCondition(String searchword) {
        return crawlerRepository.findAllBySearchCondition(searchword);
    }

    public ObjectId startCrawling(CrawlerRequest crawlerRequest)
            throws IOException, InterruptedException, ExecutionException {

        StopWatch s = new StopWatch();
        crawler = new Crawler();
        crawler.setSearchCondition(crawlerRequest.getSearch());
        crawler.setStatus(Status.NEW);
        crawler.setCreatedDate(LocalDate.now());
        crawler.setModifiedDate(LocalDate.now());

        crawler = saveOrUpdateCrawler(crawler);

        LOGGER.info("startet Crawling with params: searchWord " + crawlerRequest.getSearch() + " amount of threads "
                + AMOUT_OF_THREADS);

        s.start();

        RabotaPageBasicInformation robota = new RabotaPageBasicInformation(crawlerRequest.getSearch(), AMOUT_OF_THREADS);
        crawler.setStatus(Status.IN_PROCESS);
        saveOrUpdateCrawler(crawler);
        List<Integer> listOfPagesRobota = robota.getListOfPages();
        ExecutorService ex = Executors.newWorkStealingPool();

        List<Future<List<Vacancy>>> linksRobota =
                ex.invokeAll(getListOfLinksTaskRobota(listOfPagesRobota, crawlerRequest.getSearch()));

        List<Callable<List<Vacancy>>> listOfTasksAll =
                new ArrayList<Callable<List<Vacancy>>>(getListOfDescriptionTaskRobota(linksRobota));

        List<Future<List<Vacancy>>> listOfVacancies = ex.invokeAll(listOfTasksAll);

        stop(ex);

        saveIntoDB(listOfVacancies);
        s.stop();
        crawler.setStatus(Status.PROCESSED);
        saveOrUpdateCrawler(crawler);

        LOGGER.info("Total time in seconds " + s.getTime(TimeUnit.SECONDS));
        return crawler.getId();
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
     * Method for creating tasks for each thread and dividing pages for crawling per each task. The
     * logic is that {@amountOfThreads - 1} threads will have the same amount of pages; and the last
     * thread will have amountOfPages: { amountOfPagesForTheLastThread = amountOfPages -
     * (amountOfPages / (amountOfThreads-1)))}
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

    public Crawler findOneById(ObjectId crawlerId) {
        return crawlerRepository.findById(crawlerId).get();
    }
}
