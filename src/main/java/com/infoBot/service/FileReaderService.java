package com.infoBot.service;    /*
 *created by WerWolfe on FileReaderService
 */

import com.infoBot.config.SettingBot;
import com.infoBot.model.Answer;
import com.infoBot.repository.AnswersRepository;
import com.infoBot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
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
    private final TextUtil textUtil;

    public boolean pathInSettingCorrect() {

        if (setting.getFileName() == null || setting.getPathInput() == null) {
            return false;
        }

        if (setting.getFileName().isEmpty() || setting.getPathInput().isEmpty()) {
            return false;
        }

        return true;
    }

    public String getPathString() {
        return "." + (setting.getPathInput().matches(".*/") ?
                setting.getPathInput() + setting.getFileName() :
                setting.getPathInput() + "/" + setting.getFileName());
    }

    public Path getFilePath() {
        return Paths.get(getPathString());
    }

    public void downloadDataFromFile(Path path) {

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            List<Answer> answerList = readfile(reader);

            if (!answerList.isEmpty()) {
                answersRepository.saveAll(answerList);
            }

            Files.delete(path);
            log.info("The data from the file has been successfully uploaded to the database");

        } catch (MalformedInputException e) {
            log.error("-- Unknown file encoding {} {}", path.toAbsolutePath(), LocalDateTime.now());
        } catch (IOException e) {
            log.debug("-- The file could not be read {} {}", path.toAbsolutePath(), LocalDateTime.now());
            log.error(e.getMessage());
        }
    }

    private List<Answer> readfile(BufferedReader reader) throws IOException {

        String line;
        String questText = "";
        List<Answer> answerList = new ArrayList<>();
        StringBuilder answersText = new StringBuilder();

        while ((line = reader.readLine()) != null) {

            if (line.startsWith("##")) {

                if (!questText.isEmpty()) {
                    answerList.add(new Answer(questText, answersText.toString()));
                }

                questText = textUtil.getDescriptor(line);
                answersText = new StringBuilder();
                continue;
            }
            answersText.append(line);
        }

        return answerList;
    }

}
