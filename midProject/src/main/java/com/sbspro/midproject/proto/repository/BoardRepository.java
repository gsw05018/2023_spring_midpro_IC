package com.sbspro.midproject.proto.repository;

import com.sbspro.midproject.proto.entitny.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
