package com.infoBot.bot.command;

import com.infoBot.bot.InfoBot;
import com.infoBot.bot.Sender;
import com.infoBot.model.Answers;
import com.infoBot.repository.AnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HelpCommand implements IBotCommand {
    private final AnswersRepository answersRepository;

    @Override
    public String getCommandIdentifier() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Помощь";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        List<Answers> answersList = answersRepository.findAll();

        if (answersList.size() == 0) {
            Sender.sendBotMessage((InfoBot) absSender, "База данных не загружена", message.getChatId());
            return;
        }

        for (Answers answer : answersList) {
            String text = answer.getQuest() +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    answer.getAnswer();
            Sender.sendBotMessage((InfoBot) absSender, text, message.getChatId());
        }
    }
}