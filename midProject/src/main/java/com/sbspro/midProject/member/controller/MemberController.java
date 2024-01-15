package com.sbspro.midProject.member.controller;

import com.sbspro.midProject.base.rq.Rq;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.exception.EmailNotVerifiedAccessDeniedException;
import com.sbspro.midProject.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usr/member")
@RequiredArgsConstructor
public class MemberController {

    private final Rq rq;
    private final MemberService memberService;

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String showLogin(){
        return "usr/member/login";
    }

    @GetMapping("/join")
    @PreAuthorize("isAnonymous()")
    public String showJoin(){
        return "usr/member/join";
    }

    @PostMapping("/join")
    @PreAuthorize("isAnonymous()")
    public String join(@Valid JoinForm joinForm){

     RsData<Member> joinRs =  memberService.join(
             joinForm.getUsername(),
             joinForm.getPassword(),
             joinForm.getNickname(),
             joinForm.getEmail(),
             joinForm.getProfileImg());

        if (joinRs.isFail()) return rq.historyBack(joinRs.getMsg());

        return rq.redirectOrBack("/", joinRs);
    }

    @GetMapping("/checkUsernameDup")
    @ResponseBody
    @PreAuthorize("isAnonymous()")
    public RsData<String> checkUsernameDup(String username){
        return memberService.checkUsernameDup(username);

    }

    @GetMapping("/checkEmailDup")
    @ResponseBody
    @PreAuthorize("isAnonymous()")
    public RsData<String> checkEmailDup(String email){
        return memberService.checkEmailDup(email);
    }

    @AllArgsConstructor
    @Getter
    public static class JoinForm{

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String nickname;

        @NotBlank
        private String email;

        private MultipartFile profileImg;
    }


    public boolean assertCurrentMemberVerified() {
        if (!memberService.isEmailVerified(rq.getMember()))
            throw new EmailNotVerifiedAccessDeniedException("이메일 인증 후 이용해주세요.");

        return true;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/notVerified")
    public String showNotVerified() {
        return "usr/member/notVerified";
    }

}
