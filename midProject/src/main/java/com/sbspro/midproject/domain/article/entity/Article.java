package com.sbspro.midproject.domain.article.entity;

import com.sbspro.midproject.base.jpa.baseEntity.BaseEntity;
import com.sbspro.midproject.domain.board.entity.Board;
import com.sbspro.midproject.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Article extends BaseEntity {
    @ManyToOne
    private Member author;

    @ManyToOne
    private Board board;

    private String subject;
    @Column(columnDefinition = "TEXT")
    private String body;
}