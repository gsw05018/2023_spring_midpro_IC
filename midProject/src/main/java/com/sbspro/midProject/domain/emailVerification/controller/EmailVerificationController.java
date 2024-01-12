package com.sbspro.midProject.domain.emailVerification.controller;

import com.sbspro.midProject.base.rq.Rq;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.domain.emailVerification.service.EmailVerificationService;
import com.sbspro.midProject.member.service.MemberService;
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
    public String verify(long memberId, String code){
        RsData verifyEmailRs = emailVerificationService.verify(memberId, code);

        if(verifyEmailRs.isFail()) return rq.redirect("/", verifyEmailRs.getMsg());

        if(rq.isLogout()) return rq.redirect("/usr/member/login",verifyEmailRs);

        return rq.redirect("/", verifyEmailRs);
    }
}
