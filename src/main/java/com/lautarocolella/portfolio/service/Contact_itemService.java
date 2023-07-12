package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.Contact_item;
import com.lautarocolella.portfolio.repo.Contact_itemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Contact_itemService {
    @Autowired
    Contact_itemRepo contact_itemRepo;

    public List<Contact_item> getAll(){
        try {
            List<Contact_item> contact_item = new ArrayList<>();
            contact_itemRepo.findAll().forEach(contact_item::add);
            return contact_item;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<Contact_item> getById(long id){
        try {
            return contact_itemRepo.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Contact_item saveOrUpdate(Contact_item contact_item){
        try {
            contact_itemRepo.save(contact_item);
            return contact_item;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id) {
        try {
            contact_itemRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting contact item";
        }
    }
}
