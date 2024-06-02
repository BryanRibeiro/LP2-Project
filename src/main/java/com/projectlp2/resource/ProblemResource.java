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

import com.projectlp2.dto.ProblemDTO;
import com.projectlp2.dto.ErrorResponse;
import com.projectlp2.entity.Problem;
import com.projectlp2.repository.ProblemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Path("/activity")
public class ProblemResource {

    @Inject
    ProblemRepository repository;

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
}
