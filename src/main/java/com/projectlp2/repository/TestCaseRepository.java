package com.projectlp2.repository;

import com.projectlp2.entity.TestCase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestCaseRepository implements PanacheRepository<TestCase> {
}
