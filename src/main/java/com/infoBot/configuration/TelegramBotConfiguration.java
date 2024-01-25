package com.infoBot.configuration;


import com.infoBot.bot.InfoBot;
import com.infoBot.config.SettingBot;
import com.infoBot.service.FileReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TelegramBotConfiguration {

    private final SettingBot setting;
    private final FileReaderService fileReaderService;

    @Bean
    TelegramBotsApi telegramBotsApi(InfoBot infoBot) {

        TelegramBotsApi botsApi = null;

        try {

            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(infoBot);
            runDownloadTaskFromFile();

            log.info("Telegram bot {} launched", infoBot.getBotUsername());

        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message to telegram!", e);
        }
        return botsApi;
    }

    private void runDownloadTaskFromFile() {

        if (!fileReaderService.pathInSettingCorrect()) {
            return;
        }

        if (setting.getTimeout() == 0) {
            setting.setTimeout(5);
        }

        Path path = fileReaderService.getFilePath();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> fileReaderService.downloadDataFromFile(path),
                1
                , setting.getTimeout()
                , TimeUnit.MINUTES);
    }
}
