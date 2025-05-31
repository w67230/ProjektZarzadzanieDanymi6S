package net.fryc.controllers;


import net.fryc.items.Book;
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
public class BookController implements JsonSerializer<Book> {

    @PostMapping("/book")
    public ResponseEntity<String> createBook(String name, Integer authorId) {
        try {
            if(name.isEmpty() || authorId == null){
                return ResponseEntity.badRequest().body("Name and authorId cannot be null");
            }

            ArrayList<Book> list = new ArrayList<>(this.readFromFile());
            int id = !list.isEmpty() ? list.get(list.size()-1).id() + 1 : 1;
            list.add(new Book(id, name, authorId));
            this.writeToFile(JsonHelper.BOOK_JSON, list);
            return ResponseEntity.ok("Book was successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Create operation failed: ", e);
        }
    }

    @GetMapping("/books")
    public List<Book> getAll() {
        try {
            return this.readFromFile();
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @GetMapping("/book")
    public Book get(int id) {
        try {
            return this.readFromFile().stream().filter(book -> book.id() == id).findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/book")
    public ResponseEntity<String> deleteBook(int id) {
        try {
            ArrayList<Book> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(book -> {
                return book.id() == id;
            });
            this.writeToFile(JsonHelper.BOOK_JSON, list);
            return bl ? ResponseEntity.ok("Book was successfully deleted") : ResponseEntity.badRequest().body("Book with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    public void writeToFile(File file, List<Book> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(JsonHelper.BOOK_JSON, itemList);
    }

    public List<Book> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(JsonHelper.BOOK_JSON, Book[].class));
    }

}
