package net.fryc.controllers;

import net.fryc.items.Offer;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OfferController implements JsonSerializer<Offer> {

    private static final File ITEM_FILE = JsonHelper.OFFER_JSON;

    @PostMapping("/offer")
    public ResponseEntity<String> createOffer(Integer bookId, Float price, Integer amount) {
        try {
            if(bookId == null || price == null || amount == null){
                return ResponseEntity.badRequest().body("Book id, price and amount cannot be null");
            }

            ArrayList<Offer> list = new ArrayList<>(this.readFromFile());
            int id = !list.isEmpty() ? list.get(list.size()-1).id() + 1 : 1;
            list.add(new Offer(id, bookId, price, amount));
            this.writeToFile(list);
            return ResponseEntity.ok("Offer was successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Create operation failed: ", e);
        }
    }


    @GetMapping("/offers")
    public List<Offer> getAll() {
        try {
            return this.readFromFile();
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }


    @GetMapping("/offer")
    public Offer get(@RequestParam(defaultValue = "1") int id) {
        try {
            return this.readFromFile().stream().filter(offer -> offer.id() == id).findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/offer")
    public ResponseEntity<String> deleteOffer(@RequestParam(defaultValue = "1") int id) {
        try {
            ArrayList<Offer> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(offer -> {
                return offer.id() == id;
            });
            this.writeToFile(list);
            return bl ? ResponseEntity.ok("Offer was successfully deleted") : ResponseEntity.badRequest().body("Offer with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    @PutMapping("/offer")
    public ResponseEntity<String> replaceOffer(Integer id, Integer bookId, Float price, Integer amount){
        try {
            if(id == null){
                return ResponseEntity.badRequest().body("Offer id cannot be null");
            }

            ArrayList<Offer> list = new ArrayList<>(this.readFromFile());
            boolean bl = false;
            for(Offer offer : list){
                if(offer.id() == id){
                    int rBook = bookId == null ? offer.bookId() : bookId;
                    float rPrice = price == null ? offer.price() : price;
                    int rAmount = amount == null ? offer.amount() : amount;
                    list.set(list.indexOf(offer), new Offer(id, rBook, rPrice, rAmount));
                    bl = true;
                    break;
                }
            }
            if(bl){
                this.writeToFile(list);
                return ResponseEntity.ok("Offer was successfully updated");
            }

            return ResponseEntity.badRequest().body("Offer with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Update operation failed: ", e);
        }
    }

    public void writeToFile(List<Offer> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<Offer> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, Offer[].class));
    }
}
