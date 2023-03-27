package com.example.PizzaProject.repository;

import com.example.PizzaProject.entity.Cafes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CafesRepository extends JpaRepository<Cafes, Long> {

    Cafes findByName(String name);
    Iterable<Cafes> findByCity(String city);
    Iterable<Cafes> findByAddress(String address);
    Iterable<Cafes> findByPhone(String phone);
    Iterable<Cafes> findByEmail(String email);
    Iterable<Cafes> findByOpenAt(String openAt);
    Iterable<Cafes> findByCloseAt(String closeAt);

    Iterable<Cafes> findByAddressContainingIgnoreCase(String address);

    @Query("SELECT c FROM Cafes c LEFT JOIN FETCH c.pizzas WHERE c.id = ?1")
    Cafes findByIdWithPizzas(Long id);

    @Query("SELECT DISTINCT c FROM Cafes c JOIN FETCH c.pizzas")
    List<Cafes> findAllWithPizzas();

}


