package com.sbspro.midproject.domain.attr.repository;

import com.sbspro.midproject.domain.attr.entity.Attr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttrRepository extends JpaRepository<Attr, Long> {
    Optional<Attr> findByRelTypeCodeAndRelIdAndTypeCodeAndType2Code(String relTypeCode, long relId, String typeCode, String type2Code);
}
