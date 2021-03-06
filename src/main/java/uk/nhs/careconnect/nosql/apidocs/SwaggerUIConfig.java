package uk.nhs.careconnect.nosql.apidocs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerUIConfig {

    @Primary
    @Bean
    @Lazy
    public SwaggerResourcesProvider swaggerResourcesProvider(ServiceDefinitionsContext definitionContext ) {
        System.out.println("swaggerResourceProvider");
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>();
            resources.clear();
            resources.addAll(definitionContext.getSwaggerDefinitions());
            return resources;
        };
    }

}
