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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
            TypeReference<Line> typeReference = new TypeReference<Line>(){};

            URL url = TypeReference.class.getResource("/json");
            File folder = new File(url.toURI());
            File[] listOfFiles = folder.listFiles();
            List<String> lineFilesNames = new ArrayList<>();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    lineFilesNames.add(listOfFiles[i].getName());
                }
            }

            List<Line> lines = new ArrayList<>();
            for(String filename : lineFilesNames){
                InputStream inputStream = TypeReference.class.getResourceAsStream("/json/" + filename);
                try {
                    Line lineToImport = mapper.readValue(inputStream,typeReference);
                    lines.add(lineToImport);
                } catch (IOException e){
                    System.out.println("Unable to save line: " + e.getMessage());
                }
            }

            service.save(lines);

            for(Line l : lines) {
                service.setLineAdmin(l.getName(), l.getEmail());
            }
        };
    }*/

    /*@Bean
    CommandLineRunner runner(IntroService service) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Line>> typeReference = new TypeReference<List<Line>>(){};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/json/dancing_larvae.json");
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
