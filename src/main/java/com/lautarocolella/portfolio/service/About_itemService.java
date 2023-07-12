package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.About_item;
import com.lautarocolella.portfolio.repo.About_itemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class About_itemService {
    @Autowired
    About_itemRepo about_itemRepo;

    public List<About_item> getAll(){
        try {
            List<About_item> about_items = new ArrayList<>();
            about_itemRepo.findAll().forEach(about_items::add);
            return about_items;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<About_item> getById(long id){
        try {
            return about_itemRepo.findById(id);
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public About_item saveOrUpdate(About_item about_item){
        try {
            about_itemRepo.save(about_item);
            return about_item;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id){
        try {
            about_itemRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting about item";
        }
    }
}
