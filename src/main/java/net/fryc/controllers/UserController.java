package net.fryc.controllers;

import net.fryc.items.User;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController implements JsonSerializer<User> {

    private static final File ITEM_FILE = JsonHelper.USER_JSON;

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String password) {
        try {
            if(login.isEmpty() || password.isEmpty()){
                return ResponseEntity.badRequest().body("Login and password cannot be empty");
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

    @GetMapping("/correctLogin")
    public boolean isLoginCorrect(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String password) {
        try {
            return this.readFromFile().stream().anyMatch(user -> {
                return user.login().equals(login) && user.haslo().equals(password);
            });
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @GetMapping("/user")
    public boolean get(@RequestParam(defaultValue = "") String login) {
        try {
            return this.readFromFile().stream().anyMatch(user -> user.login().equals(login));
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam(defaultValue = "") String login) {
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

    @PutMapping("/user")
    public ResponseEntity<String> replaceUser(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String newLogin, @RequestParam(defaultValue = "") String password){
        try {
            if(login.isEmpty()){
                return ResponseEntity.badRequest().body("Login cannot be empty");
            }
            else if(this.readFromFile().stream().anyMatch(user -> user.login().equals(newLogin))){
                return ResponseEntity.badRequest().body("New login is occupied");
            }

            ArrayList<User> list = new ArrayList<>(this.readFromFile());
            boolean bl = false;
            for(User user : list){
                if(user.login().equals(login)){
                    String rLogin = newLogin.isEmpty() ? user.login() : newLogin;
                    String rPass = password.isEmpty() ? user.haslo() : password;
                    list.set(list.indexOf(user), new User(rLogin, rPass));
                    bl = true;
                    break;
                }
            }
            if(bl){
                this.writeToFile(list);
                return ResponseEntity.ok("User was successfully updated");
            }

            return ResponseEntity.badRequest().body("User with following login: " + login + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Update operation failed: ", e);
        }
    }

    public void writeToFile(List<User> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<User> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, User[].class));
    }
}
