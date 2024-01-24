package com.sbspro.midproject.proto.entitny;

import com.sbspro.midproject.base.jpa.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

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

}
