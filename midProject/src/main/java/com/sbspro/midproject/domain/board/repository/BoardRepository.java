package com.sbspro.midproject.domain.board.repository;

import com.sbspro.midproject.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByCode(String boardCode);
}
