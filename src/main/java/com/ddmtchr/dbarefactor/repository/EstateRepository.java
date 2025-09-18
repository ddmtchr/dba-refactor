package com.ddmtchr.dbarefactor.repository;

import com.ddmtchr.dbarefactor.entity.Estate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
}
