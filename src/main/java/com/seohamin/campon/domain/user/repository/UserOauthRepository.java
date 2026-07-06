package com.seohamin.campon.domain.user.repository;

import com.seohamin.campon.domain.user.entity.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    Optional<UserOauth> findByProviderAndProviderUserId(final String provider, final String providerUserId);
}
