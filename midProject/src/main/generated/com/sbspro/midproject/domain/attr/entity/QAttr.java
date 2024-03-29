package com.sbspro.midproject.domain.attr.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAttr is a Querydsl query type for Attr
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttr extends EntityPathBase<Attr> {

    private static final long serialVersionUID = -524397244L;

    public static final QAttr attr = new QAttr("attr");

    public final com.sbspro.midproject.base.jpa.baseEntity.QBaseEntity _super = new com.sbspro.midproject.base.jpa.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final DateTimePath<java.time.LocalDateTime> expireDate = createDateTime("expireDate", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> relId = createNumber("relId", Long.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    public final StringPath type2Code = createString("type2Code");

    public final StringPath typeCode = createString("typeCode");

    public final StringPath val = createString("val");

    public QAttr(String variable) {
        super(Attr.class, forVariable(variable));
    }

    public QAttr(Path<? extends Attr> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAttr(PathMetadata metadata) {
        super(Attr.class, metadata);
    }

}

