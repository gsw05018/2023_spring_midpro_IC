package com.sbspro.midProject.member.controller;

import com.sbspro.midProject.base.rq.Rq;
import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.base.util.Ut;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.exception.EmailNotVerifiedAccessDeniedException;
import com.sbspro.midProject.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.AccessDeniedException;
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
    public String showLogin() {
        return "usr/member/login";
    }

    @GetMapping("/join")
    @PreAuthorize("isAnonymous()")
    public String showJoin() {
        return "usr/member/join";
    }

    @PostMapping("/join")
    @PreAuthorize("isAnonymous()")
    public String join(@Valid JoinForm joinForm) {

        RsData<Member> joinRs = memberService.join(
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
    public RsData<String> checkUsernameDup(String username) {
        return memberService.checkUsernameDup(username);

    }

    @GetMapping("/checkEmailDup")
    @ResponseBody
    @PreAuthorize("isAnonymous()")
    public RsData<String> checkEmailDup(String email) {
        return memberService.checkEmailDup(email);
    }

    public boolean assertCurrentMemberVerified() {
        Member currentMember = rq.getMember();
        if (currentMember == null || !memberService.isEmailVerified(currentMember))
            throw new EmailNotVerifiedAccessDeniedException("이메일 인증 후 이용해주세요");
        return true;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/notVerified")
    public String showNotVerified() {
        return "usr/member/notVerified";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "usr/member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    public String findUsername(String email) {
        return memberService.findByEmail(email)
                .map(member ->
                        rq.redirect(
                                "/usr/member/login?lastUsername=%s".formatted(member.getUsername()),
                                "해당 회원의 아이디는 `%s` 입니다.".formatted(member.getUsername())
                        )
                )
                .orElseGet(() -> rq.historyBack("`%s` ( 은)는 존재하지 않은 회원 이메일 입니다.".formatted(email)));
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "usr/member/findPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    public String findPassword(String username, String email) {
        return
                memberService.findByUsernameAndEmail(username, email)
                        .map(member -> {
                            memberService.sendTempPasswordToEmail(member);
                            return rq.redirect(
                                    "/usr/member/login?lastUsername=%s".formatted(member.getUsername()),
                                    "해당 회원의 이메일로 임시 비밀번호를 발송하였습니다."
                            );
                        }).orElseGet(() -> rq.historyBack("일치하는 회원이 존재하지 않습니다."));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String showMe() {
        return "usr/member/me";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(String checkPasswordAuthCode){
        memberService
                .checkCheckPasswordAuthCode(rq.getMember(), checkPasswordAuthCode)
                .optional()
                .filter(RsData::isFail)
                .ifPresent((rsData) -> {
                    throw new AccessDeniedException("올바르지 않은 접근입니다.");
                });

        return "usr/member/mypage_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@Valid ModifyForm modifyForm, String checkPasswordAuthCode){
        memberService
                .checkCheckPasswordAuthCode(rq.getMember(), checkPasswordAuthCode)
                .optional()
                .filter(RsData::isFail)
                .ifPresent((rsData) -> {
                    throw new AccessDeniedException("올바르지 않은 접근입니다.");
                });

        RsData<Member> modifyRs  = memberService.modify(
                rq.getMember().getId(),
                modifyForm.getPassword(),
                modifyForm.getNickname(),
                modifyForm.getProfileImg()
        );
        return rq.redirectOrBack("/usr/member/me", modifyRs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/checkPassword")
    public String showCheckPassword(){
        return "usr/member/checkPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/checkPassword")
    public String checkPassword(String password, String redirectUrl){
        if(!memberService.isSamePassword(rq.getMember(), password))
            return rq.historyBack("비밀번호가 일치하지 않습니다");

        String code =memberService.genCheckPasswordAuthCode(rq.getMember());

        redirectUrl = Ut.url.modifyQueryParam(redirectUrl, "checkPasswordAuthCode", code);

        return rq.redirect(redirectUrl);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class JoinForm {

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

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ModifyForm{
        @NotBlank
        private String nickname;
        private String password;
        private MultipartFile profileImg;
    }
}
