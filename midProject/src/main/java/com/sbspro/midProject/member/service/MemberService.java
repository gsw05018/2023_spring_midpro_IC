package com.sbspro.midProject.member.service;

import com.sbspro.midProject.base.rsData.RsData.RsData;
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
    public RsData<Member> join(String username, String password, String nickname, String email, String phoneNumber) {
        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .phone_number(phoneNumber)
                .build();


        member =  memberRepositroy.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다", member);
    }

    public Optional<Member> findByUsername(String username){
        return memberRepositroy.findByUsername(username);
    }

    public Optional<Member> findById(Long id){
        return memberRepositroy.findById(id);
    }
}