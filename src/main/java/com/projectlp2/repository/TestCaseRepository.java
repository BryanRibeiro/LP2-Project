package com.projectlp2.repository;

import com.projectlp2.entity.TestCase;
import java.util.List;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestCaseRepository implements PanacheRepository<TestCase> {

    public void deleteByTestCaseId(Long testCaseId) {
        delete("id", testCaseId);
    }

    public List<TestCase> findByProblemId(Long problemId) {
        return list("problem_id", problemId);
    }
}
