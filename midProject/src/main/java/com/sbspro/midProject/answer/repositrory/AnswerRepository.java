package com.sbspro.midProject.answer.repositrory;


import com.sbspro.midProject.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository  extends JpaRepository<Answer, Long> {
}
