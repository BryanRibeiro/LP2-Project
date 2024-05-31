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

import com.projectlp2.dto.ProblemDTO;
import com.projectlp2.dto.ErrorResponse;
import com.projectlp2.entity.Problem;
import com.projectlp2.repository.ProblemRepository;

@Path("/activity")
public class ProblemResource {

    @Inject
    ProblemRepository repository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerProblem(@Valid ProblemDTO problemDTO) {
        if (repository.find("problemCode", problemDTO.getProblemCode()).firstResultOptional().isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR001", "Problem code already exists")
            ).build();
        }

        Problem problem = new Problem();
        problem.setFilename(problemDTO.getFilename());
        problem.setProblemCode(problemDTO.getProblemCode());
        problem.setLps(problemDTO.getLps());

        repository.persist(problem);
        return Response.status(Response.Status.CREATED).entity(problemDTO).build();
    }
}
