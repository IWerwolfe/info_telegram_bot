package com.infoBot.bot.command;

import com.infoBot.bot.InfoBot;
import com.infoBot.bot.Keyboards;
import com.infoBot.bot.Sender;
import com.infoBot.model.Answer;
import com.infoBot.model.BotUser;
import com.infoBot.repository.AnswersRepository;
import com.infoBot.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartCommand implements IBotCommand {
    private final AnswersRepository answersRepository;

    private final BotUserRepository userRepository;
    private final Sender sender;
    private final Keyboards keyboards;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        long chatId = message.getChatId();
        int reply = message.getFrom().getIsBot() ? message.getMessageId() : 0;

        BotUser user = getUser(message.getFrom());

        if (user.getId() == null) {
            userRepository.save(user);
        }

        String text = user.getName() + ", привет!";
        List<Answer> answerList = answersRepository.findAll();

        text = text + (answerList.isEmpty() ? """
                                
                Справка еще не загрузилась, попробуйте немного позже.""" :
                """
                                        
                        На самые частые вопросы мы собрали справку. Просто нажми на кнопку, чтобы получить исчерпывающий ответ: 
                        """);

        InlineKeyboardMarkup keyboard = keyboards.getInlineMarkupByTasks(answerList);
        sender.sendBotEditMessage((InfoBot) absSender, text, keyboard, chatId, reply);
    }

    private BotUser getUser(User from) {
        Optional<BotUser> optional = userRepository.findByTelegramId(from.getId());
        return optional.orElseGet(() -> new BotUser(from));
    }

}