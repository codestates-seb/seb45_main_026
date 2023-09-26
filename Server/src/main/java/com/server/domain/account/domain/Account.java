package com.server.domain.account.domain;

import com.server.domain.member.entity.Member;
import com.server.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private String name;

    private String account;

    private String bank;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Account(String name, String account, String bank, Member member) {
        this.name = name;
        this.account = account;
        this.bank = bank;
        this.member = member;
    }

    public static Account createAccount(String name, String accountNumber, String bank, Member member) {
        Account account = new Account(name, accountNumber, bank, member);

        member.updateAccount(account);

        return account;
    }
}
