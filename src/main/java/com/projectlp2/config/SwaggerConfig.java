package com.projectlp2.config;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "LP2 Project API", version = "1.0", description = "API for LP2 Project"))
@ApplicationScoped
public class SwaggerConfig extends Application {

}
