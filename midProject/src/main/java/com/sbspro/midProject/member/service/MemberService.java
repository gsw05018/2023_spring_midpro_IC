package com.sbspro.midProject.member.service;

import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.domain.genFile.service.GenFileService;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.repositroy.MemberRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepositroy memberRepositroy;
    private final PasswordEncoder passwordEncoder;
    private final GenFileService genFileService;


    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String email, String phoneNumber, MultipartFile profileImg) {

        if (findByUsername(username).isPresent())
            return RsData.of("F-1", "%s(은)는 사용중인 아이디입니다.".formatted(username));

            Member member = Member
                    .builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .nickname(nickname)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .build();

            member = memberRepositroy.save(member);

        if(profileImg != null){
            genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 0, profileImg);
        }

            return RsData.of("S-1", "회원가입이 완료되었습니다", member);
        }

        public Optional<Member> findByUsername (String username){
            return memberRepositroy.findByUsername(username);
        }

        public Optional<Member> findById (Long id){
            return memberRepositroy.findById(id);
        }

    public RsData checkUsernameDup(String username) {
        if(findByUsername(username).isPresent()) return RsData.of("F-1", "%s는 사용중인 아이디입니다.".formatted(username));

        return RsData.of("S-1", "%s는 사용 가능한 아이디입니다.".formatted(username));
    }
}
