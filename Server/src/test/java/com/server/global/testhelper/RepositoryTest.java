package com.server.global.testhelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

@DataJpaTest
public abstract class RepositoryTest {

    @Autowired protected EntityManager em;
}
