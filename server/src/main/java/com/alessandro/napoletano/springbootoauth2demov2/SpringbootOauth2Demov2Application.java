package com.alessandro.napoletano.springbootoauth2demov2;

import com.alessandro.napoletano.springbootoauth2demov2.config.AppProperties;
import com.alessandro.napoletano.springbootoauth2demov2.model.ImportedLine;
import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.TempForReservation;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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

    @Bean
    CommandLineRunner runner(IntroService service) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<ImportedLine> typeReference = new TypeReference<ImportedLine>(){};

            URL url = TypeReference.class.getResource("/json");
            File folder = new File(url.toURI());
            File[] listOfFiles = folder.listFiles();
            List<String> lineFilesNames = new ArrayList<>();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    lineFilesNames.add(listOfFiles[i].getName());
                }
            }

            for(String filename : lineFilesNames){
                InputStream inputStream = TypeReference.class.getResourceAsStream("/json/" + filename);
                try {
                    ImportedLine lineToImport = mapper.readValue(inputStream,typeReference);
                    service.save(lineToImport);
                } catch (IOException e){
                    System.out.println("Unable to save line: " + e.getMessage());
                }
            }
        };
    }
}
