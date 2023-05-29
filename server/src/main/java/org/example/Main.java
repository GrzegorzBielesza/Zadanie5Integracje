package org.example;

import jakarta.xml.ws.Endpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        //APLIKACJA KONSOLOWA
//        CsvFileReader.readFile("example.csv");
        //APLIKACJA OKIENKOWA
//        new WindowFileReader();
        //APLIKACJA SOAP
//        Endpoint.publish("http://localhost:8080/computers", new org.example.SoapBean());
//        H2DatabaseInitializer.initializeDatabase();
        //APLIKACJA REST
        SpringApplication.run(Main.class, args);
        H2DatabaseInitializer.initializeDatabase();
    }
}
