package com.courseshop.courseshop.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.courseshop.courseshop.model.Todo;

@Repository 
public interface TodoRepository extends JpaRepository<Todo, Long>{
    Optional <Todo> findByname(String name);
}
