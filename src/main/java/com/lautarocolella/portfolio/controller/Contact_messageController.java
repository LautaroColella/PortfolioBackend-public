package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.Contact_message;
import com.lautarocolella.portfolio.service.Contact_messageService;
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
@RequestMapping("/api/v1/contact_message")
public class Contact_messageController {
    @Autowired
    Contact_messageService contact_messageService;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<Contact_message> contact_messages = contact_messageService.getAll();
        if(contact_messages == null){
            errorResponse.put("message", "Internal server error while retrieving a list of messages from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(contact_messages.isEmpty()){
            errorResponse.put("message", "There are no messages in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(contact_messages), HttpStatus.OK);
    }

    @GetMapping("/{contact_messageId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("contact_messageId")long contact_messageId){
        Map<String, String> errorResponse = new HashMap<>();
        if(contact_messageId < 1){
            errorResponse.put("message", "The message id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Contact_message> optContact_message = contact_messageService.getById(contact_messageId);
        if(optContact_message.isEmpty()){
            errorResponse.put("message", "A message with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optContact_message, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody Contact_message contact_message){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateMessage(contact_message, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        contact_message.setReaded(false);
        Contact_message newContact_message = contact_messageService.saveOrUpdate(contact_message);
        if(newContact_message == null){
            errorResponse.put("message", "Internal server error while creating message");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newContact_message, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody Contact_message contact_message){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateMessage(contact_message, 0);

        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        contact_message.setReaded(true);
        Contact_message updatedContact_message = contact_messageService.saveOrUpdate(contact_message);
        if(updatedContact_message == null){
            errorResponse.put("message", "Internal server error while updating message");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedContact_message, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{contact_messageId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("contact_messageId")long contact_messageId){
        Map<String, String> errorResponse = new HashMap<>();
        if(contact_messageId < 1){
            errorResponse.put("message", "The message id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(contact_messageService.getById(contact_messageId).isEmpty()){
            errorResponse.put("message", "A message with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = contact_messageService.delete(contact_messageId);
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

    public String validateMessage(Contact_message contact_message, int type){
        String errorMessage = "";
        Pattern regexSimpleString = Pattern.compile("^[a-zA-Z0-9 ]+$");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(type == 0){
            Optional<Contact_message> optContact_message = contact_messageService.getById(contact_message.getId());
            if(optContact_message.isEmpty()){
                errorMessage = "A message with that id doesn't exist in the database";
            } else {
                if(contact_message.getSubject().equals(optContact_message.get().getSubject()) &&
                        contact_message.getMessage().equals(optContact_message.get().getMessage()) &&
                        contact_message.getReply().equals(optContact_message.get().getReply()) &&
                        contact_message.getReaded() == optContact_message.get().getReaded() &&
                        contact_message.getDate().isEqual(optContact_message.get().getDate())){
                    errorMessage = "Message not updated";
                }
            }
        }
        else if(type == 1) {
            if(contact_messageService.getById(contact_message.getId()).isPresent()){
                errorMessage = "A message with that id already exist in the database";
            }
        }
        if(contact_message.getSubject() == null){
            errorMessage = "The message subject can't be null";
        } else if(contact_message.getSubject().length() < 3){
            errorMessage = "The message subject must be greater than 3 characters";
        } else if(contact_message.getSubject().length() > 255){
            errorMessage = "The message subject can't be greater than 255 characters";
        } else if(!regexSimpleString.matcher(contact_message.getSubject()).matches()){
            errorMessage = "The message subject must only contain letters, numbers, spaces and can't have starting or trailing spaces";
        } else if(contact_message.getMessage() == null){
            errorMessage = "The message can't be null";
        } else if(contact_message.getMessage().length() < 3){
            errorMessage = "The message must be greater than 3 characters";
        } else if(contact_message.getMessage().length() > 3000){
            errorMessage = "The message can't be greater than 3000 characters";
        } else if(contact_message.getReply() == null){
            errorMessage = "The message reply can't be null";
        } else if(contact_message.getReply().length() < 3){
            errorMessage = "The message reply must be greater than 3 characters";
        } else if(contact_message.getReply().length() > 255){
            errorMessage = "The message reply can't be greater than 255 characters";
        } else if(contact_message.getDate() == null){
            errorMessage = "The message date can't be null";
        } else if(!contact_message.getDate().format(dateFormat).equals(contact_message.getDate().toString())){
            errorMessage = "The message date must be in the format of 'yyyy-MM-dd'";
        } else if(contact_message.getDate().getYear() < 1945 || contact_message.getDate().isAfter(LocalDate.now()) ||
                contact_message.getDate().getYear() == 1945 && contact_message.getDate().getMonthValue() < 8 ||
                contact_message.getDate().getYear() == 1945 && contact_message.getDate().getMonthValue() == 8 && contact_message.getDate().getDayOfMonth() < 6){
            errorMessage = "The message date must be before the current date and after 1945-08-06";
        }

        return errorMessage;
    }
}
