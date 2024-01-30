package com.sbspro.midproject.domain.attr.service;

import com.sbspro.midproject.domain.attr.entity.Attr;
import com.sbspro.midproject.domain.attr.repository.AttrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 기능을 자동을 추가
@Transactional(readOnly = true) //데이터베이스 읽기 전용 트랜잭션 실행

public class AttrService {
    // application Attributes (속성)을 관리하는 역할을 함
    // 속성 값의 저장과 조회 기능을 제공
    private final AttrRepository attrRepository;

    //  조회
    public String get(String varName, String defaultValue) {
        Attr attr = findAttr(varName);

        if (attr == null) {
            return defaultValue;
        }

        if (attr.getExpireDate() != null && attr.getExpireDate().isBefore(LocalDateTime.now())) {
            return defaultValue;
        }

        return attr.getVal();
    }
    // 주어진 변수 이름(varName)에 해당하는 속성 값을 조회, 값이 없거나 만료되었을 경우 defaultValue 반환

    private Attr findAttr(String relTypeCode, Long relId, String typeCode, String type2Code) {
        return attrRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2Code(relTypeCode, relId, typeCode, type2Code).orElse(null);
    }

    private Attr findAttr(String varName) {
        String[] varNameBits = varName.split("__");
        String relTypeCode = varNameBits[0];
        long relId = Integer.parseInt(varNameBits[1]);
        String typeCode = varNameBits[2];
        String type2Code = varNameBits[3];

        return findAttr(relTypeCode, relId, typeCode, type2Code);
    }
    // 주어진 식별자를 사용하여 'Attr' 객체를 찾다가 없으면  null 반환

    public long getAsLong(String varName, long defaultValue) {
        String value = get(varName, "");

        if (value.isBlank()) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    public boolean getAsBoolean(String varName, boolean defaultValue) {
        String value = get(varName, "");

        if (value.isBlank()) {
            return defaultValue;
        }

        if (value.equals("true")) {
            return true;
        } else return value.equals("1");
    }

    public LocalDateTime getAsLocalDateTime(String varName, LocalDateTime defaultValue) {

        String value = get(varName, "");

        if (value.isBlank()) {
            return defaultValue;
        }

        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }
    // 각 각 long boolean LocalDateTime 형태로 속성 값을 변환하여
    // 값이 없거나 형식에 맞지 않으면 기본값을 반환

    // 명령

    @Transactional
    public void set(String varName, String value) {

        set(varName, value, null);
    }
    // 여러 타입의 값을 속성으로 저장하는 메서드를 오버로딩

    @Transactional
    public void set(String varName, String value, LocalDateTime expireDate) {
        String[] varNameBits = varName.split("__");
        // varName 문자열을 "__"기준으로 나누어 String  배열에 저장
        // 이 배열은 속성을 식별하는데 사용되는 여러 요소 포함
        String relTypeCode = varNameBits[0];
        // 배열의 첫 번쨰 요소는 관계 유형 코드(relTypeCode)이다
        long relId = Long.parseLong(varNameBits[1]);
        // 배열의 두 번째 요소는 관계 ID (relId) 이다
        String typeCode = varNameBits[2];
        // 배열의 세 번째 요소는 유형 코드 typeCode 이다
        String type2Code = varNameBits[3];
        // 배열의 네 번째 요소는 2차 유형 코드 type2Code 이다

        set(relTypeCode, relId, typeCode, type2Code, value, expireDate);
    }
    // 주어진 식별자와 값으로 Attr 객체를 만들거나 업데이트 한 후 저장

    @Transactional
    public void set(String varName, long value) {
        set(varName, String.valueOf(value));
    }
    // varName 이라는 속성명에 long  타입의 값을 저장
    // 호출을 통해  long 값이 문자열로 변환되어 다른 set 메서드에 전달

    @Transactional
    public void set(String varName, long value, LocalDateTime expireDate) {
        set(varName, String.valueOf(value), expireDate);
    }
    // varName 이라는 속성명에 long 타입의 값을 저장하며, 만료 날짜(expireDate)를 저장
    // 호출하여 문자열로 변환된 값과 만료 날짜를 다른 set 메서드에 전달

    @Transactional
    public void set(String varName, boolean value) {
        set(varName, String.valueOf(value));
    }
    // varName 이라는 속성명에 boolean 타입의 값을 저장
    // 호출하여 boolean 값이 문자열로 변환되어 저장

    @Transactional
    public void set(String varName, boolean value, LocalDateTime expireDate) {
        set(varName, String.valueOf(value), expireDate);
    }
    // varName 이라는 속성명에 boolean 타입의 값을 저장하며, 만료 날짜를 지정
    // 호출하여 문자열로 변환된 값과 만료 날짜를 함께 저장

    @Transactional
    public void set(String relTypeCode, Long relId, String typeCode, String type2Code, LocalDateTime value, LocalDateTime expireDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        set(relTypeCode, relId, typeCode, type2Code, value.format(formatter), expireDate);
    }
    // 복합 키(relTypeCode, relId, typeCode, type2Code)를 사용하여 LocalDateTime 값과 만료 날짜를 저장
    // DateTimeFormatter를 사용하여 LocalDateTime 값을 포맷팅하고, set 메서드의 다른 버전을 호출하여 저장

    @Transactional
    public void set(String relTypeCode, Long relId, String typeCode, String type2Code, String value, LocalDateTime expireDate) {
        Attr attr = findAttr(relTypeCode, relId, typeCode, type2Code);

        if (attr == null) {
            attr = Attr
                    .builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .type2Code(type2Code)
                    .typeCode(typeCode)
                    .build();
        }

        attr.setVal(value);
        attr.setExpireDate(expireDate);

        attrRepository.save(attr);
    }
    // 복합 키를 사용하여 문자열 값과 만료 날짜 저장
    // findAttr 메서드를 호출하여 기존의 Attr 객체를 찾고 없으면 새로 생성
    // attr.setVal(value)와 attr.setExpireDate(expireDate)를 사용하여 값과 만료 날짜를 설정하고, attrRepository.save(attr)를 통해 데이터베이스에 저장
}
