package net.fryc.controllers;


import net.fryc.items.Author;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
public class AuthorController implements JsonSerializer<Author> {

    private static final File ITEM_FILE = JsonHelper.AUTHOR_JSON;

    @PostMapping("/author")
    public ResponseEntity<String> createAuthor(@RequestParam(defaultValue = "") String firstName, @RequestParam(defaultValue = "") String lastName, @RequestParam(defaultValue = "") String birthDate, @Nullable String deathDate) {
        try {
            if(firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()){
                return ResponseEntity.badRequest().body("First name, last name and birth date cannot be empty");
            }

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy:HH", Locale.ENGLISH);
            Date bDate = format.parse(birthDate+":12");
            Date dDate = deathDate != null ? format.parse(deathDate+":12") : null;

            ArrayList<Author> list = new ArrayList<>(this.readFromFile());
            int id = !list.isEmpty() ? list.get(list.size()-1).id() + 1 : 1;
            list.add(new Author(id, firstName, lastName, bDate, dDate));
            this.writeToFile(list);
            return ResponseEntity.ok("Author was successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Create operation failed: ", e);
        }
    }

    @GetMapping("/authors")
    public List<Author> getAll() {
        try {
            return this.readFromFile();
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @GetMapping("/author")
    public Author get(@RequestParam(defaultValue = "1") int id) {
        try {
            return this.readFromFile().stream().filter(author -> author.id() == id).findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/author")
    public ResponseEntity<String> deleteAuthor(@RequestParam(defaultValue = "1") int id) {
        try {
            ArrayList<Author> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(author -> {
                return author.id() == id;
            });
            this.writeToFile(list);
            return bl ? ResponseEntity.ok("Author was successfully deleted") : ResponseEntity.badRequest().body("Author with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    @PutMapping("/author")
    public ResponseEntity<String> replaceAuthor(Integer id, @RequestParam(defaultValue = "") String firstName, @RequestParam(defaultValue = "") String lastName, @RequestParam(defaultValue = "") String birthDate, @Nullable String deathDate){
        try {
            if(id == null){
                return ResponseEntity.badRequest().body("Author id cannot be null");
            }

            ArrayList<Author> list = new ArrayList<>(this.readFromFile());
            boolean bl = false;
            for(Author author : list){
                if(author.id() == id){
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy:HH", Locale.ENGLISH);
                    String fName = firstName.isEmpty() ? author.firstName() : firstName;
                    String lName = lastName.isEmpty() ? author.lastName() : lastName;
                    Date bDate = birthDate.isEmpty() ? author.birthDate() : format.parse(birthDate+":12");
                    Date dDate = deathDate == null ? author.deathDate() : format.parse(deathDate+":12");
                    list.set(list.indexOf(author), new Author(id, fName, lName, bDate, dDate));
                    bl = true;
                    break;
                }
            }
            if(bl){
                this.writeToFile(list);
                return ResponseEntity.ok("Author was successfully updated");
            }

            return ResponseEntity.badRequest().body("Author with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Update operation failed: ", e);
        }
    }

    public void writeToFile(List<Author> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<Author> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, Author[].class));
    }

}
