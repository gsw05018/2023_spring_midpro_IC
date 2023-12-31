package com.sbspro.midProject.member.service;

import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.domain.email.service.EmailService;
import com.sbspro.midProject.domain.genFile.entity.GenFile;
import com.sbspro.midProject.domain.genFile.service.GenFileService;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.repositroy.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepositroy;
    private final PasswordEncoder passwordEncoder;
    private final GenFileService genFileService;
    private final EmailService emailService;


    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String email,  MultipartFile profileImg) {

        if (findByUsername(username).isPresent())
            return RsData.of("F-1", "%s(은)는 사용중인 아이디입니다.".formatted(username));

        if (findByEmail(email).isPresent())
            return RsData.of("F-2", "%s(은)는 사용중인 이메일입니다.".formatted(email));

        Member member = Member
                    .builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .nickname(nickname)
                    .email(email)
                    .build();

            member = memberRepositroy.save(member);

        if(profileImg != null){
            genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 0, profileImg);
        }

        sendJoinCompleteMail(member);

            return RsData.of("S-1", "회원가입이 완료되었습니다", member);
        }

    public Optional<Member> findByUsername (String username){
            return memberRepositroy.findByUsername(username);
        }

    public Optional<Member> findByEmail (String email){
        return memberRepositroy.findByEmail(email);
    }

    public Optional<Member> findById (Long id){
            return memberRepositroy.findById(id);
        }

    public RsData checkUsernameDup(String username) {
        if(findByUsername(username).isPresent()) return RsData.of("F-1", "%s는 사용중인 아이디입니다.".formatted(username));

        return RsData.of("S-1", "%s는 사용 가능한 아이디입니다.".formatted(username));
    }

    private void sendJoinCompleteMail(Member member) {
        emailService.send(member.getEmail(),"회원가입이 완료되었습니다.", "회원가입이 완료되었습니다.");
    }

    public RsData<String> checkEmailDup(String email) {
        if(findByEmail(email).isPresent()) return RsData.of("F-1", "%s는 사용중인 이메일입니다.".formatted(email));

        return RsData.of("S-1", "%s는 사용 가능한 이메일입니다.".formatted(email));
    }

    public Optional<String> findProfileImgUrl(Member member){
        return genFileService.findGenFileBy(
                member.getModelName(), member.getId(), "common", "profileImg", 0
        )
                .map(GenFile::getUrl);
    }

}
