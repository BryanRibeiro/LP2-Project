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

import com.projectlp2.dto.SolutionSubmissionDTO;
import com.projectlp2.dto.SolutionResponseDTO;
import com.projectlp2.dto.ErrorResponse;
import com.projectlp2.entity.Problem;
import com.projectlp2.entity.SolutionExecution;
import com.projectlp2.entity.TestCase;
import com.projectlp2.repository.ProblemRepository;
import com.projectlp2.repository.SolutionExecutionRepository;
import com.projectlp2.repository.TestCaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("/activity/solution")
public class SolutionExecutionResource {

    @Inject
    SolutionExecutionRepository executionRepository;

    @Inject
    ProblemRepository problemRepository;

    @Inject
    TestCaseRepository testCaseRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response submitSolution(@Valid SolutionSubmissionDTO submissionDTO) {
        Optional<Problem> problemOptional = problemRepository.find("problemCode", submissionDTO.getProblemCode()).firstResultOptional();
        if (!problemOptional.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR003", "Problem not found")
            ).build();
        }

        Problem problem = problemOptional.get();

        if (!submissionDTO.getFilename().equals(problem.getFilename() + ".py")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR004", "Filename does not match registered problem filename")
            ).build();
        }

        List<TestCase> testCases = testCaseRepository.find("problemId", problem.getId()).list();
        boolean allTestsPassed = true;

        for (TestCase testCase : testCases) {
            // Aqui você executa os casos de teste e verifica se a solução passa em todos
        }

        SolutionExecution submission = new SolutionExecution();
        submission.setAuthor(submissionDTO.getAuthor());
        submission.setFilename(submissionDTO.getFilename());
        submission.setProblemCode(submissionDTO.getProblemCode());
        submission.setSourceCode(submissionDTO.getSourceCode());
        submission.setStatus(allTestsPassed ? "SUCCESS" : "FAIL");
        submission.setCreatedAt(LocalDateTime.now());
        executionRepository.persist(submission);

        SolutionResponseDTO responseDTO = new SolutionResponseDTO();
        responseDTO.setAuthor(submission.getAuthor());
        responseDTO.setFilename(submission.getFilename());
        responseDTO.setProblemCode(submission.getProblemCode());
        responseDTO.setStatus(submission.getStatus());
        responseDTO.setCreatedAt(submission.getCreatedAt());

        return Response.status(Response.Status.CREATED).entity(responseDTO).build();
    }
}
