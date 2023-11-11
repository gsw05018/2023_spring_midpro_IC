package com.sbspro.midProject.proto.controller;

import com.sbspro.midProject.proto.entitny.Board;
import com.sbspro.midProject.proto.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    @GetMapping("board")
    public String board(Model model){
        List<Board> boardList = this.boardRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/board";
    }
}
