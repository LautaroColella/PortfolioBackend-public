package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.Work_item;
import com.lautarocolella.portfolio.repo.Work_itemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Work_itemService {
    @Autowired
    Work_itemRepo work_itemRepo;

    public List<Work_item> getAll(){
        try {
            List<Work_item> work_item = new ArrayList<>();
            work_itemRepo.findAll().forEach(work_item::add);
            return work_item;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<Work_item> getById(long id){
        try {
            return work_itemRepo.findById(id);
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public Work_item saveOrUpdate(Work_item work_item) {
        try {
            work_itemRepo.save(work_item);
            return work_item;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id) {
        try {
            work_itemRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting about item";
        }
    }
}
