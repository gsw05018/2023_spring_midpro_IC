package com.sbspro.midproject.base.initData;

import com.sbspro.midproject.member.entity.Member;
import com.sbspro.midproject.member.service.MemberService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration // 스프링의 구성 클래스임을 나타내는 어노테이션
@Profile("!prod") //  "prod" 프로파일이 아닐 경우에만 이 구성이 활성화되도록 지정
// 프로파일이 아닌 환경이란 ? 개발 또는 테스트 환경
public class NotProd {
    @Bean // 스프링 컨테이너에 의해 관리되는 빈을 정의
    public ApplicationRunner init(MemberService memberService) {
        // init 메서드는 인터페이스 구현
        // ApplicationRunner 인터페이스를 구현하는 람다 표현식 반환
        // Application 시작 시 실행될 로직 정의
        return args -> {
            Member member1 = memberService.join("admin", "1234", "admin", "admin@test.com", null).getData();
            Member member2 = memberService.join("user1", "1234", "nickname1", "user1@test.com", null).getData();
            Member member3 = memberService.join("user2", "1234", "nickname2", "user2@test.com", null).getData();

            memberService.setEmailVerified(member1);
            memberService.setEmailVerified(member2);
            memberService.setEmailVerified(member3);
        };
    }
}