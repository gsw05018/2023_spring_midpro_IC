package com.sbspro.midProject.answer.controller;

import com.sbspro.midProject.answer.service.AnswerService;
import com.sbspro.midProject.proto.entitny.Board;
import com.sbspro.midProject.proto.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final BoardService boardService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id, @RequestParam String content){
        Board board = this.boardService.getBoard(id);
        this.answerService.create(board,content);
        return "redirect:/reply_board";
    }

}
