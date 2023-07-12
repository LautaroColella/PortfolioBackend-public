package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.Contact_message;
import com.lautarocolella.portfolio.repo.Contact_messageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Contact_messageService {
    @Autowired
    Contact_messageRepo contact_messageRepo;

    public List<Contact_message> getAll(){
        try {
            List<Contact_message> contact_message = new ArrayList<>();
            contact_messageRepo.findAll().forEach(contact_message::add);
            return contact_message;
        } catch (Exception e){
            return null;
        }
    }

    public Optional<Contact_message> getById(long id){
        try {
            return contact_messageRepo.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Contact_message saveOrUpdate(Contact_message contact_message){
        try {
            contact_messageRepo.save(contact_message);
            return contact_message;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id){
        try {
            contact_messageRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting message";
        }
    }
}
