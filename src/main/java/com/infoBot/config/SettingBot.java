package com.infoBot.config;    /*
 *created by WerWolfe on Bot
 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "setting-bot")
public class SettingBot {

    private String pathInput;
    private String fileName;
    private long timeout;
}
