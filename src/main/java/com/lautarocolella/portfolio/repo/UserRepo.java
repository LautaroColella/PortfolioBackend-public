package com.lautarocolella.portfolio.repo;

import com.lautarocolella.portfolio.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {

    Optional<User> findOneByEmail(String email);
}
