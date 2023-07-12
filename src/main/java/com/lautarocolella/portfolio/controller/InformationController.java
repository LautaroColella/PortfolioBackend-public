package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.Information;
import com.lautarocolella.portfolio.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/information")
public class InformationController {
    @Autowired
    InformationService informationService;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<Information> information = informationService.getAll();
        if(information == null){
            errorResponse.put("message", "Internal server error while retrieving a list of the information from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(information.isEmpty()){
            errorResponse.put("message", "There is no information in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(information), HttpStatus.OK);
    }

    @GetMapping("/{informationId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("informationId")long informationId){
        Map<String, String> errorResponse = new HashMap<>();
        if(informationId < 1){
            errorResponse.put("message", "The information id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Information> optInformation = informationService.getById(informationId);
        if(optInformation.isEmpty()){
            errorResponse.put("message", "Information with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optInformation, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody Information information){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateInformation(information, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Information newInformation = informationService.saveOrUpdate(information);
        if(newInformation == null){
            errorResponse.put("message", "Internal server error while adding information");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newInformation, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody Information information){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateInformation(information, 0);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Information updatedInformation = informationService.saveOrUpdate(information);
        if(updatedInformation == null){
            errorResponse.put("message", "Internal server error while updating the information");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedInformation, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{informationId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("informationId")long informationId){
        Map<String, String> errorResponse = new HashMap<>();
        if(informationId < 1){
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(informationService.getById(informationId).isEmpty()){
            errorResponse.put("message", "Information with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = informationService.delete(informationId);
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

    public String validateInformation(Information information, int type){
        String errorMessage = "";
        Pattern regexSimpleString = Pattern.compile("^[a-zA-Z0-9 ]+$");

        if(type == 0){
            Optional<Information> optInformation = informationService.getById(information.getId());
            if(optInformation.isEmpty()){
                errorMessage = "Information with that id doesn't exist in the database";
            } else {
                if(information.getName().equals(optInformation.get().getName()) &&
                information.getInformation().equals(optInformation.get().getInformation())){
                    errorMessage = "Information not updated";
                }
            }
        }
        else if(type == 1) {
            if(informationService.getById(information.getId()).isPresent()){
                errorMessage = "Information with that id already exist in the database";
            }
        }
        if(information.getName() == null){
            errorMessage = "The information name can't be null";
        } else if(information.getName().length() < 3){
            errorMessage = "The information name must be greater than 3 characters";
        } else if(information.getName().length() > 255){
            errorMessage = "The information name can't be greater than 255 characters";
        } else if(information.getInformation() == null){
            errorMessage = "The information can't be null";
        } else if(information.getInformation().length() < 3){
            errorMessage = "The information must be greater than 3 characters";
        } else if(information.getInformation().length() > 2000){
            errorMessage = "The information can't be greater than 2000 characters";
        }

        return errorMessage;
    }
}
