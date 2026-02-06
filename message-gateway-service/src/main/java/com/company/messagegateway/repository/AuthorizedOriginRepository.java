package com.company.messagegateway.repository;

import com.company.messagegateway.entity.AuthorizedOrigin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedOriginRepository extends JpaRepository<AuthorizedOrigin, Long> {

	boolean existsByOrigin(String origin);
}