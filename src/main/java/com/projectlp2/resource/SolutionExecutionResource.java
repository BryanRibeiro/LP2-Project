package com.projectlp2.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

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

        List<TestCase> testCases = testCaseRepository.find("problem_id", problem.getId()).list();
        boolean allTestsPassed = true;

        // Verificar se a solução passa em todos os casos de teste
        for (TestCase testCase : testCases) {
            if (!runTestCase(submissionDTO, testCase)) {
                allTestsPassed = false;
                break;
            }
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSolutionExecutions() {
        List<SolutionExecution> executions = executionRepository.listAll();
        List<SolutionResponseDTO> executionDTOs = executions.stream().map(execution -> {
            SolutionResponseDTO dto = new SolutionResponseDTO();
            dto.setAuthor(execution.getAuthor());
            dto.setFilename(execution.getFilename());
            dto.setProblemCode(execution.getProblemCode());
            dto.setStatus(execution.getStatus());
            dto.setCreatedAt(execution.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
        return Response.ok(executionDTOs).build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response uploadSolution(@MultipartForm MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        String author = extractFormField(uploadForm, "author");
        String problemCode = extractFormField(uploadForm, "problemCode");
        String filename = extractFormField(uploadForm, "filename");
        String sourceCode = extractFormField(uploadForm, "sourceCode");

        Optional<Problem> problemOptional = problemRepository.find("problemCode", problemCode).firstResultOptional();
        if (!problemOptional.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR003", "Problem not found")
            ).build();
        }

        Problem problem = problemOptional.get();

        if (!filename.equals(problem.getFilename() + ".py")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR004", "Filename does not match registered problem filename")
            ).build();
        }

        List<TestCase> testCases = testCaseRepository.find("problem.id", problem.getId()).list();
        boolean allTestsPassed = true;

        for (TestCase testCase : testCases) {
            boolean testPassed = runTestCase(author, problemCode, filename, sourceCode, testCase);
            if (!testPassed) {
                allTestsPassed = false;
                break;
            }
        }

        SolutionExecution submission = new SolutionExecution();
        submission.setAuthor(author);
        submission.setFilename(filename);
        submission.setProblemCode(problemCode);
        submission.setSourceCode(sourceCode);
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

    private String extractFormField(Map<String, List<InputPart>> uploadForm, String fieldName) {
        try {
            List<InputPart> inputParts = uploadForm.get(fieldName);
            if (inputParts != null && !inputParts.isEmpty()) {
                return inputParts.get(0).getBodyAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para executar um caso de teste e comparar a saída com a saída esperada
    private boolean runTestCase(SolutionSubmissionDTO submissionDTO, TestCase testCase) {
        return runTestCase(submissionDTO.getAuthor(), submissionDTO.getProblemCode(), submissionDTO.getFilename(), submissionDTO.getSourceCode(), testCase);
    }

    private boolean runTestCase(String author, String problemCode, String filename, String sourceCode, TestCase testCase) {
        // Diretório para salvar o código fonte e os arquivos de entrada e saída
        String directory = "temp";
        File tempDir = new File(directory);
        tempDir.mkdir();

        // Salvar o código fonte do usuário em um arquivo temporário
        String sourceCodePath = directory + "/" + filename;
        try {
            Files.write(Paths.get(sourceCodePath), sourceCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Ler o conteúdo do arquivo de entrada registrado
        String inputFilePath = directory + "/" + testCase.getInputFile();
        String expectedOutputFilePath = directory + "/" + testCase.getExpectedOutputFile();
        String inputContent = null;
        String expectedOutputContent = null;
        try {
            inputContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            expectedOutputContent = new String(Files.readAllBytes(Paths.get(expectedOutputFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Executar o código fonte com o arquivo de entrada
        try {
            ProcessBuilder pb = new ProcessBuilder("python", sourceCodePath);
            pb.redirectInput(new File(inputFilePath));
            pb.redirectOutput(new File(directory + "/actualOutput.txt"));
            Process process = pb.start();
            process.waitFor();

            // Comparar a saída com a saída esperada
            String actualOutput = new String(Files.readAllBytes(Paths.get(directory + "/actualOutput.txt"))).trim();
            String expectedOutput = new String(Files.readAllBytes(Paths.get(expectedOutputFilePath))).trim();
            return actualOutput.equals(expectedOutput);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}