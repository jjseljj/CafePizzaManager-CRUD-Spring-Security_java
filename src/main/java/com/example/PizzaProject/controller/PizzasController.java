package com.example.PizzaProject.controller;

import com.example.PizzaProject.entity.Pizzas;
import com.example.PizzaProject.repository.CafesRepository;
import com.example.PizzaProject.repository.PizzasRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PizzasController {

    private PizzasRepository repository;
    private CafesRepository cafesRepository;


    @Autowired
    public PizzasController(PizzasRepository repository, CafesRepository cafeRepository) {
        this.repository = repository;
        this.cafesRepository = cafesRepository;
    }

    //GET http://localhost:8080/pizzas?cafe_id=1
    //7 List all pizzas of specific cafe
    //Список всех пицц определенного кафе
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pizzas")
    public ResponseEntity<List<Pizzas>> getPizzasByCafeId(@RequestParam (name = "cafe_id") Long cafe_id) {
        List<Pizzas> pizzas = repository.findByCafeId(cafe_id);
        if (pizzas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }

    //POST http://localhost:8080/pizzas
    // 8 Add new pizza to specific cafe
    //Добавить новую пиццу в определенное кафе
    @PreAuthorize("hasRole('ADMIN')")  // !!! 500
    @PostMapping("/pizzas")
    public ResponseEntity<Pizzas> addPizzaToCafe(@RequestBody Pizzas pizza) {
        Pizzas newPizza = repository.save(pizza);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPizza);
    }

    // GET http://localhost:8080/pizza/1
    //9 Get specific pizza details
    //Получить детали определенной пиццы
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pizza/{id}")
    public ResponseEntity<Pizzas> getPizzaById(@PathVariable Long id) {
        Pizzas pizza = repository.findById(id).orElse(null);
        if (pizza == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pizza, HttpStatus.OK);
    }

    //PUT http://localhost:8080/pizza/1
    //10 Update pizza details (by pizza id)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/pizza/{id}")
    public ResponseEntity<Pizzas> updatePizzaById(@PathVariable(name = "id") Long id,
                                                  @RequestBody Pizzas pizzaRequest) {
        Pizzas pizza = repository.findById(id).orElse(null);
        if (pizza == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        pizza.setName(pizzaRequest.getName());
        pizza.setDescription(pizzaRequest.getDescription());
        pizza.setPrice(pizzaRequest.getPrice());
        pizza.setIsVegetarian(pizzaRequest.getIsVegetarian());
        pizza.setIsGlutenFree(pizzaRequest.getIsGlutenFree());
        pizza.setImageUrl(pizzaRequest.getImageUrl());
        pizza = repository.save(pizza);
        return new ResponseEntity<>(pizza, HttpStatus.OK);
    }

    //http://localhost:8080/pizza/1
    @PreAuthorize("hasRole('ADMIN')")
    //11 Delete specific pizza
    @DeleteMapping("/pizza/{id}")
    public ResponseEntity<?> deletePizza(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //GET http://localhost:8080/pizzas
    //12 List all pizzas from database
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pizzas/all")
    public ResponseEntity<List<Pizzas>> getAllPizzas() {
        List<Pizzas> pizzas = repository.findAll();
        if (pizzas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }

    //GET http://localhost:8080/pizzas?name=MARGARITA
    //13 Basic search by pizza name (should return all pizzas whose name contains search term)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pizzas/search")
    public ResponseEntity<List<Pizzas>> searchPizzasByName(@RequestParam String name) {
        List<Pizzas> pizzas = repository.findByNameContainingIgnoreCase(name);
        if (pizzas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }

    //GET http://localhost:8080/pizzas?price=100
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pizzas/addPizzas")
    ResponseEntity <String> addPizzas(
            @Valid @RequestBody Pizzas pizza
    ) {
        repository.save(pizza);
        return ResponseEntity.ok(
                "Pizza is valid"
        );
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
                error ->
                        errors.put(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()
                        )
        );
        return errors;
    }
}
