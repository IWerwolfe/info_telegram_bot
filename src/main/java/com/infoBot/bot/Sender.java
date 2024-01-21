package com.infoBot.bot;    /*
 *created by WerWolfe on Sender
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodBoolean;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
@Component
public class Sender {

    public static void sendBotMessage(InfoBot absSender, SendMessage message) {
        sendMessage(absSender, message);
    }

    public static void sendErrorMessage(InfoBot absSender, SendMessage message) {
        sendMessage(absSender, message, true);
    }

    public static void sendBotMessage(InfoBot absSender, String message, long chatId) {
        sendBotMessage(absSender, message, null, chatId);
    }

    public static void sendBotMessage(InfoBot absSender, String message, ReplyKeyboard keyboard, long chatId) {

        if (message != null && message.trim().isEmpty()) {
            log.error("Attempt to send an empty message from a user with an ID {}", chatId);
            return;
        }
        SendMessage sendMessage = createMessage(chatId, message, keyboard);
        sendBotMessage(absSender, sendMessage);
    }

    private static SendMessage createMessage(long chatId, String text, ReplyKeyboard keyboard) {

        if (text.length() > 4095) {
            text = text.substring(0, 4092) + "...";
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboard);
        return message;
    }

    private static <T extends Serializable> int sendMessage(InfoBot absSender, BotApiMethod<T> message) {
        return sendMessage(absSender, message, false);
    }

    private static  <T extends Serializable> int sendMessage(InfoBot absSender, BotApiMethod<T> message, boolean notHandleError) {
        try {

            if (message instanceof BotApiMethodMessage method) {
                return absSender.execute(method).getMessageId();
            }
            if (message instanceof BotApiMethodBoolean method) {
                absSender.execute(method);
            }

            return 0;

        } catch (TelegramApiException e) {

            if (notHandleError) {
                log.error(e.getMessage());
            } else {
//                absSender.handleAnException(e.getMessage());
            }

            return -1;
        }
    }
}
