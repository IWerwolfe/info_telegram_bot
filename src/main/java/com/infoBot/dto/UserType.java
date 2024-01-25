package com.infoBot.dto;    /*
 *created by WerWolfe on UserType
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {

    USER("Пользователь"),
    ADMIN("Администратор");

    private String label;
}
