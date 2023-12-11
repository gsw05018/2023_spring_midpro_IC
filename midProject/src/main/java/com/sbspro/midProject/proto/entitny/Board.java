package com.sbspro.midProject.proto.entitny;

import com.sbspro.midProject.answer.entity.Answer;
import com.sbspro.midProject.base.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Column(columnDefinition = "Text")
    private String content;

    @Column(length = 20)
    private String title;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

}
