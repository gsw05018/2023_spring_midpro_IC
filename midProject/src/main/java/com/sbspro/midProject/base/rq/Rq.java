package com.sbspro.midProject.base.rq;

import com.sbspro.midProject.base.rsData.RsData;
import com.sbspro.midProject.base.util.Ut;
import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final HttpSession session;
    private Member member = null; // 로그인한 회원 정보 저장, 초기에는 null로 저장
    private final User user; // Spring Security의 User 객체 지정

    // 생성자를 통해 의존성 주입을 수행하고, 로그인한 사용자 정보 초기화
    public Rq(MemberService memberService, HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        this.memberService = memberService;
        this.req = req;
        this.resp = resp;
        this.session = session;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보에서 User 객체를 가져와 user 필드에 할당
        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    // 로그인한 회원의 사용자 정보 반환
    private String getLoginedMemberUsername() {
        if (isLogout()) return null;

        return user.getUsername();
    }

    // 로그인 상태 확인
    public boolean isLogin() {
        return user != null;
    }

    // 로그아웃 상태 확인
    public boolean isLogout() {
        return !isLogin();
    }

    // 현재 로그인 한 회원에 Member 객체를 반환
    public Member getMember() {
        if (isLogout()) {
            return null;
        }

        if (member == null) {
            member = memberService.findByUsername(getLoginedMemberUsername()).orElse(null);
        }

        return member;
    }

    // 현재 로그인한 회원이 관리자인지 확인
    public boolean isAdmin() {
        if (isLogout()) return false;

        return getMember().isAdmin();
    }

    // 세션에 값을 설정하는 메서드
    public void setSession(String name, Object value) {
        session.setAttribute(name, value);
    }

    // 세션에서 값을 가져오는 메서드 없으면 defaultValue값 반환
    private Object getSession(String name, Object defaultValue) {
        Object value = session.getAttribute(name);

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    // 세션에서 long 타입의 값을 가져오는 메서드
    private long getSessionAsLong(String name, long defaultValue) {
        Object value = getSession(name, null);

        if (value == null) return defaultValue;

        return (long) value;
    }

    // session에서 특정 이름의 속성을 제거
    public void removeSession(String name) {
        session.removeAttribute(name);
    }

    // cookie 값을 설정
    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value); // 새로운 cookie 객체 생성
        cookie.setPath("/"); // cookie의 경로 설정
        resp.addCookie(cookie); // 응답에 cookie 추가
    }

    // cookie 값을 가져옴 없으면 defaultValue 값 반환
    private String getCookie(String name, String defaultValue) {
        Cookie[] cookies = req.getCookies(); // 요청으로부터 모든 cookie 가져옴

        if (cookies == null) { // cookie가 없으면 defaultValue 반환
            return defaultValue;
        }

        for (Cookie cookie : cookies) { // 모든 cookie 순회
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            } // 일치하는 cookie가 있으면 해당 값 반환
        }

        return defaultValue; // 일치하는 cookie가 없으면 defaultValue 반환
    }

    // cookie long 타입의 값을 가져옴
    private long getCookieAsLong(String name, int defaultValue) {
        String value = getCookie(name, null);
        // cookie에서 문자열 값 가져옴

        if (value == null) {
            return defaultValue;
        } // 값이 없으면 defaultValue 반환

        return Long.parseLong(value); // 문자열을  long 타입으로 변환하여 반환
    }

    // 특정 이름의 cookie 제거
    public void removeCookie(String name) {
        Cookie cookie = new Cookie(name, ""); // 제거할 cookie 생성
        cookie.setMaxAge(0); // cookie 유효기간을 0으로 설정하여 즉시 만료
        cookie.setPath("/"); // cookie 경로 설정
        resp.addCookie(cookie); // 응답에 cookie 추가하여 제거
    }


    // 모든  cookie 이름과 값을 문자열로 반환
    public String getAllCookieValuesAsString() {
        StringBuilder sb = new StringBuilder(); // 문자열 빌더 생성

        Cookie[] cookies = req.getCookies(); //  모든 cookie 가져옴
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName()).append(": ").append(cookie.getValue()).append("\n");
            }
        }// 각 cookie에 대해 이름과 값을 문자열로 가져옴

        return sb.toString(); // 생성된 문자열 반환
    }

    // 모든 Session 이름과 값을 문자열로 반환
    public String getAllSessionValuesAsString() {
        StringBuilder sb = new StringBuilder(); // 문자열 빌더 생성

        java.util.Enumeration<String> attributeNames = session.getAttributeNames(); // session의 모든 속성 이름을 가져옴
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            sb.append(attributeName).append(": ").append(session.getAttribute(attributeName)).append("\n");
        } // 각 속성에 대해 속성 이름을 가져오고 이름과 값을 문자열에 추가

        return sb.toString(); // 생성된 문자열 반환
    }

    public String historyBack(RsData rs){
        return historyBack(rs.getMsg());
    }

    // 이전 페이지로 돌아가는 JS코드를 반환하고, 오류 메시지를 session에 저장
    public String historyBack(String msg) {
        String referer = req.getHeader("referer"); // 이전 페이지의 URL 가져옴
        String key = "historyBackFailMsg___" + referer; // 오류 메시지에 대한 키 생성
        req.setAttribute("localStorageKeyAboutHistoryBackFailMsg", key); // 요청에 키 생성
        req.setAttribute("historyBackFailMsg", Ut.url.withTtl(msg)); // 요청에 메시지 설정
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 200 이 아니라 400 으로 응답코드가 지정되도록

        return "common/common";
    }

    public String redirect(String url, RsData rs){
        return redirect(url, rs.getMsg());
    }

    // 특정 URL로 redirect하고 메시지 전달
    public String redirect(String url, String msg) {
        return "redirect:" + Ut.url.modifyQueryParam(url, "msg", Ut.url.encodeWithTtl(msg));
    }

    public String redirectOrBack(String url, RsData rs){
        if(rs.isFail()) return historyBack(rs);

        return redirect(url, rs);
    }


    // 현재 로그인한 사용자의 프로필 이미지 URL 반환
    public String getProfileImgUrl() {
        return Optional.ofNullable(getMember()) // 현재 로그인한 사용자의 Member 객체를 Optional로 변환
                .flatMap(memberService::findProfileImgUrl) // 프로필 이미지 URL  찾음
                .orElse("https://placehold.co/30x30?text=UU"); // URL이 없으면 기본 이미지 URL 반환
    }
}