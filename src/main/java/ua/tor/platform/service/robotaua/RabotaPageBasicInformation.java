package ua.tor.platform.service.robotaua;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author alex
 *
 */
public class RabotaPageBasicInformation {

	private final String WEBLINK = "https://rabota.ua";
	private final String SELECTOR_FOR_AMOUT_OF_FOUND_VACANSIES =
			"#content_vacancyList_ltCount span.fd-fat-merchant ";
	private final String BACKSPACE = "&nbsp;";

	private final double ELEMENTS_PER_PAGE = 20;

	private int totalPages;
	private int threads;

	private String searchWord;
	private Document doc;
	private Elements vacansies;

	public RabotaPageBasicInformation(String serachWord, int threads) {
		this.threads = threads;
		this.searchWord = serachWord;
	}

	public List<Integer> getListOfPages() throws IOException {
		doc = Jsoup.connect(WEBLINK + "/ua/jobsearch/vacancy_list?keyWords=" + searchWord)
				.timeout(10 * 1000).get();

		vacansies = doc.select(SELECTOR_FOR_AMOUT_OF_FOUND_VACANSIES);
		double amountOfAllVacancies = Double.valueOf(vacansies.html().replaceAll(BACKSPACE, ""));
		totalPages = (int) getNumberOfPages(amountOfAllVacancies);
		return getPages(totalPages, threads);
	}

	private List<Integer> getPages(int pages, int threads) {
		int elementsPerOneThread = pages / threads;

		int elemntsForTheLastThread = elementsPerOneThread;

		if (pages % threads != 0) {
			elemntsForTheLastThread = pages - (elementsPerOneThread * (threads - 1));
		}
		List<Integer> listOfElements = new ArrayList<>();
		for (int i = 0; i < threads - 1; i++) {
			listOfElements.add(elementsPerOneThread);
		}
		listOfElements.add(elemntsForTheLastThread);

		return listOfElements;
	}

	private double getNumberOfPages(double openVacancies) {
		return Math.ceil(openVacancies / ELEMENTS_PER_PAGE);
	}
}
