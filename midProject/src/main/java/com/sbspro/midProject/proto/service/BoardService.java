package com.sbspro.midProject.proto.service;

import com.sbspro.midProject.base.common.DataNotFoundException;
import com.sbspro.midProject.proto.entitny.Board;
import com.sbspro.midProject.proto.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Board getBoard(Long id) {
        Optional<Board> board = this.boardRepository.findById(id);
        if (board.isPresent()) {
            return board.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public List<Board> getList(){
        return this.boardRepository.findAll();
    }



}
