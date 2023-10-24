package com.springauthorization.demo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springauthorization.demo.dao.UserResult;
import com.springauthorization.demo.entity.QUserRole;
import com.springauthorization.demo.entity.Users;
import com.springauthorization.demo.entity.UserRole;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.springauthorization.demo.entity.QUsers.users;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    public UserResult getUserInfo(String userId) {
        return queryFactory
                .select(Projections.constructor(UserResult.class,
                        users))
                .from(users)
                .leftJoin(users.userRoles, QUserRole.userRole)
                .where(users.id.eq(userId))
                .fetchOne();
    }

    @Transactional
    public void initData(Users user, UserRole userRole) {
        Users checkUser = queryFactory.selectFrom(users).where(users.id.eq(user.getId())).fetchOne();
        if (checkUser == null) {

            em.persist(user);
        }
        UserRole checkUserRole = queryFactory.selectFrom(QUserRole.userRole).where(QUserRole.userRole.id.eq(userRole.getId())).fetchOne();
        if (checkUserRole == null) {

            em.persist(userRole);
        }
    }
}
