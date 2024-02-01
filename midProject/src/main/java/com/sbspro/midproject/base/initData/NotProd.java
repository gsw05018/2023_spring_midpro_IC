package com.sbspro.midproject.base.initData;

import com.sbspro.midproject.domain.article.service.ArticleService;
import com.sbspro.midproject.domain.board.entity.Board;
import com.sbspro.midproject.domain.board.service.BoardService;
import com.sbspro.midproject.member.entity.Member;
import com.sbspro.midproject.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@Configuration // 스프링의 구성 클래스임을 나타내는 어노테이션
@Profile("!prod") //  "prod" 프로파일이 아닐 경우에만 이 구성이 활성화되도록 지정
// 프로파일이 아닌 환경이란 ? 개발 또는 테스트 환경
public class NotProd {
    @Value("${custom.security.oauth2.client.registration.kakao.devUserOauthId}")
    private String kakaoDevUserOAuthId;

    @Bean // 스프링 컨테이너에 의해 관리되는 빈을 정의
    public ApplicationRunner init(MemberService memberService, BoardService boardService, ArticleService articleService) {
        // init 메서드는 인터페이스 구현
        // ApplicationRunner 인터페이스를 구현하는 람다 표현식 반환
        // Application 시작 시 실행될 로직 정의
        return args -> {
            Board board1 = boardService.make("notice1", "공지사항", "<i class=\"fa-regular fa-flag\"></i>").getData();
            Board board2 = boardService.make("free1", "자유", "<i class=\"fa-solid fa-face-grin-tears\"></i>").getData();

            Member member1 = memberService.join("admin5428", "@@admin1234", "admin5428", "admin@test.com", "").getData();
            if (member1 != null) {
                memberService.setEmailVerified(member1);
            }

            Member member2 = memberService.join("user5428", "@user1234", "nickname1", "user1@test.com", "").getData();
            if (member2 != null) {
                memberService.setEmailVerified(member2);
            }

            Member member3 = memberService.join("user5428", "@user1234", "nickname2", "user2@test.com", "").getData();
            if (member3 != null) {
                memberService.setEmailVerified(member3);
            }

            IntStream.rangeClosed(4, 50).forEach(i -> memberService.join("testUser" + i, "@user1234", "user" + i, "user" + i + "@test.com", ""));

            Member memberByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__%s".formatted(kakaoDevUserOAuthId), "홍길동", "");

            IntStream.rangeClosed(1, 25).forEach(i -> articleService.write(board1, member1, "Spring Boot 기본 설정법: 프로젝트 생성부터 의존성 관리, 환경 설정, 프로파일 관리까지의 상세 가이드", "Spring Boot를 시작할 때 가장 기본적인 설정 방법에 대해 설명합니다. 프로젝트 생성부터 의존성 관리까지의 과정을 다룹니다."));
            IntStream.rangeClosed(1, 25).forEach(i -> articleService.write(board2, member1, "JavaScript의 비동기 처리 방법: Callback, Promise, async/await 그리고 Event Loop 이해하기", "JavaScript에서의 비동기 처리 방법과 Promise, async/await의 사용 예시를 통해 비동기 코드를 작성하는 방법을 배워봅니다."));
            IntStream.rangeClosed(1, 25).forEach(i -> articleService.write(board1, member2, "JPA의 N+1 문제 해결법: 성능 최적화 전략, Fetch Join, EntityGraph 사용법 및 쿼리 최적화 방안", "JPA를 사용하면서 자주 발생하는 N+1 문제와 이를 해결하는 방법에 대해 상세하게 알아보겠습니다."));
            IntStream.rangeClosed(1, 25).forEach(i -> articleService.write(board2, member2, "Vue.js 시작하기: Vue 인스턴스, 컴포넌트 구조, 디렉티브와 이벤트 처리 기법을 활용한 웹 애플리케이션 개발", "Vue.js를 처음 시작하는 사용자를 위한 튜토리얼. Vue 인스턴스 생성부터 기본 디렉티브 사용법까지를 간단한 예제로 살펴봅니다."));

        };
    }
}