package com.infoBot.config;    /*
 *created by WerWolfe on Telegram
 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "telegram")
public class Telegram {
    private Bot bot;

}
