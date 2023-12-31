package com.server.domain.account.repository;

import com.server.domain.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from Account a join Member m on m.account = a where m.memberId = :memberId")
    Optional<Account> findByMemberId(Long memberId);
}