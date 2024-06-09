package com.projectlp2.resource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.projectlp2.dto.ProblemDTO;
import com.projectlp2.dto.ErrorResponse;
import com.projectlp2.entity.Problem;
import com.projectlp2.entity.SolutionExecution;
import com.projectlp2.entity.TestCase;
import com.projectlp2.repository.ProblemRepository;
import com.projectlp2.repository.SolutionExecutionRepository;
import com.projectlp2.repository.TestCaseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Path("/activity")
public class ProblemResource {

    @Inject
    ProblemRepository repository;

    @Inject
    ProblemRepository problemRepository;

    @Inject
    SolutionExecutionRepository solutionExecutionRepository;

    @Inject
    TestCaseRepository testCaseRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProblems() {
        List<Problem> problems = repository.listAll();
        List<ProblemDTO> problemDTOs = problems.stream().map(problem -> {
            ProblemDTO dto = new ProblemDTO();
            dto.setFilename(problem.getFilename());
            dto.setProblemCode(problem.getProblemCode());
            dto.setLps(problem.getLps());
            return dto;
        }).collect(Collectors.toList());
        return Response.ok(problemDTOs).build();
    }

    @GET
    @Path("/{problemCode}")
    public Response getSolutionsByProblemCode(@PathParam("problemCode") String problemCode) {
        List<SolutionExecution> solutions = solutionExecutionRepository.find("problemCode", problemCode).list();
        if (solutions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(solutions).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerProblem(@Valid ProblemDTO problemDTO) {
        // Verificar se já existe um problema com o mesmo código
        if (repository.find("problemCode", problemDTO.getProblemCode()).firstResultOptional().isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR001", "Problem code already exists")
            ).build();
        }

        // Verificar se já existe um problema com o mesmo filename
        if (repository.find("filename", problemDTO.getFilename()).firstResultOptional().isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR002", "Filename already exists")
            ).build();
        }

        // Criar e persistir a entidade Problem
        Problem problem = new Problem();
        problem.setFilename(problemDTO.getFilename());
        problem.setProblemCode(problemDTO.getProblemCode());
        problem.setLps(problemDTO.getLps());

        repository.persist(problem);
        return Response.status(Response.Status.CREATED).entity(problemDTO).build();
    }

    @DELETE
    @Path("/{problemCode}")
    @Transactional
    public Response removeActivity(@PathParam("problemCode") String problemCode) {
        // Encontrar o problema com base no código fornecido
        Problem problem = problemRepository.find("problemCode", problemCode).firstResult();

        // Verificar se o problema existe
        if (problem == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Encontrar todas as soluções associadas ao problema
        List<SolutionExecution> solutions = solutionExecutionRepository.find("problemCode = ?1", problem.getProblemCode()).list();

        // Excluir todas as soluções associadas ao problema
        for (SolutionExecution solution : solutions) {
            solutionExecutionRepository.delete(solution);
        }

        // Encontrar todos os casos de teste associados ao problema
        List<TestCase> testCases = testCaseRepository.find("problem_id = ?1", problem.getId()).list();

        // Excluir todos os casos de teste associados ao problema
        for (TestCase testCase : testCases) {
            testCaseRepository.delete(testCase);
        }

        // Excluir o problema
        problemRepository.delete(problem);

        return Response.ok().build();
    }
}
