package com.sbspro.midProject.member.controller;

import com.sbspro.midProject.base.rsData.RsData.RsData;
import com.sbspro.midProject.base.util.Ut.Ut;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usr/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String showJoin(){
        return "usr/member/join";
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

        @NotBlank
        private String phoneNumber;
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, HttpServletRequest req){

     RsData<Member> joinRs =  memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getNickname(), joinForm.getEmail(), joinForm.getPhoneNumber());
        if(joinRs.isFail()){
            req.setAttribute("msg", joinRs.getMsg());
            return "common/common";
        }

        return "redirect:/?msg=" + Ut.url.encode(joinRs.getMsg());

    }

}
