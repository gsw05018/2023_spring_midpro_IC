package com.sbspro.midProject.member.service;

import com.sbspro.midProject.base.app.AppConfig;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.domain.attr.service.AttrService;
import com.sbspro.midProject.domain.email.service.EmailService;
import com.sbspro.midProject.domain.emailVerification.service.EmailVerificationService;
import com.sbspro.midProject.domain.genFile.entity.GenFile;
import com.sbspro.midProject.domain.genFile.service.GenFileService;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.repositroy.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenFileService genFileService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final AttrService attrService;


    public Optional<Member> findByUsername (String username){
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByEmail (String email){
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findById (Long id){
        return memberRepository.findById(id);
    }

    public RsData checkUsernameDup(String username) {
        if(findByUsername(username).isPresent()) return RsData.of("F-1", "%s는 사용중인 아이디입니다.".formatted(username));

        return RsData.of("S-1", "%s는 사용 가능한 아이디입니다.".formatted(username));
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

            memberRepository.save(member);

        if(profileImg != null) saveProfileImg(member, profileImg);


        sendJoinCompleteEmail(member);
        sendEmailVerificationEmail(member);

            return RsData.of("S-1", "회원가입이 완료되었습니다", member);
        }

    private void saveProfileImg(Member member, MultipartFile profileImg){

        genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 0, profileImg);

    }

    private void sendJoinCompleteEmail(Member member) {
        CompletableFuture<RsData> sendRsFuture = emailService.send(
                member.getEmail(),
                "[%s 가입축하 ] 회원가입이 완료되었습니다.".formatted(AppConfig.getSiteName()),
                "많은 이용 바랍니다.");

        final String email = member.getEmail();

        sendRsFuture.whenComplete((rs, throwable) -> {
            if(rs.isFail()){
                log.info("sendJoinCompleteEmail 메일 발송 실패 : " + email);
                return;
            }
            log.info("sendJoinCompleteEmail 메일 발송 성공 : " + email);
        });
    }

    public void sendEmailVerificationEmail(Member member){
        emailVerificationService.send(member);
    }



    @Transactional
    public void setEmailVerified(Long memberId){
        attrService.set("member__%d__extra__emailVerified".formatted(memberId), true);
    }

    public boolean isEmailVerified(Member member) {
        if(member == null || member.getId() == null){
            return false;
        }

        return attrService.getAsBoolean("member__%d__extra__emailVerified".formatted(member.getId()), false);
    }

}
