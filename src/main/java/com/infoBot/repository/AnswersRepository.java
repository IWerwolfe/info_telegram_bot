package com.infoBot.repository;

import com.infoBot.model.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AnswersRepository extends JpaRepository<Answers, Long> {


}