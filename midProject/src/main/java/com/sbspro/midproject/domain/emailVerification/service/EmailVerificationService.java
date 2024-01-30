package com.sbspro.midproject.domain.emailVerification.service;

import com.sbspro.midproject.base.app.AppConfig;
import com.sbspro.midproject.base.rsData.RsData;
import com.sbspro.midproject.domain.attr.service.AttrService;
import com.sbspro.midproject.domain.email.service.EmailService;
import com.sbspro.midproject.member.entity.Member;
import com.sbspro.midproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 읽기 전용 트랜잭션을 활성화
public class EmailVerificationService {
    private final EmailService emailService;
    // 이메일 전송 서비스

    private final AttrService attrService;
    // 속성 관리 서비스

    private final SpringTemplateEngine templateEngine;

    @Autowired
    @Lazy
    private MemberService memberService;
    // 회원 관리 서비스 , 지연 로딩을 사용

    // 조회

    // 명령
    @Transactional
    public CompletableFuture<RsData> send(Member member) {
        // 회원 객체를 받아 해당 회원에게 인증 이메일을 발송하는 메서드

        String subject = "[%s 이메일인증] 안녕하세요 %s님. 뭐 하지 또? 또 가지 뭐!회원가입을 환영합니다. 링크를 클릭하여 회원가입을 완료해주세요."
                .formatted(
                        AppConfig.getSiteName(),
                        member.getUsername()
                );
        // 이메일 제목 설정

        Context context = new Context();
        String verificationUrl = genEmailVerificationUrl(member);
        context.setVariable("username", member.getUsername());
        context.setVariable("verificationUrl", verificationUrl);
        String body = templateEngine.process("email/verification", context);

        // 이메일 본문에 들어갈 인증 URL 생성

        return emailService.send(member.getEmail(), subject, body);
        // EmailService를 통해 이메일 발송
    }

    private String genEmailVerificationUrl(Member member) {
        return genEmailVerificationUrl(member.getId());
    }
    // 회원 객체를 받아 이메일 인증 URL을 생성하는 메서드

    private String genEmailVerificationUrl(long memberId) {
        // 회원 ID를 받아 이메일 인증 URL을 생성하는 메서드
        String code = genEmailVerificationCode(memberId);
        // 이메일 인증 코드 생성

        return AppConfig.getSiteBaseUrl() + "/emailVerification/verify?memberId=%d&code=%s".formatted(memberId, code);
        // 인증 URL 반환
    }

    private String genEmailVerificationCode(long memberId) {
        // 이메일 인증 코드를 생성하고 저장하는 메서드
        String code = UUID.randomUUID().toString();
        attrService.set("member__%d__extra__emailVerificationCode".formatted(memberId), code, LocalDateTime.now().plusSeconds(60 * 60));
        // 생성된 코드를 속성 서비스를 통해 저장
        return code;
        // 생성된 코드 반환
    }

    @Transactional
    public RsData verify(long memberId, String code) {
        // 이메일 인증을 확인하는 메서드
        RsData checkVerificationCodeValidRs = checkVerificationCodeValid(memberId, code);
        // 인증 코드 유효성 검사

        if (!checkVerificationCodeValidRs.isSuccess()) return checkVerificationCodeValidRs;
        // 유효하지 않은 경우, 실패 응답 반환

        setEmailVerified(memberId);
        // 인증 성공 시, 회원의 이메일 인증 상태 변경

        return RsData.of("S-1", "이메일인증이 완료되었습니다.");
        // 인증 성공 응답 반환
    }

    private RsData checkVerificationCodeValid(long memberId, String code) {
        // 인증 코드 유효성 검사 메서드

        String foundCode = attrService.get("member__%d__extra__emailVerificationCode".formatted(memberId), "");
        // 저장된 인증 코드 조회

        if (!foundCode.equals(code)) return RsData.of("F-1", "만료 되었거나 유효하지 않은 코드입니다.");
        // 코드가 일치하지 않은 경우, 실패응답 반환

        return RsData.of("S-1", "인증된 코드 입니다.");
        // 코드가 유효한 경우, 성공 응답 반환
    }

    private void setEmailVerified(long memberId) {
        // 이메일 인증 상태를 설정하는 메서드
        memberService.setEmailVerified(memberId);
        // 회원 서비스를 통해 해당 회원이 이메일 인증 상태 변경
    }
}