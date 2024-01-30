package com.sbspro.midproject.domain.email.entity;

import com.sbspro.midproject.base.jpa.baseEntity.BaseEntity;
import com.sbspro.midproject.base.rsData.RsData;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
// @ToString 어노테이션은 toString() 메서드를 자동으로 생성함
// callSuper = true는 부모 클래스의 필드도 toString() 결과에 포함하도록 함
public class SendEmailLog extends BaseEntity {

    private String resultCode; // 이메일 전송 결과 코드
    private String message; // 이메일 전소 결과 메시지
    private String email; // 수신자 이메일 주소
    private String subject; // 이메일 제목
    private String body; // 이메일 본문
    private LocalDateTime sendEndData; // 이메일 전송 성공 시각
    private LocalDateTime failDate; // 이메일 전송 실패 시각

    public void setCompleted(RsData rs) {
        // 이메일 전송 완료 상태를 설정하는 메서드
        this.resultCode = rs.getResultCode();
        // 결과 코드 설정
        this.message = rs.getMsg();
        // 결과 메시지 설정

        if (rs.isSuccess()) this.sendEndData = LocalDateTime.now();
            // 성공한 경우, 전송 안료 시각 설정

        else this.failDate = LocalDateTime.now();
        // 실패한 경우, 실패 시각 설정
    }

}
