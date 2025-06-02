package net.fryc.controllers;


import net.fryc.items.Book;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookController implements JsonSerializer<Book> {

    private static final File ITEM_FILE = JsonHelper.BOOK_JSON;

    @PostMapping("/book")
    public ResponseEntity<String> createBook(@RequestParam(defaultValue = "") String name, Integer authorId) {
        try {
            if(name.isEmpty() || authorId == null){
                return ResponseEntity.badRequest().body("Name and authorId cannot be null");
            }

            ArrayList<Book> list = new ArrayList<>(this.readFromFile());
            int id = !list.isEmpty() ? list.get(list.size()-1).id() + 1 : 1;
            list.add(new Book(id, name, authorId));
            this.writeToFile(list);
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
    public Book get(@RequestParam(defaultValue = "1") int id) {
        try {
            return this.readFromFile().stream().filter(book -> book.id() == id).findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/book")
    public ResponseEntity<String> deleteBook(@RequestParam(defaultValue = "1") int id) {
        try {
            ArrayList<Book> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(book -> {
                return book.id() == id;
            });
            this.writeToFile(list);
            return bl ? ResponseEntity.ok("Book was successfully deleted") : ResponseEntity.badRequest().body("Book with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    @PutMapping("/book")
    public ResponseEntity<String> replaceBook(Integer id, @RequestParam(defaultValue = "") String name, Integer authorId){
        try {
            if(id == null){
                return ResponseEntity.badRequest().body("Book id cannot be null");
            }

            ArrayList<Book> list = new ArrayList<>(this.readFromFile());
            boolean bl = false;
            for(Book book : list){
                if(book.id() == id){
                    String rName = name.isEmpty() ? book.name() : name;
                    int rAuthor = authorId == null ? book.authorId() : authorId;
                    list.set(list.indexOf(book), new Book(id, rName, rAuthor));
                    bl = true;
                    break;
                }
            }
            if(bl){
                this.writeToFile(list);
                return ResponseEntity.ok("Book was successfully updated");
            }

            return ResponseEntity.badRequest().body("Book with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Update operation failed: ", e);
        }
    }

    public void writeToFile(List<Book> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<Book> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, Book[].class));
    }

}
