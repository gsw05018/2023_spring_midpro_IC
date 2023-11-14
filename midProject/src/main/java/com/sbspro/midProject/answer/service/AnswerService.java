package com.sbspro.midProject.answer.service;

import com.sbspro.midProject.answer.entity.Answer;
import com.sbspro.midProject.answer.repositrory.AnswerRepository;
import com.sbspro.midProject.proto.entitny.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Board board, String content){

        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setBoard(board);
        this.answerRepository.save(answer);
    }
}
