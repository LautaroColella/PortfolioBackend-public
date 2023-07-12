package com.lautarocolella.portfolio.controller;

import com.lautarocolella.portfolio.model.User;
import com.lautarocolella.portfolio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Object> getAll(){
        Map<String, String> errorResponse = new HashMap<>();
        List<User> users = userService.getAll();
        if(users == null){
            errorResponse.put("message", "Internal server error while retrieving a list of users from the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(users.isEmpty()) {
            errorResponse.put("message", "There are no users in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(users), HttpStatus.OK);
    }

    @GetMapping("/{userId:[0-9]*}")
    public ResponseEntity<Object> getById(@PathVariable("userId")long userId){
        Map<String, String> errorResponse = new HashMap<>();
        if(userId < 1){
            errorResponse.put("message", "The user id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<User> optUser = userService.getById(userId);
        if(optUser.isEmpty()){
            errorResponse.put("message", "A user with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody User user){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateUser(user, 1);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String unhashedPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(unhashedPassword));
        User newUser = userService.saveOrUpdate(user);
        if(newUser == null){
            errorResponse.put("message", "Internal server error while creating user");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        newUser.setPassword(unhashedPassword);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody User user){
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = validateUser(user, 0);
        if(!errorMessage.isEmpty()){
            errorResponse.put("message", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String unhashedPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(unhashedPassword));
        User updatedUser = userService.saveOrUpdate(user);
        if(updatedUser == null){
            errorResponse.put("message", "Internal server error while updating user");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        updatedUser.setPassword(unhashedPassword);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId:[0-9]*}")
    public ResponseEntity<Object> delete(@PathVariable("userId")long userId){
        Map<String, String> errorResponse = new HashMap<>();

        Optional<User> optUser = userService.getById(userId);
        if(optUser.isPresent()){
            // hardcoded user so there's always at least one user in the db
            if(optUser.get().getEmail().equals("admin@admin.com")){
                errorResponse.put("message", "Admin user can't be deleted");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }
        if(userId < 1){
            errorResponse.put("message", "The user id must be greater than 1");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(userService.getById(userId).isEmpty()){
            errorResponse.put("message", "A user with that id was not found in the database");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String delResponse = userService.delete(userId);
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

    public String validateUser(User user, int type){
        String errorMessage = "";

        if(type == 0){
            Optional<User> optUser = userService.getById(user.getId());
            if(optUser.isEmpty()){
                errorMessage = "A user with that id doesn't exist in the database";
            } else {
                // hardcoded user so there's always at least one user in the db
                if(optUser.get().getEmail().equals("admin@admin.com")){
                    errorMessage = "Admin user can't be updated";
                }
                else if(user.getName().equals(optUser.get().getName()) &&
                        user.getEmail().equals(optUser.get().getEmail()) &&
                        passwordEncoder.matches(user.getPassword(), optUser.get().getPassword())){
                    errorMessage = "User not updated";
                }
            }
        }
        else if(type == 1) {
            if(userService.getById(user.getId()).isPresent()){
                errorMessage = "A user with that id already exist in the database";
            }
            List<User> userList = userService.getAll();
            for(User user1 : userList){
                if(user.getEmail().equals(user1.getEmail())){
                    errorMessage = "A user with that email already exist in the database";
                    break;
                }
            }
        }
        if(user.getName() == null){
            errorMessage = "The user name can't be null";
        } else if(user.getName().length() < 3){
            errorMessage = "The user name must be greater than 3 characters";
        } else if(user.getName().length() > 255){
            errorMessage = "The user name can't be greater than 255 characters";
        } else if(user.getEmail() == null){
            errorMessage = "The user email can't be null";
        } else if(user.getEmail().length() < 3){
            errorMessage = "The user email must be greater than 3 characters";
        } else if(user.getEmail().length() > 255){
            errorMessage = "The user email can't be greater than 255 characters";
        } else if(user.getPassword() == null){
            errorMessage = "The user password can't be null";
        } else if(user.getPassword().length() < 3){
            errorMessage = "The user password must be greater than 3 characters";
        } else if(user.getPassword().length() > 255){
            errorMessage = "The user password can't be greater than 255 characters";
        }

        return errorMessage;
    }
}
