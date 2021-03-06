package ua.tor.platform.service.robotaua;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ua.tor.platform.persistent.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 
 * @author alex
 *
 */
public class RabotaDescription implements Callable<List<Vacancy>> {
	private static final Logger log = Logger.getLogger(RabotaDescription.class);

	private final String SELECTOR_FOR_DESCRIPTION_TYPE_FANCY = "div.f-vacancy-description";
	private final String SELECTOR_FOR_DESCRIPTION_TYPE_REGULAR = "div.d_des";

	private Document doc;
	private Vacancy vacancy;

	private List<Vacancy> urls;
	private List<Vacancy> listOfVacancies;

	public RabotaDescription(List<Vacancy> urls) {
		this.urls = urls;;
	}

	@Override
	public List<Vacancy> call() {
		listOfVacancies = new ArrayList<>();

		for (int i = 0; i < urls.size(); i++) {
			vacancy = urls.get(i);
			try {
				doc = Jsoup.connect(urls.get(i).getLink()).timeout(10 * 10000)
						.ignoreHttpErrors(true).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Elements vacancyBody = doc.select(SELECTOR_FOR_DESCRIPTION_TYPE_FANCY);
			vacancy.setLink(urls.get(i).getLink());
			vacancy.setTitle(doc.title());
			log.info(Thread.currentThread().getName());
			if (!vacancyBody.text().isEmpty()) {
				vacancy.setDescription(vacancyBody.text());
				log.info(vacancyBody.text());
			} else {
				vacancyBody = doc.select(SELECTOR_FOR_DESCRIPTION_TYPE_REGULAR);
				vacancy.setDescription(vacancyBody.text());

				log.info(vacancyBody.text());
			}
			listOfVacancies.add(vacancy);
		}
		return listOfVacancies;
	}
}
