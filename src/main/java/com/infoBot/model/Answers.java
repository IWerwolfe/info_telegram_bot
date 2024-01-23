package com.infoBot.model;    /*
 *created by WerWolfe on Answers
 */

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity(name="answer")
public class Answers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, columnDefinition = "VARCHAR")
    private String quest;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    public Answers(String quest) {
        this.quest = quest;
    }

    public Answers(String quest, String answer) {
        this.quest = quest;
        this.answer = answer;
    }
}
