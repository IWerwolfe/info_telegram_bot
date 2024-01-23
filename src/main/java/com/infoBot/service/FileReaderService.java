package com.infoBot.service;    /*
 *created by WerWolfe on FileReaderService
 */

import com.infoBot.config.SettingBot;
import com.infoBot.model.Answers;
import com.infoBot.repository.AnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileReaderService {

    private final AnswersRepository answersRepository;

    private final SettingBot setting;
    private String filePath;

    public boolean pathInSettingCorrect() {

        if (setting.getFileName() == null || setting.getPathInput() == null) {
            return false;
        }

        if (setting.getFileName().isEmpty() || setting.getPathInput().isEmpty()) {
            return false;
        }

        return true;
    }


    public String getFilePath() {
        return "." + (setting.getPathInput().matches(".*/") ?
                setting.getPathInput() + setting.getFileName() :
                setting.getPathInput() + "/" + setting.getFileName());
    }

    public void downloadDataFromFile() {

        filePath = getFilePath();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            List<Answers> answersList = readfile(reader);

            if (!answersList.isEmpty()) {
                answersRepository.saveAll(answersList);
            }

            Files.delete(path);
            log.info("The data from the file has been successfully uploaded to the database");

        } catch (MalformedInputException e) {
            log.debug("-- Unknown file encoding {} {}", filePath, LocalDateTime.now());
        } catch (IOException e) {
            log.debug("-- The file could not be read {} {}", filePath, LocalDateTime.now());
            log.error(e.getMessage());
        }
    }

    private List<Answers> readfile(BufferedReader reader) throws IOException {

        String line;
        String questText = "";
        List<Answers> answersList = new ArrayList<>();
        StringBuilder answersText = new StringBuilder();

        while ((line = reader.readLine()) != null) {

            if (line.matches("#.*")) {

                if (!questText.trim().isEmpty()) {
                    answersList.add(new Answers(questText, answersText.toString()));
                }

                questText = line;
                answersText = new StringBuilder();
                continue;
            }
            answersText.append(line);
        }

        return answersList;
    }

}
