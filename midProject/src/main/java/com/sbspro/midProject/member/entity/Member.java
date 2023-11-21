package com.sbspro.midProject.member.entity;

import com.sbspro.midProject.base.entity.baseEntity;
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
@Getter
@Setter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Member extends baseEntity {
    @Column(unique = true)
    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phoneNumber;



    public boolean isAdmin(){
        return "admin".equals(username);
    }

    public List<? extends GrantedAuthority> getGrantedAuthorities(){
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        if(isAdmin()){
            grantedAuthorities.add(new SimpleGrantedAuthority("admine"));
        }

        return grantedAuthorities;

    }

}



