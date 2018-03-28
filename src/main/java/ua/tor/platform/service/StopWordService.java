package ua.tor.platform.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.tor.platform.model.StopWord;
import ua.tor.platform.repository.IStopWordRepository;

/**
 * 
 * @author alex
 *
 */
@Component
public class StopWordService {

	private static final Logger LOGGER = Logger.getLogger(StopWordService.class);
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	@Autowired
	private IStopWordRepository stopWordRepository;
	private List<StopWord> stopWords;
	private StopWord stopWord;

	public void loadStopWords() {
		try {
			insertStopWords(readFromInputStream("stop_words.txt"));
		} catch (IOException e) {
			LOGGER.error("Exception with openning file ");
			e.printStackTrace();
		}
	}

	private void insertStopWords(List<StopWord> stopWords) {
		stopWordRepository.insert(stopWords);
	}

	private List<StopWord> readFromInputStream(String aFileName) throws IOException {
		stopWord = new StopWord();
		Path path = Paths.get(aFileName);
		stopWords = new ArrayList<>();
		String str;

		try (Scanner scanner = new Scanner(path, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				str = scanner.nextLine();
				str = str.replaceAll("[^a-zA-Z0-9'-]", "");
				if (!str.isEmpty()) {
					stopWord = new StopWord();
					stopWord.setKey(str);
					stopWord.setCreatedDate(new Date());
					stopWord.setModifiedDate(new Date());
					stopWords.add(stopWord);
					LOGGER.info(str);
				}
			}
		}
		return stopWords;
	}
}
