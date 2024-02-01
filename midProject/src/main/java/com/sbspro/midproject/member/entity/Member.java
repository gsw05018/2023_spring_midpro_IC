package com.sbspro.midproject.member.entity;

import com.sbspro.midproject.base.jpa.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;

    private String password;

    private String nickname;

    private String email;

    public boolean isAdmin() {
        return "admin5428".equals(username);
    }

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        if (isAdmin()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin5428"));
        }

        return grantedAuthorities;

    }

    public boolean isSocialMember() {
        return username.startsWith("KAKAO_");
    }

    public boolean isModifyAvailable() {
        return !isSocialMember();
    }

    public String getEmailForPrint() {
        if (isSocialMember()) return "-";
        return email;
    }
}



