package com.lautarocolella.portfolio.service;

import com.lautarocolella.portfolio.model.Information;
import com.lautarocolella.portfolio.repo.InformationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InformationService {
    @Autowired
    InformationRepo informationRepo;

    public List<Information> getAll(){
        try {
            List<Information> information = new ArrayList<>();
            informationRepo.findAll().forEach(information::add);
            return information;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<Information> getById(long id){
        try {
            return informationRepo.findById(id);
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public Information saveOrUpdate(Information information) {
        try {
            informationRepo.save(information);
            return information;
        } catch (Exception e) {
            return null;
        }
    }

    public String delete(long id){
        try {
            informationRepo.deleteById(id);
            return "";
        } catch (Exception e) {
            return "Internal server error while deleting information";
        }
    }
}
