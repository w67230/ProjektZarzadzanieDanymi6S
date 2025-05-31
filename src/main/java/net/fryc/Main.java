package net.fryc;

import net.fryc.json.JsonHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        JsonHelper.createJsonFilesWhenNeeded();
        SpringApplication.run(Main.class, args);
    }

}