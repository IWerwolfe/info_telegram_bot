package com.infoBot.bot;

import com.infoBot.bot.command.StartCommand;
import com.infoBot.bot.command.QuestionCommand;
import com.infoBot.config.Telegram;
import com.infoBot.model.Answer;
import com.infoBot.model.BotUser;
import com.infoBot.repository.AnswersRepository;
import com.infoBot.repository.BotUserRepository;
import com.infoBot.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InfoBot extends TelegramLongPollingCommandBot {
    private final BotUserRepository botUserRepository;
    private final AnswersRepository answersRepository;
    private final String regexSplitPattern = "(##\\s*.*?)([\\s\\S]*)";
    private final TextUtil textUtil;
    private final Sender sender;
    private final StartCommand startCommand;
    private final QuestionCommand questionCommand;

    private final String botUsername;
    private final Telegram telegram;

    @Autowired
    public InfoBot(
            List<IBotCommand> commandList,
            Telegram telegram,
            AnswersRepository answersRepository,
            BotUserRepository botUserRepository, TextUtil textUtil, Sender sender, StartCommand startCommand, QuestionCommand questionCommand) {

        super(telegram.getBot().getToken());
        this.telegram = telegram;
        this.botUsername = telegram.getBot().getUsername();
        this.textUtil = textUtil;
        this.sender = sender;
        this.startCommand = startCommand;
        this.questionCommand = questionCommand;

        commandList.forEach(this::register);
        this.answersRepository = answersRepository;
        this.botUserRepository = botUserRepository;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {

        if (update.hasMyChatMember()) {
            handlerUserStatus(update.getMyChatMember());
            return;
        }

        if (update.hasCallbackQuery()) {
            handlerCallbackQuery(update.getCallbackQuery());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            Message message = update.getMessage();
            BotUser user = getUser(message.getFrom());
            Long userId = update.getMessage().getChatId();

//            if (user.getUserType() == UserType.ADMIN && message.getText().matches(regexIsCommandMessage)) {
            if (message.getText().startsWith("##")) {
                addNewAnswer(message, userId);
            }
        }
    }

    private void addNewAnswer(Message message, Long userId) {

        String[] param = textUtil.splitText(message.getText());
        String question = textUtil.getDescriptor(param[0]);

        if (!question.isEmpty()) {
            Answer answer = new Answer(question, param[1]);
            answersRepository.save(answer);
            sender.sendBotMessage(this, "Ваш ответ упешно зарегистрирован", userId);
        }
    }

    private void handlerCallbackQuery(CallbackQuery callbackQuery) {

        String[] param = callbackQuery.getData().split(":");
        String text = null;

        if (param.length < 2) {
            text = "Произошла ошибка при обработке, перезапустите бот с помощью комманды /start и попробуйте еще раз";
            log.error("Incorrect command format {}", callbackQuery.getData());
            sender.sendBotMessage(this, text, callbackQuery.getMessage().getChatId());
            return;
        }

        switch (param[0]) {
            case "question" -> questionCommand.processMessage(this, callbackQuery.getMessage(), new String[]{param[1]});
            case "return" -> startCommand.processMessage(this, callbackQuery.getMessage(), null);
            default -> {
                log.error("Incorrect param to command {}", callbackQuery.getData());
                text = "Что то поменялось в команде, попробуйте еще раз";
            }
        }
        if (text != null) {
            sender.sendBotMessage(this, text, callbackQuery.getMessage().getChatId());
        }
    }

    private void handlerUserStatus(ChatMemberUpdated update) {

        ChatMember newMember = update.getNewChatMember();
        ChatMember oldMember = update.getOldChatMember();
        String status = newMember.getStatus();
        String oldStatus = oldMember.getStatus();
        String text;

        updateBotUser(update, status);

        if (status.equals("member")) {
            text = oldStatus.equals("kicked") ?
                    newMember.getUser().getUserName() + " с возвращением!" :
                    "Приветствуем " + newMember.getUser().getUserName() + ", нового воина орды";
            long id = update.getChat().getId();
            sender.sendBotMessage(this, text, id);
        }
    }

    private void updateBotUser(ChatMemberUpdated update, String status) {
        BotUser user = getUser(update.getFrom());
        user.setIsDeleted(status.equals("kicked"));
        botUserRepository.save(user);
    }

    private BotUser getUser(User from) {
        Optional<BotUser> optional = botUserRepository.findByTelegramId(from.getId());
        return optional.orElseGet(() -> new BotUser(from));
    }
}
