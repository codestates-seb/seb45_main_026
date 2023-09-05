package com.server.search.repository;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class MySQLSearchRepository implements SearchRepository {
	private final JPAQueryFactory queryFactory;

	public MySQLSearchRepository(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

}
