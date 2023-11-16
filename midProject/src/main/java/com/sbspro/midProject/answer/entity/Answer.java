package com.sbspro.midProject.answer.entity;

import com.sbspro.midProject.base.entity.baseEntity;
import com.sbspro.midProject.proto.entitny.Board;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor
public class Answer extends baseEntity {

    private String content;

    @ManyToOne
    private Board board;


}