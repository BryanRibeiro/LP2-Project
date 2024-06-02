package com.projectlp2.resource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
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

import java.util.List;
import java.util.stream.Collectors;

@Path("/tc")
public class TestCaseResource {

    @Inject
    ProblemRepository problemRepository;

    @Inject
    TestCaseRepository testCaseRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerTestCase(@Valid TestCaseDTO testCaseDTO) {
        // Verificar se o problema existe
        Problem problem = problemRepository.find("problemCode", testCaseDTO.getProblemCode()).firstResult();
        if (problem == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR003", "Problem code does not exist")
            ).build();
        }

        // Criar e persistir a entidade TestCase
        TestCase testCase = new TestCase();
        testCase.setInputFile(testCaseDTO.getInputFile());
        testCase.setExpectedOutputFile(testCaseDTO.getExpectedOutputFile());
        testCase.setProblem(problem); // Associando o problema ao caso de teste

        testCaseRepository.persist(testCase);
        return Response.status(Response.Status.CREATED).entity(testCaseDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTestCases() {
        List<TestCase> testCases = testCaseRepository.listAll();
        List<TestCaseDTO> testCaseDTOs = testCases.stream().map(testCase -> {
            TestCaseDTO dto = new TestCaseDTO();
            dto.setProblemCode(testCase.getProblem().getProblemCode());
            dto.setInputFile(testCase.getInputFile());
            dto.setExpectedOutputFile(testCase.getExpectedOutputFile());
            return dto;
        }).collect(Collectors.toList());
        return Response.ok(testCaseDTOs).build();
    }
}
