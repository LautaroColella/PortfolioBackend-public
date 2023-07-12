package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.Work_item;
import com.lautarocolella.portfolio.service.Work_itemService;
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
@RequestMapping("/api/v1/work_item")
public class Work_itemController {
    @Autowired
    Work_itemService work_itemService;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<Work_item> work_items = work_itemService.getAll();
        if(work_items == null){
            errorResponse.put("message", "Internal server error while retrieving a list of work items from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(work_items.isEmpty()) {
            errorResponse.put("message", "There are no work items in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(work_items), HttpStatus.OK);
    }

    @GetMapping("/{work_itemId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("work_itemId")long work_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(work_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Work_item> optWork_item = work_itemService.getById(work_itemId);
        if(optWork_item.isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optWork_item, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody Work_item work_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateWorkItem(work_item, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Work_item newWork_item = work_itemService.saveOrUpdate(work_item);
        if(newWork_item == null){
            errorResponse.put("message", "Internal server error while creating work item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newWork_item, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody Work_item work_item){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateWorkItem(work_item, 0);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Work_item updatedWork_item = work_itemService.saveOrUpdate(work_item);
        if(updatedWork_item == null){
            errorResponse.put("message", "Internal server error while updating work item");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedWork_item, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{work_itemId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("work_itemId")long work_itemId){
        Map<String, String> errorResponse = new HashMap<>();
        if(work_itemId < 1){
            errorResponse.put("message", "The item id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(work_itemService.getById(work_itemId).isEmpty()){
            errorResponse.put("message", "An item with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = work_itemService.delete(work_itemId);
        if(!delResponse.isEmpty()){
            errorResponse.put("message", delResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid json input");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public String validateWorkItem(Work_item work_item, int type){
        String errorMessage = "";
        Pattern regexSimpleString = Pattern.compile("^[a-zA-Z0-9 ]+$");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(type == 0){
            if(work_itemService.getById(work_item.getId()).isEmpty()){
                errorMessage = "An item with that id doesn't exist in the database";
            }
        }
        if(type == 0){
            Optional<Work_item> optWork_item = work_itemService.getById(work_item.getId());
            if(optWork_item.isEmpty()){
                errorMessage = "An item with that id doesn't exist in the database";
            } else {
                if(work_item.getName().equals(optWork_item.get().getName()) &&
                        work_item.getTechnologies().equals(optWork_item.get().getTechnologies()) &&
                        work_item.getDescription().equals(optWork_item.get().getDescription()) &&
                        work_item.getCode_uri().equals(optWork_item.get().getCode_uri()) &&
                        work_item.getLive_uri().equals(optWork_item.get().getLive_uri()) &&
                        work_item.getImage_uri().equals(optWork_item.get().getImage_uri()) &&
                        work_item.getImage_alt().equals(optWork_item.get().getImage_alt()) &&
                        work_item.getDate().isEqual(optWork_item.get().getDate())){
                    errorMessage = "Item not updated";
                }
            }
        }
        else if(type == 1) {
            if(work_itemService.getById(work_item.getId()).isPresent()){
                errorMessage = "An item with that id already exist in the database";
            }
        }
        if(work_item.getName() == null){
            errorMessage = "The item name can't be null";
        } else if(work_item.getName().length() < 3){
            errorMessage = "The item name must be greater than 3 characters";
        } else if(work_item.getName().length() > 255){
            errorMessage = "The item name can't be greater than 255 characters";
        } else if(!regexSimpleString.matcher(work_item.getName()).matches()){
            errorMessage = "The item name must only contain letters, numbers, spaces and can't have starting or trailing spaces";
        } else if(work_item.getTechnologies() == null){
            errorMessage = "The item technologies can't be null";
        } else if(work_item.getTechnologies().length() < 3){
            errorMessage = "The item technologies must be greater than 3 characters";
        } else if(work_item.getTechnologies().length() > 255) {
            errorMessage = "The item technologies can't be greater than 255 characters";
        } else if(work_item.getDescription() == null){
            errorMessage = "The item description can't be null";
        } else if(work_item.getDescription().length() < 3){
            errorMessage = "The item description must be greater than 3 characters";
        } else if(work_item.getDescription().length() > 3000){
            errorMessage = "The item description can't be greater than 3000 characters";
        } else if(work_item.getCode_uri() != null) {
            if(work_item.getCode_uri().contains(" ")){
                errorMessage = "The item code link can't have spaces";
            } else if(work_item.getCode_uri().length() < 3){
                errorMessage = "The item code link must be greater than 3 characters";
            } else if(work_item.getCode_uri().length() > 255){
                errorMessage = "The item code link can't be greater than 255 characters";
            } else if(!work_item.getCode_uri().toLowerCase().startsWith("http://") && !work_item.getCode_uri().toLowerCase().startsWith("https://")){
                errorMessage = "The item code link must start with either 'http://' or 'https://'";
            }
        } else if(work_item.getLive_uri() != null) {
            if(work_item.getLive_uri().contains(" ")){
                errorMessage = "The item live link can't have spaces";
            } else if(work_item.getLive_uri().length() < 3){
                errorMessage = "The item live link must be greater than 3 characters";
            } else if(work_item.getLive_uri().length() > 255){
                errorMessage = "The item live link can't be greater than 255 characters";
            } else if(!work_item.getLive_uri().toLowerCase().startsWith("http://") && !work_item.getLive_uri().toLowerCase().startsWith("https://")){
                errorMessage = "The item live link must start with either 'http://' or 'https://'";
            }
        } else if(work_item.getImage_uri() != null){
            if(work_item.getImage_uri().contains(" ")){
                errorMessage = "The item image link can't have spaces";
            } else if(work_item.getImage_uri().length() < 3){
                errorMessage = "The item image link must be greater than 3 characters";
            } else if(work_item.getImage_uri().length() > 255){
                errorMessage = "The item image link can't be greater than 255 characters";
            } else if(!work_item.getImage_uri().toLowerCase().startsWith("http://") && !work_item.getImage_uri().toLowerCase().startsWith("https://")){
                errorMessage = "The item image link must start with either 'http://' or 'https://'";
            }
        } else if(work_item.getImage_alt() != null) {
            if(work_item.getImage_alt().length() < 3){
                errorMessage = "The item image alt must be greater than 3 characters";
            } else if(work_item.getImage_alt().length() > 255){
                errorMessage = "The item image alt can't be greater than 255 characters";
            } else if(!regexSimpleString.matcher(work_item.getImage_alt()).matches()){
                errorMessage = "The item image alt must only contain letters, numbers, spaces and can't have starting or trailing spaces";
            }
        } else if(!work_item.getDate().format(dateFormat).equals(work_item.getDate().toString())){
            errorMessage = "The item date must be in the format of 'yyyy-MM-dd'";
        } else if(work_item.getDate().getYear() < 1945 || work_item.getDate().isAfter(LocalDate.now()) ||
                work_item.getDate().getYear() == 1945 && work_item.getDate().getMonthValue() < 8 ||
                work_item.getDate().getYear() == 1945 && work_item.getDate().getMonthValue() == 8 && work_item.getDate().getDayOfMonth() < 6){
            errorMessage = "The item date must be before the current date and after 1945-08-06";
        }

        return errorMessage;
    }
}
