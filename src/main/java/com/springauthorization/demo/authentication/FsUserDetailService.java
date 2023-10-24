package com.springauthorization.demo.authentication;

import com.springauthorization.demo.dao.UserResult;
import com.springauthorization.demo.entity.UserRole;
import com.springauthorization.demo.entity.Users;
import com.springauthorization.demo.repository.UserQueryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FsUserDetailService implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Transactional
    @PostConstruct
    void init() {

        Users users = Users.builder()
                .id("admin")
                .userPwd("{noop}1111")
                .userName("admin")
                .userEmail("admin@abc.com")
                .isUserExpired(false)
                .isUserPwdExpired(false)
                .build();
        UserRole userRole = UserRole.builder().roleName("ADMIN")
                .id("123")
                .userId("admin")
                .build();

        userQueryRepository.initData(users, userRole);

    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserResult user = userQueryRepository.getUserInfo(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        List<String> roleNames = user.getRoleNames();
        String[] rolesArray = roleNames.toArray(new String[roleNames.size()]);
        for (int i = 0; i < rolesArray.length; i++) {
            rolesArray[i] = "ROLE_" + rolesArray[i];
        }

        return new User(user.getUserId(), user.getUserPwd(), true, !user.getIsUserExpired(), !user.getIsUserPwdExpired(), true, AuthorityUtils.createAuthorityList(rolesArray));

    }
}
