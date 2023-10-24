package com.springauthorization.demo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthorizationConsent is a Querydsl query type for AuthorizationConsent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthorizationConsent extends EntityPathBase<AuthorizationConsent> {

    private static final long serialVersionUID = 765379984L;

    public static final QAuthorizationConsent authorizationConsent = new QAuthorizationConsent("authorizationConsent");

    public final StringPath authorities = createString("authorities");

    public final StringPath principalName = createString("principalName");

    public final StringPath registeredClientId = createString("registeredClientId");

    public QAuthorizationConsent(String variable) {
        super(AuthorizationConsent.class, forVariable(variable));
    }

    public QAuthorizationConsent(Path<? extends AuthorizationConsent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthorizationConsent(PathMetadata metadata) {
        super(AuthorizationConsent.class, metadata);
    }

}

