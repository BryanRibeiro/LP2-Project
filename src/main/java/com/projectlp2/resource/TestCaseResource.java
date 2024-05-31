package com.projectlp2.resource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.projectlp2.dto.TestCaseDTO;
import com.projectlp2.dto.ErrorResponse;
import com.projectlp2.entity.Problem;
import com.projectlp2.entity.TestCase;
import com.projectlp2.repository.ProblemRepository;
import com.projectlp2.repository.TestCaseRepository;

@Path("/tc")
public class TestCaseResource {

    @Inject
    TestCaseRepository repository;

    @Inject
    ProblemRepository problemRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerTestCase(@Valid TestCaseDTO testCaseDTO) {
        Problem problem = problemRepository.find("problemCode", testCaseDTO.getProblemCode()).firstResult();
        if (problem == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR002", "Problem not found")
            ).build();
        }

        TestCase testCase = new TestCase();
        testCase.setProblemId(problem.getId());
        testCase.setInputFile(testCaseDTO.getInputFile());
        testCase.setExpectedOutputFile(testCaseDTO.getExpectedOutputFile());

        repository.persist(testCase);
        return Response.status(Response.Status.CREATED).entity(testCaseDTO).build();
    }
}
