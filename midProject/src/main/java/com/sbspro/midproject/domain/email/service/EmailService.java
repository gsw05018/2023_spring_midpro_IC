package com.sbspro.midproject.domain.email.service;

import com.sbspro.midproject.base.rsData.RsData;
import com.sbspro.midproject.domain.email.entity.SendEmailLog;
import com.sbspro.midproject.domain.email.repository.SendEmailLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 클래스 레벨에서 @Transactional(readOnly = true ) 어노테이션을 사용하여,
// 이 서비스 클래스의 모든 public 메서드가 기본적으로 읽기 전용 트랜잭션에서 실행하도록 함
public class EmailService {

    private final SendEmailLogRepository emailLogRepository;
    // 이메일 로그 저장을 위한  JPA 리포지터리

    private final JavaMailSender mailSender;
    // Spring의 JavaMailSender 인터페이스를 사용하여 이메일을 발송

    @Autowired
    @Lazy
    private EmailService self;
    // 클래스 자신을 주입받기 위한 @Lazy를 사용하여 순환 참조 문제를 방지

    // 명령
    @Async // 메서드를 비동기적으로 실행
    @Transactional // 메서드 레벨에서 쓰기 트랜잭션을 활성화
    public CompletableFuture<RsData> send(String to, String subject, String body) {
        // 비동기 메일 발송 메서드, CompletableFuture를 반환하여 비동기 작엄의 결과를 처리

        SendEmailLog sendEmailLog = saveSendEmailLog(to, subject, body);
        // 발송할 이메일에 대한 로그를 저장

        if (isTestEmail(to)) return CompletableFuture.supplyAsync(() -> {
            // 테스트 이메일 주소인 경우, 실제 발송을 하지 않고 바로 성공 응답
            RsData rs = RsData.of("S-2", "메일이 발송되었습니다.(테스트 메일)");
            self.setCompleted(sendEmailLog.getId(), rs);
            return rs;
        });

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // MIME 형식의 메일 메시지 생성

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 메일 내용을 조작하기 위한 Helper 클래스 사용
            mimeMessageHelper.setTo(to); // 메일 수신자
            mimeMessageHelper.setSubject(subject); // 메일 제목
            mimeMessageHelper.setText(body, true); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage); // 이메일 발송
        } catch (MessagingException e) {
            return CompletableFuture.supplyAsync(() -> {
                // 이메일 발송 중 예외 발생 시, 실패 응답 반환
                RsData rs = RsData.of("F-1", "메일이 발송되지 않았습니다.");
                self.setCompleted(sendEmailLog.getId(), rs);
                return rs;
            });
        }

        return CompletableFuture.supplyAsync(() -> {
            // 이메일 성공 시 성공 응답 반환
            RsData rs = RsData.of("S-1", "메일이 발송되었습니다.");
            self.setCompleted(sendEmailLog.getId(), rs);
            return rs;
        });
    }

    private boolean isTestEmail(String email) {
        return email.endsWith("@test.com");
    }
    // 주어진 이메일이 테스트 이메일인지 확인

    @Transactional
    // Transactional 관리를 위한 어노테이션
    private void setCompleted(Long id, RsData rs) {
        // 이메일 로그의 상태를 '완료'로 설정
        SendEmailLog sendEmailLog = emailLogRepository.findById(id).orElseThrow();
        // 주어진 ID로 이메일 로그를 찾고, 없는 경우 에외 발생
        sendEmailLog.setCompleted(rs);
        // 로그 상태를 '완료'로 설정
    }

    private SendEmailLog saveSendEmailLog(String to, String subject, String body) {
        // 이메일 로그를 저장하는 메서드
        SendEmailLog sendEmailLog = SendEmailLog
                .builder()
                .email(to)
                .subject(subject)
                .body(body)
                .build();
        // 이메일 로그 객체 생성

        return emailLogRepository.save(sendEmailLog);
        // 생성된 로그 객체를 저장하고 반환
    }
}

