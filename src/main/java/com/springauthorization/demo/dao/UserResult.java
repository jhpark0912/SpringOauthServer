package com.springauthorization.demo.dao;

import com.springauthorization.demo.entity.Users;
import com.springauthorization.demo.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResult {
    private String userId;
    private String userName;
    private String userPwd;
    private String userEmail;

    private Boolean isUserPwdExpired;
    private Boolean isUserExpired;

    private List<String> roleNames = new ArrayList<>();

    public UserResult(Users user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.userPwd = user.getUserPwd();
        this.userEmail = user.getUserEmail();
        this.isUserPwdExpired = user.getIsUserPwdExpired();
        this.isUserExpired = user.getIsUserExpired();
        this.roleNames = user.getUserRoles().stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }
}
