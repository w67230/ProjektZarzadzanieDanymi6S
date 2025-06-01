package net.fryc.controllers;

import net.fryc.items.User;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController implements JsonSerializer<User> {

    private static final File ITEM_FILE = JsonHelper.USER_JSON;

    @PostMapping("/user")
    public ResponseEntity<String> createUser(String login, String password) {
        try {
            if(login == null || password == null){
                return ResponseEntity.badRequest().body("Login and password cannot be null");
            }

            ArrayList<User> list = new ArrayList<>(this.readFromFile());
            if(list.stream().anyMatch(user -> user.login().equals(login))){
                return ResponseEntity.badRequest().body("User with such login already exists");
            }
            list.add(new User(login, password));
            this.writeToFile(list);
            return ResponseEntity.ok("User was successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Create operation failed: ", e);
        }
    }

    /*
    @GetMapping("/users")
    public List<String> getAll() {
        try {
            List<String> list = new ArrayList<>();
            this.readFromFile().forEach(user -> {
                list.add(user.login());
            });
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }
     */

    @GetMapping("/user")
    public boolean get(String login) {
        try {
            return this.readFromFile().stream().anyMatch(user -> user.login().equals(login));
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(String login) {
        try {
            ArrayList<User> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(user -> {
                return user.login().equals(login);
            });
            this.writeToFile(list);
            return bl ? ResponseEntity.ok("User was successfully deleted") : ResponseEntity.badRequest().body("User with following login: " + login + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    public void writeToFile(List<User> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<User> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, User[].class));
    }
}
