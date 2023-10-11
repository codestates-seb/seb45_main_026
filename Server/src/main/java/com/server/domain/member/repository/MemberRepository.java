package com.server.domain.member.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	Optional<Member> findByEmail(String email);

	@Query("select m.memberId from Member m where m.memberId = ?1")
	Long findMemberIdById(Long id);

	@Query("select m from Member m where m.email in ?1")
    List<Member> findAllByEmails(HashSet<String> adminEnterRoomIds);
}
