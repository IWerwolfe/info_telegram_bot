package com.infoBot.bot;

import com.infoBot.config.Telegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;


@Service
@Slf4j
public class InfoBot extends TelegramLongPollingCommandBot {

    private final String botUsername;
    private final Telegram telegram;

    public InfoBot(
            List<IBotCommand> commandList,
            Telegram telegram) {

        super(telegram.getBot().getToken());
        this.telegram = telegram;
        this.botUsername = telegram.getBot().getUsername();

        commandList.forEach(this::register);
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

        String text = """
                Я не умею общаться в свободном стиле, лучше отправь мне команду из этого списка:
                                
                /start - начать работу
                """;

        Long userId = update.getMessage().getChatId();
        Sender.sendBotMessage(this, text, userId);
    }

    private void handlerUserStatus(ChatMemberUpdated update) {

        ChatMember newMember = update.getNewChatMember();
        ChatMember oldMember = update.getOldChatMember();
        String status = newMember.getStatus();
        String oldStatus = oldMember.getStatus();
        String text;

        if (status.equals("member")) {

            text = oldStatus.equals("kicked") ?
                    newMember.getUser().getUserName() + " с возвращением!" :
                    "Приветствуем " + newMember.getUser().getUserName() + ", нового воина орды";
            long id = update.getChat().getId();
            Sender.sendBotMessage(this, text, id);
        }
    }
}
