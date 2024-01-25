package com.infoBot.bot.command;

import com.infoBot.bot.InfoBot;
import com.infoBot.bot.Keyboards;
import com.infoBot.bot.Sender;
import com.infoBot.model.Answer;
import com.infoBot.repository.AnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionCommand implements IBotCommand {

    private final AnswersRepository answersRepository;
    private final Keyboards keyboards;
    private final Sender sender;

    @Override
    public String getCommandIdentifier() {
        return "question";
    }

    @Override
    public String getDescription() {
        return "Ответы на вопросы";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        int reply = message.getMessageId();
        long chatId = message.getChatId();

        if (arguments.length < 1) {
            sender.sendBotEditMessage((InfoBot) absSender, "Некорректный параметр команды", chatId, reply);
            return;
        }

        String text = getAnswerById(Long.parseLong(arguments[0]));
        InlineKeyboardMarkup keyboard = keyboards.getBackButton();
        sender.sendBotEditMessage((InfoBot) absSender, text, keyboard, chatId, reply);
    }

    private String getAnswerById(long id) {

        Optional<Answer> optional = answersRepository.findById(id);

        if (optional.isEmpty()) {
            log.error("Data not found. Entity {}, id {}", Answer.class.getName(), id);
            return "У нас, к сожалению, нет ответа на ваш вопрос";
        }
        return optional.get().getAnswer();
    }
}