package net.fryc.controllers;

import net.fryc.items.Order;
import net.fryc.json.JsonHelper;
import net.fryc.json.JsonSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController implements JsonSerializer<Order> {

    private static final File ITEM_FILE = JsonHelper.ORDER_JSON;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(String userLogin, Integer offerId, Integer amount) {
        try {
            if(userLogin == null || offerId == null || amount == null){
                return ResponseEntity.badRequest().body("User login, offer id and amount cannot be null");
            }

            ArrayList<Order> list = new ArrayList<>(this.readFromFile());
            int id = !list.isEmpty() ? list.get(list.size()-1).id() + 1 : 1;
            list.add(new Order(id, userLogin, offerId, amount));
            this.writeToFile(list);
            return ResponseEntity.ok("Order was successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Create operation failed: ", e);
        }
    }


    @GetMapping("/orders")
    public List<Order> getAll() {
        try {
            return this.readFromFile();
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }


    @GetMapping("/order")
    public Order get(int id) {
        try {
            return this.readFromFile().stream().filter(order -> order.id() == id).findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Get operation failed: ", e);
        }
    }

    @DeleteMapping("/order")
    public ResponseEntity<String> deleteOrder(int id) {
        try {
            ArrayList<Order> list = new ArrayList<>(this.readFromFile());
            boolean bl = list.removeIf(order -> {
                return order.id() == id;
            });
            this.writeToFile(list);
            return bl ? ResponseEntity.ok("Order was successfully deleted") : ResponseEntity.badRequest().body("Order with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Delete operation failed: ", e);
        }
    }

    @PutMapping("/order")
    public ResponseEntity<String> replaceOrder(Integer id, String userLogin, Integer offerId, Integer amount){
        try {
            if(id == null){
                return ResponseEntity.badRequest().body("Order id cannot be null");
            }

            ArrayList<Order> list = new ArrayList<>(this.readFromFile());
            boolean bl = false;
            for(Order order : list){
                if(order.id() == id){
                    String rLogin = userLogin == null ? order.userLogin() : userLogin;
                    int rOffer = offerId == null ? order.offerId() : offerId;
                    int rAmount = amount == null ? order.amount() : amount;
                    list.set(list.indexOf(order), new Order(id, rLogin, rOffer, rAmount));
                    bl = true;
                    break;
                }
            }
            if(bl){
                this.writeToFile(list);
                return ResponseEntity.ok("Order was successfully updated");
            }
            
            return ResponseEntity.badRequest().body("Order with following id: " + id + " was not found");
        } catch (Exception e) {
            throw new RuntimeException("Update operation failed: ", e);
        }
    }

    public void writeToFile(List<Order> itemList) throws IOException {
        JsonHelper.MAPPER.writerWithDefaultPrettyPrinter().writeValue(ITEM_FILE, itemList);
    }

    public List<Order> readFromFile() throws IOException {
        return List.of(JsonHelper.MAPPER.readValue(ITEM_FILE, Order[].class));
    }
}
