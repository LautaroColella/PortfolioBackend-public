package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.User;
import com.lautarocolella.portfolio.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    public List<User> getAll(){
        try {
            List<User> users = new ArrayList<>();
            userRepo.findAll().forEach(users::add);
            return users;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<User> getById(long id){
        try {
            return userRepo.findById(id);
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public User saveOrUpdate(User user){
        try {
            userRepo.save(user);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id){
        try {
            userRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting user";
        }
    }
}
