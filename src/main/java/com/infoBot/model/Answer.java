package com.infoBot.model;    /*
 *created by WerWolfe on Answer
 */

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name="answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, columnDefinition = "VARCHAR")
    private String quest;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    public Answer(String quest) {
        this.quest = quest;
    }

    public Answer(String quest, String answer) {
        this.quest = quest;
        this.answer = answer;
    }
}
