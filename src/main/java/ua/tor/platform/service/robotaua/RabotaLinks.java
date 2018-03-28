package ua.tor.platform.service.robotaua;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.tor.platform.model.Status;
import ua.tor.platform.model.Vacancy;


/**
 * 
 * @author alex
 *
 */
public class RabotaLinks implements Callable<List<Vacancy>> {
	
	private static final Logger LOGGER = Logger.getLogger(RabotaLinks.class);

	private final String WEBLINK = "https://rabota.ua";
	private final String SELECTOR_FOR_LNKS = "h3.fd-beefy-gunso a";
	private final String SELECTOR_FOR_COMAPNY =
			"p.f-vacancylist-companyname.fd-merchant.f-text-dark-bluegray";
	private final String SELECTOR_FOR_CITY =
			"div.fd-f1 div.f-vacancylist-characs-block.fd-f-left-middle p.fd-merchant";
	private final String REF = "href";

	private int startPage;
	private int finishPage;
	private String searchWord;

	private Document doc;
	private String city;
	private String company;
	private Vacancy vacancy;
	private ObjectId crawlerId;

	private List<String> links;
	private List<String> cities;
	private List<Vacancy> listOfVacancies;

	public RabotaLinks(int startPage, int finishPage, String searchWord, ObjectId crawlerId) {
		this.startPage = startPage;
		this.finishPage = finishPage;
		this.searchWord = searchWord;
		this.crawlerId = crawlerId;
	}

	@Override
	public List<Vacancy> call() throws Exception {

		links = new ArrayList<>();
		listOfVacancies = new ArrayList<>();

		for (int i = startPage; i <= finishPage; i++) {
			// doc = Jsoup.connect("https://rabota.ua/jobsearch/vacancy_list?pg=" + i)
			// .timeout(10 * 10000).get();

			doc = Jsoup
					.connect(
							WEBLINK + "/jobsearch/vacancy_list?keyWords=" + searchWord + "&pg=" + i)
					.timeout(10 * 1000).get();

			Elements allLinksFromPage = doc.select(SELECTOR_FOR_LNKS);

			Elements elementsForCompany = doc.select(SELECTOR_FOR_COMAPNY);

			Elements elementsForCity = doc.select(SELECTOR_FOR_CITY);

			cities = new ArrayList<>();

			for (Element e : elementsForCity) {
				if (!e.ownText().isEmpty()) {
					cities.add(e.ownText());
				}
			}

			for (int j = 0; j < allLinksFromPage.size(); j++) {

				links.add(WEBLINK + allLinksFromPage.get(j).attr(REF));
				vacancy = new Vacancy();
				city = cities.get(j).toLowerCase().trim();
				company = elementsForCompany.get(j).text().toLowerCase().trim();
				vacancy.setCompany(company);
				vacancy.setCity(city);
				vacancy.setLink(WEBLINK + allLinksFromPage.get(j).attr(REF));
				vacancy.setStatus(Status.NEW);
				vacancy.setCrawlerId(crawlerId);
				listOfVacancies.add(vacancy);

				LOGGER.info(allLinksFromPage.get(j).attr(REF));
				LOGGER.info(Thread.currentThread().getName());
			}
		}
		return listOfVacancies;
	}
}
