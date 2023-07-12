package com.lautarocolella.portfolio.repo;

import com.lautarocolella.portfolio.model.Information;
import org.springframework.data.repository.CrudRepository;

public interface InformationRepo extends CrudRepository<Information, Long> {
}
