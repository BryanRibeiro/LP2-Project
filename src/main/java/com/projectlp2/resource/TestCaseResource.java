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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/tc")
public class TestCaseResource {

    @Inject
    ProblemRepository problemRepository;

    @Inject
    TestCaseRepository testCaseRepository;

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response uploadTestCase(@MultipartForm MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        String problemCode = extractFormField(uploadForm, "problemCode");
        String inputFilePath = saveFile(uploadForm, "inputFile");
        String expectedOutputFilePath = saveFile(uploadForm, "expectedOutputFile");

        Problem problem = problemRepository.find("problemCode", problemCode).firstResult();
        if (problem == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse("ERR003", "Problem code does not exist")
            ).build();
        }

        TestCase testCase = new TestCase();
        testCase.setInputFile(inputFilePath);
        testCase.setExpectedOutputFile(expectedOutputFilePath);
        testCase.setProblem(problem);

        testCaseRepository.persist(testCase);

        return Response.status(Response.Status.CREATED).entity(testCase).build();
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

    private String saveFile(Map<String, List<InputPart>> uploadForm, String fieldName) {
        try {
            List<InputPart> inputParts = uploadForm.get(fieldName);
            if (inputParts != null && !inputParts.isEmpty()) {
                InputPart inputPart = inputParts.get(0);
                String filename = inputPart.getHeaders().getFirst("Content-Disposition")
                        .split(";")[2].trim().split("=")[1];
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                File tempFile = File.createTempFile("temp", ".tmp");
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return tempFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}