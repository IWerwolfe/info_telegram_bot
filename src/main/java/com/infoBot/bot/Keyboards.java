package com.infoBot.bot;    /*
 *created by WerWolfe on Keyboards
 */

import com.infoBot.model.Answer;
import com.infoBot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Keyboards {

    private final TextUtil textUtil;

    public InlineKeyboardMarkup getInlineMarkupByTasks(List<Answer> answerList) {

        List<List<InlineKeyboardButton>> rowsInLine = answerList.stream()
                .map(answer -> {
                    String name = answer.getQuest();
                    String command = "question:" + answer.getId();
                    return getInlineKeyboardButton(name, command);
                })
                .toList();

        return getInlineKeyboardMarkup(rowsInLine);
    }

    public InlineKeyboardMarkup getBackButton() {

        String name = "<- Вернуться";
        String command = "return:0";
        List<InlineKeyboardButton> button = getInlineKeyboardButton(name, command);

        return getInlineKeyboardMarkup(List.of(button));
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<List<InlineKeyboardButton>> rows) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rows);
        return markupInline;
    }

    private List<InlineKeyboardButton> getInlineKeyboardButton(String label, String command) {

        InlineKeyboardButton taskButton = new InlineKeyboardButton(label);
        taskButton.setCallbackData(command);
        return List.of(taskButton);
    }
}
