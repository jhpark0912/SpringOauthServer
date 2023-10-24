package com.springauthorization.demo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClient is a Querydsl query type for Client
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClient extends EntityPathBase<Client> {

    private static final long serialVersionUID = -396789190L;

    public static final QClient client = new QClient("client");

    public final StringPath authorizationGrantTypes = createString("authorizationGrantTypes");

    public final StringPath clientAuthenticationMethods = createString("clientAuthenticationMethods");

    public final StringPath clientId = createString("clientId");

    public final DateTimePath<java.time.Instant> clientIdIssuedAt = createDateTime("clientIdIssuedAt", java.time.Instant.class);

    public final StringPath clientName = createString("clientName");

    public final StringPath clientSecret = createString("clientSecret");

    public final DateTimePath<java.time.Instant> clientSecretExpiresAt = createDateTime("clientSecretExpiresAt", java.time.Instant.class);

    public final StringPath clientSettings = createString("clientSettings");

    public final StringPath id = createString("id");

    public final StringPath postLogoutRedirectUris = createString("postLogoutRedirectUris");

    public final StringPath redirectUris = createString("redirectUris");

    public final StringPath scopes = createString("scopes");

    public final StringPath tokenSettings = createString("tokenSettings");

    public QClient(String variable) {
        super(Client.class, forVariable(variable));
    }

    public QClient(Path<? extends Client> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClient(PathMetadata metadata) {
        super(Client.class, metadata);
    }

}

