package com.infoBot.model;    /*
 *created by WerWolfe on BotUser
 */

import com.infoBot.dto.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Clock;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name="bot_users")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(name="telegram_id")
    private Long telegramId;
    @Column(name="date_registration")
    private LocalDateTime dateRegistration;
    @Column(name="is_deleted")
    private Boolean isDeleted;
    @Column(name="user_type")
    private UserType userType;

    public BotUser() {
        this.dateRegistration = LocalDateTime.now(Clock.systemDefaultZone());
        this.isDeleted = false;
        this.userType = UserType.USER;
    }

    public BotUser(User from) {
        this();
        this.name = from.getUserName();
        this.telegramId = from.getId();
    }
}
