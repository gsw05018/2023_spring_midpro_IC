package com.sbspro.midProject.proto.entitny;

import com.sbspro.midProject.common.baseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends baseEntity {

    @Column(columnDefinition = "Text")
    private String content;

    @Column(length = 20)
    private String title;

}
