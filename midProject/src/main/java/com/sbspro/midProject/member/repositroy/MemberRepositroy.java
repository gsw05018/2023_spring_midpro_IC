package com.sbspro.midProject.member.repositroy;

import com.sbspro.midProject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepositroy extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

}
