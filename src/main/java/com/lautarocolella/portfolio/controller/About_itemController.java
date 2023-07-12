package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.About_item;
import com.lautarocolella.portfolio.service.About_itemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/about_item")
public class About_itemController {
    @Autowired
    About_itemService about_itemService;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<About_item> about_items = about_itemService.getAll();
        if(about_items == null){
            errorResponse.put("message", "Internal server error while retrieving a list of about items from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(about_items.isEmpty()) {
            errorResponse.put("message", "There are no about items in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(about_items), HttpStatus.OK);
    }

    @GetMapping("/{about_itemId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("about_itemId")long about_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(about_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<About_item> optAbout_item = about_itemService.getById(about_itemId);
        if(optAbout_item.isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optAbout_item, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody About_item about_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateAboutItem(about_item, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        About_item newAbout_item = about_itemService.saveOrUpdate(about_item);
        if(newAbout_item == null){
            errorResponse.put("message", "Internal server error while creating about item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newAbout_item, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody About_item about_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateAboutItem(about_item, 0);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        About_item updatedAbout_item = about_itemService.saveOrUpdate(about_item);
        if(updatedAbout_item == null){
            errorResponse.put("message", "Internal server error while updating about item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedAbout_item, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{about_itemId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("about_itemId")long about_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(about_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(about_itemService.getById(about_itemId).isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = about_itemService.delete(about_itemId);
        if(!delResponse.isEmpty()){
            errorResponse.put("message", delResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // this is to validate the user JWT
    @DeleteMapping("/check_token")
    public ResponseEntity<?> delete(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid json input");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public String validateAboutItem(About_item about_item, int type){
        String errorMessage = "";
        Pattern regexSimpleString = Pattern.compile("^[a-zA-Z0-9 ]+$");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(type == 0){
            Optional<About_item> optAbout_item = about_itemService.getById(about_item.getId());
            if(optAbout_item.isEmpty()){
                errorMessage = "An item with that id doesn't exist in the database";
            } else {
                if(about_item.getName().equals(optAbout_item.get().getName()) &&
                        about_item.getDescription().equals(optAbout_item.get().getDescription()) &&
                        about_item.getImage_alt().equals(optAbout_item.get().getImage_alt()) &&
                        about_item.getImage_uri().equals(optAbout_item.get().getImage_uri()) &&
                        about_item.getLink().equals(optAbout_item.get().getLink()) &&
                        about_item.getItem_type() == optAbout_item.get().getItem_type() &&
                        about_item.getDate().isEqual(optAbout_item.get().getDate())){
                    errorMessage = "Item not updated";
                }
            }
        }
        else if(type == 1) {
            if(about_itemService.getById(about_item.getId()).isPresent()){
                errorMessage = "An item with that id already exist in the database";
            }
        }
        if(about_item.getItem_type() < 1 || about_item.getItem_type() > 3){
            errorMessage = "The item type must only be [1 (knowledge), 2 (badge), 3 (certificate)]";
        } else if(about_item.getName() == null){
            errorMessage = "The item name can't be null";
        } else if(about_item.getName().length() < 3){
            errorMessage = "The item name must be greater than 3 characters";
        } else if(about_item.getName().length() > 255){
            errorMessage = "The item name can't be greater than 255 characters";
        } else if(!regexSimpleString.matcher(about_item.getName()).matches()){
            errorMessage = "The item name must only contain letters, numbers, spaces and can't have starting or trailing spaces";
        } else if(about_item.getDescription() == null){
            errorMessage = "The item description can't be null";
        } else if(about_item.getDescription().length() < 3){
            errorMessage = "The item description must be greater than 3 characters";
        } else if(about_item.getDescription().length() > 3000){
            errorMessage = "The item description can't be greater than 3000 characters";
        } else if(about_item.getLink() != null) {
            if(about_item.getLink().contains(" ")){
                errorMessage = "The item link can't have spaces";
            } else if(about_item.getLink().length() < 3){
                errorMessage = "The item link must be greater than 3 characters";
            } else if(about_item.getLink().length() > 255){
                errorMessage = "The item link can't be greater than 255 characters";
            } else if(!about_item.getLink().toLowerCase().startsWith("http://") && !about_item.getLink().toLowerCase().startsWith("https://")){
                errorMessage = "The item link must start with either 'http://' or 'https://'";
            }
        } else if(about_item.getImage_uri() != null){
            if(about_item.getImage_uri().contains(" ")){
                errorMessage = "The item image link can't have spaces";
            } else if(about_item.getImage_uri().length() < 3){
                errorMessage = "The item image link must be greater than 3 characters";
            } else if(about_item.getImage_uri().length() > 255){
                errorMessage = "The item image link can't be greater than 255 characters";
            } else if(!about_item.getImage_uri().toLowerCase().startsWith("http://") && !about_item.getLink().toLowerCase().startsWith("https://")){
                errorMessage = "The item image link must start with either 'http://' or 'https://'";
            }
        } else if(about_item.getImage_alt() != null) {
            if(about_item.getImage_alt().length() < 3){
                errorMessage = "The item image alt must be greater than 3 characters";
            } else if(about_item.getImage_alt().length() > 255){
                errorMessage = "The item image alt can't be greater than 255 characters";
            } else if(!regexSimpleString.matcher(about_item.getImage_alt()).matches()){
                errorMessage = "The item image alt must only contain letters, numbers, spaces and can't have starting or trailing spaces";
            }
        } else if(!about_item.getDate().format(dateFormat).equals(about_item.getDate().toString())){
            errorMessage = "The item date must be in the format of 'yyyy-MM-dd'";
        } else if(about_item.getDate().getYear() < 1945 || about_item.getDate().isAfter(LocalDate.now()) ||
                about_item.getDate().getYear() == 1945 && about_item.getDate().getMonthValue() < 8 ||
                about_item.getDate().getYear() == 1945 && about_item.getDate().getMonthValue() == 8 && about_item.getDate().getDayOfMonth() < 6){
            errorMessage = "The item date must be before the current date and after 1945-08-06";
        }

        return errorMessage;
    }
}
