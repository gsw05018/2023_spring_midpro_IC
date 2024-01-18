package com.sbspro.midProject.member.service;

import com.sbspro.midProject.base.app.AppConfig;
import com.sbspro.midProject.base.rq.Rq;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.base.util.Ut;
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
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final GenFileService genFileService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final AttrService attrService;
    private final Rq rq;
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    // 조회
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }

    public Optional<String> findProfileImgUrl(Member member) {
        return genFileService.findGenFileBy(
                        member.getModelName(), member.getId(), "common", "profileImg", 1
                )
                .map(GenFile::getUrl);
    }

    public RsData<String> checkUsernameDup(String username) {
        if (findByUsername(username).isPresent()) return RsData.of("F-1", "%s(은)는 사용중인 아이디입니다.".formatted(username));

        return RsData.of("S-1", "%s(은)는 사용 가능한 아이디 입니다.".formatted(username), username);
    }

    public RsData<String> checkEmailDup(String email) {
        if (findByEmail(email).isPresent()) return RsData.of("F-1", "%s(은)는 사용중인 이메일입니다.".formatted(email));

        return RsData.of("S-1", "%s(은)는 사용 가능한 이메일 입니다.".formatted(email), email);
    }

    // 명령
    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String email, MultipartFile profileImg) {
        if (findByUsername(username).isPresent())
            return RsData.of("F-1", "%s(은)는 사용중인 아이디 입니다.".formatted(username));

        if (findByEmail(email).isPresent())
            return RsData.of("F-2", "%s(은)는 사용중인 이메일 입니다.".formatted(username));

        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .build();

        memberRepository.save(member);

        if (profileImg != null) saveProfileImg(member, profileImg);

        sendJoinCompleteEmail(member);
        sendEmailVerificationEmail(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    private void saveProfileImg(Member member, MultipartFile profileImg) {
        genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 1, profileImg);
    }

    private void sendJoinCompleteEmail(Member member) {
        final String email = member.getEmail();

        CompletableFuture<RsData> sendRsFuture = emailService.send(
                email,
                "[%s 가입축하] 회원가입이 완료되었습니다.".formatted(
                        AppConfig.getSiteName()
                ),
                "많은 이용 바랍니다."
        );

        sendRsFuture.whenComplete((rs, throwable) -> {
            if (rs.isFail()) {
                log.info("sendJoinCompleteMail, 메일 발송 실패 : " + email);
                return;
            }

            log.info("sendJoinCompleteMail, 메일 발송 성공 : " + email);
        });
    }

    private void sendEmailVerificationEmail(Member member) {
        emailVerificationService.send(member);
    }

    @Transactional
    public void setEmailVerified(Long memberId) {
        attrService.set("member__%d__extra__emailVerified".formatted(memberId), true);
    }

    @Transactional
    public void setEmailVerified(Member member) {
        setEmailVerified(member.getId());
    }

    public boolean isEmailVerified(Member member) {
        return attrService.getAsBoolean("member__%d__extra__emailVerified".formatted(member.getId()), false);
    }

    public Optional<Member> findByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email);
    }

    @Transactional
    public void sendTempPasswordToEmail(Member member) {
        final String tempPassword = Ut.str.tempPassword(6);
        member.setPassword(passwordEncoder.encode(tempPassword));

        final String email = member.getEmail();

        CompletableFuture<RsData> sendRsFuture = emailService.send(
                email,
                "[%s 임시비밀번호] 임시 비밀번호 입니다.".formatted(
                        AppConfig.getSiteName()
                ),
                tempPassword
        );

        sendRsFuture.whenComplete((rs, throwable) -> {
            if (rs.isFail()) {
                log.info("sendTempPasswordToEmail, 메일 발송 실패 : " + email);
                return;
            }

            log.info("sendTempPasswordToEmail, 메일 발송 성공 : " + email);
        });
    }

    @Transactional
    public RsData<Member> modify(long memberId, String password, String nickname, MultipartFile profileImg){
        Member member = findById(memberId).get();

        if(password != null) member.setPassword(passwordEncoder.encode(password));
        if(nickname != null) member.setNickname(nickname);
        if(profileImg != null) saveProfileImg(member, profileImg);

        return RsData.of("S-1", "회원정보가 수정되었습니다.", member);
    }
}