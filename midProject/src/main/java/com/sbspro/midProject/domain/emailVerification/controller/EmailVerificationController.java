package com.sbspro.midProject.domain.emailVerification.controller;

import com.sbspro.midProject.base.rq.Rq;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.domain.emailVerification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailVerification")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    private final Rq rq;

    @GetMapping("/verify")
    public String verify(long memberId, String code) {
        RsData verifyEmailRs = emailVerificationService.verify(memberId, code);

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
