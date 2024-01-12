package com.sbspro.midProject.base.security;


import com.sbspro.midProject.base.util.Ut;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;


public class CustomSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    // 로깅을 위한 Log 객체 생성

    private RequestCache requestCache = new HttpSessionRequestCache();
    // 이전 요청을 캐시하는 RequestCache 객체 생성
    // 사용자의 원래 요청을 Cache하여 인증 후 원래 요청하려고 했던 페이지로 리디렉션할 수 있도록 해줌

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        // 인증 전에 사용자가 요청했던 URL 가져옴
        // saveRequest 객체를 가져옴

        clearAuthenticationAttributes(request);
        // 인증 과정에서 사용된 임시 속성들을 Session에서 제거
        
        String targetUrl = savedRequest != null ? savedRequest.getRedirectUrl() : getDefaultTargetUrl();
        // 이동할 목적지 URL 결정
        // 이전에 저장된 요청이 있으면 그 요청의 redirect URL 사용하고 없으면 기본 목적지 URL 사용
        // targetUrl을 목저지 URL로 사용됨

        targetUrl = Ut.url.modifyQueryParam(targetUrl, "msg", Ut.url.encodeWithTtl("환영합니다."));
        // 목적지 URL에 메시지를 쿼리 파라미터로 추가

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        // 설정된 redirect 전략을 사용하여 목적지 URL로 redirect를 수행
    }
}