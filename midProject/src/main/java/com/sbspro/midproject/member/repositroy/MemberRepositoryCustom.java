package com.sbspro.midproject.member.repositroy;

import com.sbspro.midproject.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findByKw(String kwType, String kw, Pageable pageable);
}