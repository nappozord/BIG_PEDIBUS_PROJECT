package com.alessandro.napoletano.springbootoauth2demov2;

import com.alessandro.napoletano.springbootoauth2demov2.config.AppProperties;
import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.service.IntroService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SpringbootOauth2Demov2Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringbootOauth2Demov2Application.class);
        application.run(args);
    }

    /*@Bean
    CommandLineRunner runner(IntroService service) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Line>> typeReference = new TypeReference<List<Line>>(){};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/json/users.json");
            try {
                List<Line> lines = mapper.readValue(inputStream,typeReference);
                service.save(lines);
                System.out.println("Users Saved!");
            } catch (IOException e){
                System.out.println("Unable toUser save users: " + e.getMessage());
            }
        };
    }*/

}
