package com.sbspro.midProject.member.service;

import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.repositroy.MemberRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepositroy memberRepositroy;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Member join(String username, String password, String nickname, String email, String phoneNumber) {
        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .phone_number(phoneNumber)
                .build();

        return memberRepositroy.save(member);
    }

    public Optional<Member> findByUsername(String username){
        return memberRepositroy.findByUsername(username);
    }

    public Optional<Member> findById(Long id){
        return memberRepositroy.findById(id);
    }
}
