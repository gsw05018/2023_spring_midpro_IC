package com.sbspro.midproject.base.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
// 예외처리를 할 때 404 HTTP 상태 코드로 지정하고 entity not found 메시지  출력
public class DataNotFoundException extends RuntimeException {
    // DataNotFoundException은 RuntimeException을 상속받음으로 비검사 예외가 됨
    private static final long serialVersionUID = 1L;
    // 클래스의 버전을 나타내는 고유 값으로, 객체 직렬화와 역직렬화 과정에서 사용되는데 이를 통해 클래스의 버전 일치 여부를 확인 가능

    public DataNotFoundException(String message) {
        super(message);
        // RuntimeException 클래스의 생성자를 호출하여 예외 메시지를 넘김
    }
    // 예외 메시지를 문자열로 받음, 예외가 발생했을 때 사용
}
