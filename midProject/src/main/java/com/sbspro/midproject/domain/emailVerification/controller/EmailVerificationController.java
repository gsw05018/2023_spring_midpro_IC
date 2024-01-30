package com.sbspro.midproject.domain.emailVerification.controller;

import com.sbspro.midproject.base.rq.Rq;
import com.sbspro.midproject.base.rsData.RsData;
import com.sbspro.midproject.domain.emailVerification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailVerification")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    // 이메일 인증 관련 서비스

    private final Rq rq;
    // 사용자 요청 정보를 처리하는 커스텀 유틸리스 클래스

    @GetMapping("/verify")
    public String verify(long memberId, String code) {
        // 이메일 인증을 수행하는 메서드, URL memberID와 code 파라미터를 사용

        RsData verifyEmailRs = emailVerificationService.verify(memberId, code);
        // EmailVerificationService를 통해 이메일 인증을 수행하고 결과를 RsData 객체로 받음

        // 각 상황별 이동해야 하는  URL
        String afterFailUrl = "/";
        String afterSuccessButLogoutUrl = "/usr/member/login";
        String afterSuccessUrl = "/";

        // 인증실패 상황
        if (verifyEmailRs.isFail()) return rq.redirect(afterFailUrl, verifyEmailRs);

        // 인증성공했지만 로그아웃 상황
        if (rq.isLogout()) return rq.redirect(afterSuccessButLogoutUrl, verifyEmailRs);

        // 인증성공했고 로그인한 상황
        return rq.redirect(afterFailUrl, verifyEmailRs);
    }
}
