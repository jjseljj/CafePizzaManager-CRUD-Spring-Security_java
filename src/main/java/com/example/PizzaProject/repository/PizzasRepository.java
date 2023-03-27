package com.example.PizzaProject.repository;

import com.example.PizzaProject.entity.Pizzas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PizzasRepository extends JpaRepository<Pizzas, Long> {
    Pizzas findByName(String name);
    Pizzas findByPrice(Double price);
    List<Pizzas> findByNameContaining(String name);

    List<Pizzas> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Pizzas p JOIN p.cafe c WHERE c.id = :cafeId")
    List<Pizzas> findByCafeId(@Param("cafeId") Long cafeId);
}


