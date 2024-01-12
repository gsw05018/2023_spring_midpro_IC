package com.sbspro.midProject.base.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration // AppConfig 클래스가 설정 클래스임을 나타냄, 스프링 빈 정의를 포함한다고 스프링에 알림
public class AppConfig { 
    @Getter // Lombok 라이브러리 어노테이션으로 자동으로 getter 생성
    public static String genFileDirPath; // 정적 변수, 파일 생성 위치 저장하는데 사용

    @Getter
    public static String siteName; // 사이트 이름을 저장하는데 사용

    @Getter
    public static String siteBaseUrl; // 사이트의 기본 URL 저장하는데 사용

    @Value("${custom.genFile.dirPath}") // application.yml에서 설정된 값을 주입
    public void setGenFileDirPath(String genFileDirPath) {
        AppConfig.genFileDirPath = genFileDirPath;
    }
    // getFileDirPath 값을 설정하는 method

    @Value("${custom.site.name}")  // application.yml에서 설정된 값을 주입
    public void setSiteName(String siteName){
        AppConfig.siteName = siteName;
    }
    // siteName 값을 설정하는 method

    @Value("${custom.site.baseUrl}") // application.yml에서 설정된 값을 주입
    public void setSiteBaseUrl(String siteBaseUrl){
        AppConfig.siteBaseUrl = siteBaseUrl;
    }
    // siteBaseUrl 값을 설정하는 method
}