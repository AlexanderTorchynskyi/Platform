package ua.tor.platform.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import ua.tor.platform.model.Crawler;
import ua.tor.platform.model.ParsedVacancy;
import ua.tor.platform.repository.ICrawlerRepository;
import ua.tor.platform.repository.IParsedVacancyRepository;
import ua.tor.platform.utils.SheetServiceUtil;

/**
 * 
 * @author alex
 *
 */
@Component
public class IncrementorService {

	private static final Logger LOGGER = Logger.getLogger(IncrementorService.class);

	private static final String SAMPLE_CSV_FILE = "dump.csv";
	private static final String SPREAD_SHEET_ID = "14ccZZYNg1o6nC-rSgc7orW2jybve0U8SzPVrwqJzEo0";
	private static final Integer DEFAULT_WEIGHT = 1;
	private static final String RANGE = "Skills!A5";

	@Autowired
	private IParsedVacancyRepository parsedVacancy;
	@Autowired
	private ICrawlerRepository crawlerRepository;

	private ObjectId crawlerId;
	private String dumpId;

	private Map<String, Integer> qntConter;
	private List<ParsedVacancy> listOfParsedVacancies;

	/**
	 * Method for getting skill and it's quantity;
	 * 
	 * @param crawlerId requested param for getting batch;
	 * @return map of skill and it's quantity;
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public Map<String, Integer> getVacancies(ObjectId crawlerId)
			throws IOException, GeneralSecurityException {
		this.crawlerId = crawlerId;
		LOGGER.info("Getting vacancies with crawlerid" + crawlerId);
		listOfParsedVacancies = parsedVacancy.findByCrawlerId(crawlerId);
		return getCollection(listOfParsedVacancies);
	}

	private Map<String, Integer> getCollection(List<ParsedVacancy> lsOfVacancies)
			throws IOException, GeneralSecurityException {
		qntConter = new HashMap<>();
		LOGGER.info("Counting amount of repeated words");
		for (ParsedVacancy words : lsOfVacancies) {
			for (String word : words.getDescription()) {
				if (qntConter.containsKey(word)) {
					qntConter.put(word, qntConter.get(word) + 1);
				} else {
					qntConter.put(word, DEFAULT_WEIGHT);
				}
			}
		}
		return sortMap(qntConter);
	}

	private Map<String, Integer> sortMap(Map<String, Integer> map)
			throws IOException, GeneralSecurityException {
		LOGGER.info("Sort map in descending order by quantity");
		Map<String, Integer> result = map.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		writeToCsv(result);
		// TODO need to provide some extra configs for that method
		// writeToGoogleSheets(result);
		return result;
	}

	private void writeToCsv(Map<String, Integer> map) {
		this.dumpId = crawlerId.toString() + "_";
		LOGGER.info("Creating dump csv with name " + dumpId + SAMPLE_CSV_FILE);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(dumpId + SAMPLE_CSV_FILE));
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("Skill", "Quantity"));) {
			csvPrinter.printRecord(getSkillByCrawlerId(crawlerId),
					getAmountOfParsedVacancyByCrawlerId(crawlerId));
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				csvPrinter.printRecord(entry.getKey(), entry.getValue());
			}
			csvPrinter.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void writeToGoogleSheets(Map<String, Integer> map)
			throws IOException, GeneralSecurityException {
		LOGGER.info("Writting to google dock the id for spreadsheet " + SPREAD_SHEET_ID);
		Sheets sheetsService = SheetServiceUtil.getSheetsService();

		ClearValuesRequest requestBody = new ClearValuesRequest();
		Sheets.Spreadsheets.Values.Clear request = sheetsService.spreadsheets().values()
				.clear(SPREAD_SHEET_ID, "A1:Z50000", requestBody);

		request.execute();

		List<List<Object>> valuesForCells = new ArrayList<List<Object>>();
		valuesForCells.add(Arrays.asList(getSkillByCrawlerId(crawlerId),
				getAmountOfParsedVacancyByCrawlerId(crawlerId)));
		valuesForCells.add(Arrays.asList("Skills", "Quantity"));
		ValueRange body = new ValueRange();

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			valuesForCells.add(new ArrayList<>(Arrays.asList(entry.getKey(), entry.getValue())));
		}
		body.setValues(valuesForCells);
		sheetsService.spreadsheets().values().update(SPREAD_SHEET_ID, RANGE, body)
				.setValueInputOption("RAW").execute();
	}

	private String getSkillByCrawlerId(ObjectId crawlerId) {
		Crawler crawler = crawlerRepository.findOneById(crawlerId);
		return crawler.getSearchCondition();
	}

	private long getAmountOfParsedVacancyByCrawlerId(ObjectId crawlerId) {
		return parsedVacancy.countByCrawlerId(crawlerId);
	}
}
