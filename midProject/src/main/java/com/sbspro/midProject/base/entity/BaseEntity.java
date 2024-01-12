package com.sbspro.midProject.base.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@MappedSuperclass // 이 클래스가 다른 엔티티 클래스의 기반 클래스임을 나타냄
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 파라미터 없는 생성자 생성, 외부에서 직접 생성을 방지하기 위해 접근 레벨을 PROTECTED 지정
@EntityListeners(AuditingEntityListener.class) // JPA Auditing을 위한 리스너
@ToString // ToString 자동 생성
@SuperBuilder // 빌더 패턴을 상속받는 클래스에 사용, 복잡한 객체 생성에 유용
@AllArgsConstructor
@EqualsAndHashCode
public class BaseEntity {

    @Id // JPA의 기본 키임을 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 증가
    @EqualsAndHashCode.Include // equals와 hashCode 메서드에 id필드 포함
    private Long id;

    @CreatedDate // entity 생성 시간을 자동으로 관리
    private LocalDateTime createDate;

    @LastModifiedDate // entity가 마지막으로 수정된 시간을 자동으로 관리
    private LocalDateTime modifyDate;

    @Transient // 영속성 컨텍스트에 포함되지 않은 필드임을 나타냄
    @Builder.Default // 빌더가 기본값을 사용하도록 설정
    private Map<String, Object> extra = new LinkedHashMap<>(); // 추가적인 정보를 저장하는데 사용되는 맵

    public String getModelName() {
        String simpleName = this.getClass().getSimpleName(); // 현재 객체의 클래스의 간단한 이름을 가져옴
        // this : 현재 객체 참조
        // getClass() : 현재 객체의 class 객체를 반환, class 객체는 Java의 Reflection API의 일부로, 클래스에 대한 메타데이터 제공
        // getSimpleName() : 클래스의 간단한 이름 반환, 예를 들어 com.demo.myclass라면 myclass만 반환

        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1); // 첫 글자를 소문자로 변환하고, 나머지 이름을 그대로 이어 붙임
        // simpleName.chartAt(0) : 클래스 이름의 첫 번째 글자를 가져옴
        // Character.toLowerCase() : 주어진 문자를 소문자로 변환
        // substring(1) : 첫 번째 글자를 빼고 나머지 부분 반환
    }
    // java 객체의 클래스 이름을 가져와서, 그 이름의 첫 글자를 소문자로 변환한 문자열을 반환하는 기능을 제공
}
