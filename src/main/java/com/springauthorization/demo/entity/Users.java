package com.springauthorization.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name="users")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Users extends ModelBase{
    @Id
    @Column(name = "user_id")
    private String id;
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPwd;
    private String userEmail;

    private Boolean isUserPwdExpired;
    private Boolean isUserExpired;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<UserRole> userRoles = new ArrayList<>();
}
