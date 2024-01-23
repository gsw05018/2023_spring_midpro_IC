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
public class EmailService {

    private final SendEmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;
    @Autowired
    @Lazy
    private EmailService self;

    // 명령
    @Async
    @Transactional
    public CompletableFuture<RsData> send(String to, String subject, String body) {

        SendEmailLog sendEmailLog = saveSendEmailLog(to, subject, body);

        if (isTestEmail(to)) return CompletableFuture.supplyAsync(() -> {
            RsData rs = RsData.of("S-2", "메일이 발송되었습니다.(테스트 메일)");
            self.setCompleted(sendEmailLog.getId(), rs);
            return rs;
        });

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to); // 메일 수신자
            mimeMessageHelper.setSubject(subject); // 메일 제목
            mimeMessageHelper.setText(body, true); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            return CompletableFuture.supplyAsync(() -> {
                RsData rs = RsData.of("F-1", "메일이 발송되지 않았습니다.");
                self.setCompleted(sendEmailLog.getId(), rs);
                return rs;
            });
        }

        return CompletableFuture.supplyAsync(() -> {
            RsData rs = RsData.of("S-1", "메일이 발송되었습니다.");
            self.setCompleted(sendEmailLog.getId(), rs);
            return rs;
        });
    }

    private boolean isTestEmail(String email) {
        return email.endsWith("@test.com");
    }

    @Transactional
    private void setCompleted(Long id, RsData rs) {
        SendEmailLog sendEmailLog = emailLogRepository.findById(id).orElseThrow();
        sendEmailLog.setCompleted(rs);
    }

    private SendEmailLog saveSendEmailLog(String to, String subject, String body) {
        SendEmailLog sendEmailLog = SendEmailLog
                .builder()
                .email(to)
                .subject(subject)
                .body(body)
                .build();

        return emailLogRepository.save(sendEmailLog);
    }
}

