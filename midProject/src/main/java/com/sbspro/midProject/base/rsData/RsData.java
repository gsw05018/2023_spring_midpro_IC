package com.sbspro.midProject.base.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class RsData<T> { // API 응답을 위한 일반적인 포맷을 제공하는 제네릭 클래스
    private String resultCode; // API 응답 코드
    private String msg; // API 응답 메시지
    private T data; // API 응답 데이터, 제네릭 타입

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
        // RsData 객체를 생성하여 반환
    }
    // 성공 여부를 반환하는 메서드

    public static <T> RsData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
        // data 필드를 null로 설정하여 RsData 객체 생성
    }
    // 실패 여부를 반환하는 메서드

    public boolean isSuccess() {
        return resultCode.startsWith("S-"); // resultCode가 S-로 시작하면 성공으로 간주
    }
    // RsData 객체를 생성하는 정적 메서드

    public boolean isFail() {
        return !isSuccess(); // 성공이 아니면 실패로 간주
    }
    // RsData 객체를 생성하는 정적 메서드, data가 없는 경우

    public Optional<RsData<T>> optional(){
        return Optional.of(this);
    }


    // of 메서드는 팩토리 메서드
    // 팩토리 메서드는 객체 생성을 캡슐화하고, 생성자 대신 객체 인스턴스를 반환하는 정적 메서드
    //
}