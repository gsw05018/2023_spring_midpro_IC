package com.sbspro.midProject.base.security;

import com.sbspro.midProject.member.entity.Member;
import com.sbspro.midProject.member.repositroy.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
            // 사용자 이름을 기준으로 사용자 정보를 조회
             new RuntimeException("username(%s) not found".formatted(username)));
            // 조회된 사용자가 없으면 UsernameNotFoundException 발생

        // orElseThrow : Optional 클래스에 정의된 method로 값이 존재하지 않을 경우 예외 발생

        return new User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
        // Spring Security의 'UserDetails' 인터페이스를 구현하는 'User' 객체 생성
        // 'User' 객체는 사용자의 이름, 비밀번호, 그리고 권한 포함
        // member.getGrantedAuthorities메서드는 사용자의 권한을 반환
    }
}