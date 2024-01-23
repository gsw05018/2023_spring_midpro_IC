package com.sbspro.midproject.base.security;

import com.sbspro.midproject.base.util.Ut;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class CustomSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    // 인증 실패시 호출되는 메서드를 오버라이드
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");

        String failMsg = exception instanceof BadCredentialsException ? "비밀번호가 일치 하지 않습니다." : "존재하지 않은 회원입니다.";

        setDefaultFailureUrl("/usr/member/login?lastUsername=" + username + "&failMsg=" + Ut.url.encodeWithTtl(failMsg));

        super.onAuthenticationFailure(request, response, exception);
        // SimpleUrlAuthenticationFailureHandler 상위 클래스 onAuthenticationFailure 호출하여 추가적인 인증 실패 처리 수행
        // 기본 구현에서는 설정된 'defaultFailureURL'로 redirect 하너가 401 Unauthorized 응답 반환

    }
}