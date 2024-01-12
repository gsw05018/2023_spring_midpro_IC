package com.sbspro.midProject.base.security;

import com.sbspro.midProject.base.util.Ut;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class CustomSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    // 인증 실패시 호출되는 메서드를 오버라이드
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/usr/member/login?failMsg=" + Ut.url.encodeWithTtl("올바르지 않은 회원정보 입니다."));
        // 인증 실패 시 redirect 기본 URL 지정
        // 사용자에게 오류 메시지를 전달하기 위해 failMsg 쿼리 파라미터 추가

        super.onAuthenticationFailure(request, response, exception);
        // SimpleUrlAuthenticationFailureHandler 상위 클래스 onAuthenticationFailure 호출하여 추가적인 인증 실패 처리 수행
        // 기본 구현에서는 설정된 'defaultFailureURL'로 redirect 하너가 401 Unauthorized 응답 반환

    }
}