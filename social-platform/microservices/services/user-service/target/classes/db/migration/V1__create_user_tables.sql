-- ============================================================
-- USER ACCOUNT
-- ============================================================

CREATE TABLE users
(
    id              VARCHAR(36)  NOT NULL,
    username        VARCHAR(30)  NOT NULL,
    email           VARCHAR(254) NOT NULL,
    password_hash   VARCHAR(100) NOT NULL,

    role            VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,

    email_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at   TIMESTAMP(6) NULL,

    version         BIGINT       NOT NULL DEFAULT 0,

    created_at      TIMESTAMP(6) NOT NULL,
    updated_at      TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),

    INDEX idx_users_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ============================================================
-- USER PROFILE
-- ============================================================

CREATE TABLE user_profiles
(
    id                VARCHAR(36)  NOT NULL,
    user_id           VARCHAR(36)  NOT NULL,

    full_name         VARCHAR(100) NOT NULL,
    bio               VARCHAR(500) NULL,
    website           VARCHAR(2048) NULL,
    profile_media_id  VARCHAR(36)  NULL,

    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_user_profiles PRIMARY KEY (id),
    CONSTRAINT uk_user_profiles_user_id UNIQUE (user_id),

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ============================================================
-- USER SETTINGS
-- ============================================================

CREATE TABLE user_settings
(
    id            VARCHAR(36) NOT NULL,
    user_id       VARCHAR(36) NOT NULL,

    privacy_type  VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',

    created_at    TIMESTAMP(6) NOT NULL,
    updated_at    TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_user_settings PRIMARY KEY (id),
    CONSTRAINT uk_user_settings_user_id UNIQUE (user_id),

    CONSTRAINT fk_user_settings_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ============================================================
-- REFRESH TOKENS
-- ============================================================

CREATE TABLE refresh_tokens
(
    id                    VARCHAR(36) NOT NULL,
    user_id               VARCHAR(36) NOT NULL,

    token_hash            VARCHAR(64) NOT NULL,
    token_family_id       VARCHAR(36) NOT NULL,

    expires_at            TIMESTAMP(6) NOT NULL,
    revoked_at            TIMESTAMP(6) NULL,

    replaced_by_token_id  VARCHAR(36) NULL,

    created_at            TIMESTAMP(6) NOT NULL,
    updated_at            TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_refresh_tokens_replaced_by
        FOREIGN KEY (replaced_by_token_id)
        REFERENCES refresh_tokens (id),

    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_family_id (token_family_id),
    INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;