package com.sbspro.midProject.proto.entitny;

import com.sbspro.midProject.base.entity.baseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends baseEntity {

    @Column(columnDefinition = "Text")
    private String content;

    @Column(length = 20)
    private String title;

}
