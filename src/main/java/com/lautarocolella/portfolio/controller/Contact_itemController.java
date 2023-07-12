package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.Contact_item;
import com.lautarocolella.portfolio.service.Contact_itemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/contact_item")
public class Contact_itemController {
    @Autowired
    Contact_itemService contact_itemService;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<Contact_item> contact_items = contact_itemService.getAll();
        if(contact_items == null){
            errorResponse.put("message", "Internal server error while retrieving a list of contact items from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(contact_items.isEmpty()){
            errorResponse.put("message", "There are no contact items in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(contact_items), HttpStatus.OK);
    }

    @GetMapping("/{contact_itemId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("contact_itemId")long contact_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(contact_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Contact_item> optContact_item = contact_itemService.getById(contact_itemId);
        if(optContact_item.isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optContact_item, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody Contact_item contact_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateContactItem(contact_item, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Contact_item newContact_item = contact_itemService.saveOrUpdate(contact_item);
        if(newContact_item == null){
            errorResponse.put("message", "Internal server error while creating contact item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newContact_item, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody Contact_item contact_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateContactItem(contact_item, 0);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Contact_item updatedContact_item = contact_itemService.saveOrUpdate(contact_item);
        if(updatedContact_item == null){
            errorResponse.put("message", "Internal server error while updating contact item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedContact_item, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{contact_itemId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("contact_itemId")long contact_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(contact_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(contact_itemService.getById(contact_itemId).isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = contact_itemService.delete(contact_itemId);
        if(!delResponse.isEmpty()){
            errorResponse.put("message", delResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParseException(HttpMessageNotReadableException ex){
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid json input");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public String validateContactItem(Contact_item contact_item, int type){
        String errorMessage = "";
        Pattern regexSimpleString = Pattern.compile("^[a-zA-Z0-9 ]+$");

        if(type == 0){
            Optional<Contact_item> optContact_item = contact_itemService.getById(contact_item.getId());
            if(optContact_item.isEmpty()){
                errorMessage = "An item with that id doesn't exist in the database";
            } else {
                if(contact_item.getName().equals(optContact_item.get().getName()) &&
                        contact_item.getAccount().equals(optContact_item.get().getAccount()) &&
                        contact_item.getImage_alt().equals(optContact_item.get().getImage_alt()) &&
                        contact_item.getImage_uri().equals(optContact_item.get().getImage_uri()) &&
                        contact_item.getLink().equals(optContact_item.get().getLink())){
                    errorMessage = "Item not updated";
                }
            }
        }
        else if(type == 1) {
            if(contact_itemService.getById(contact_item.getId()).isPresent()){
                errorMessage = "An item with that id already exist in the database";
            }
        }
        if(contact_item.getName() == null){
            errorMessage = "The item name can't be null";
        } else if(contact_item.getName().length() < 3){
            errorMessage = "The item name must be greater than 3 characters";
        } else if(contact_item.getName().length() > 255){
            errorMessage = "The item name can't be greater than 255 characters";
        } else if(!regexSimpleString.matcher(contact_item.getName()).matches()){
            errorMessage = "The item name must only contain letters, numbers, spaces and can't have starting or trailing spaces";
        } else if(contact_item.getAccount() == null){
            errorMessage = "The item account can't be null";
        } else if(contact_item.getAccount().length() < 3){
            errorMessage = "The item account must be greater than 3 characters";
        } else if(contact_item.getAccount().length() > 255){
            errorMessage = "The item account can't be greater than 255 characters";
        } else if(contact_item.getLink() != null){
            if(contact_item.getLink().contains(" ")){
                errorMessage = "The item link can't have spaces";
            } else if(contact_item.getLink().length() < 3){
                errorMessage = "The item link must be greater than 3 characters";
            } else if(contact_item.getLink().length() > 255){
                errorMessage = "The item link can't be greater than 255 characters";
            } else if(!contact_item.getLink().toLowerCase().startsWith("http://") && !contact_item.getLink().toLowerCase().startsWith("https://")){
                errorMessage = "The item link must start with either 'http://' or 'https://'";
            }
        } else if(contact_item.getImage_uri() != null){
            if(contact_item.getLink().contains(" ")){
                errorMessage = "The item image link can't have spaces";
            } else if(contact_item.getImage_uri().length() < 3){
                errorMessage = "The item image link must be greater than 3 characters";
            } else if(contact_item.getImage_uri().length() > 255){
                errorMessage = "The item image link can't be greater than 255 characters";
            } else if(!contact_item.getImage_uri().toLowerCase().startsWith("http://") && !contact_item.getLink().toLowerCase().startsWith("https://")){
                errorMessage = "The item image link must start with either 'http://' or 'https://'";
            }
        } else if(contact_item.getImage_alt() != null){
            if(contact_item.getImage_alt().length() < 3){
                errorMessage = "The item image alt must be greater than 3 characters";
            } else if(contact_item.getImage_alt().length() > 255){
                errorMessage = "The item image alt can't be greater than 255 characters";
            } else if(!regexSimpleString.matcher(contact_item.getImage_alt()).matches()){
                errorMessage = "The item image alt must only contain letters, numbers, spaces and can't have starting or trailing spaces";
            }
        }

        return errorMessage;
    }
}
