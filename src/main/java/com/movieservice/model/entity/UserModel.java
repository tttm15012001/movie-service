package com.movieservice.model.entity;

import com.movieservice.dto.response.UserDtoResponse;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_USER;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_USER)
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password" ,nullable = false)
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public UserDtoResponse toUserDtoResponse() {
        return UserDtoResponse.builder()
                .email(this.email)
                .role(RoleEnum.getRoleByCode(this.role.toString()))
                .build();
    }
}
