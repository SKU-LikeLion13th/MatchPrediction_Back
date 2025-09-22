package com.sku_sku.MatchPrediction.domain;

import com.sku_sku.MatchPrediction.enums.FeeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@Entity
public class Student implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String major;

    private String studentId;

    private String name;

    @Enumerated(EnumType.STRING)
    private FeeStatus feeStatus;

    public Student(String email, String major, String studentId, String name, FeeStatus feeStatus) {
        this.email = email;
        this.major = major;
        this.studentId = studentId;
        this.name = name;
        this.feeStatus = feeStatus;
    }

    // UserDetails 필수 impl
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(feeStatus.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
