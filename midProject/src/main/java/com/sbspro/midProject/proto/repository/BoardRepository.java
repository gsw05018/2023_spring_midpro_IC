package com.sbspro.midProject.proto.repository;

import com.sbspro.midProject.proto.entitny.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
