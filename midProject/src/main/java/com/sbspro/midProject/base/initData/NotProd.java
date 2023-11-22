package com.sbspro.midProject.base.initData;

import com.sbspro.midProject.member.service.MemberService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@Configuration
@Profile("!prod")
public class NotProd {
    @Bean
    public ApplicationRunner init(MemberService memberService) {
        return args -> {
            memberService.join("admin", "1234", "admin", "admin1234@naver.com", "010-1234-1234", null);

            IntStream.rangeClosed(1, 3).forEach(i -> {
                memberService.join("user" + i, "1234", "user" + i, "user@naver.com", "010-1234-1234", null);
            });
        };
    }
}