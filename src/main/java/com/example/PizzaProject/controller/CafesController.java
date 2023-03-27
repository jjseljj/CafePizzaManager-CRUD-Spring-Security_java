package com.example.PizzaProject.controller;


import com.example.PizzaProject.entity.Cafes;
import com.example.PizzaProject.repository.CafesRepository;
import com.example.PizzaProject.repository.PizzasRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class CafesController {
    private CafesRepository repository;
    private PizzasRepository pizzasRepository;
    @Autowired
    public CafesController(CafesRepository repository,PizzasRepository pizzasRepository) {
        this.repository = repository;
        this.pizzasRepository = pizzasRepository;
    }

    //1. List all cafes
    //http://localhost:8080/cafes
    @GetMapping("/cafes")
    public ResponseEntity<Iterable<Cafes>> getAllCafes() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    //POST http://localhost:8080/cafe
    //2 Add a new café / Добавить новое кафе
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cafes")
    public ResponseEntity<Cafes> addCafe(@RequestBody Cafes cafes) {
        // Проверяем аутентификацию пользователя
        if (!isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Добавляем новое кафе в систему
        Cafes newCafe = repository.save(cafes);
        Cafes createdCafeDTO = new Cafes();

        // Возвращаем HTTP-статус код 201 Created и DTO объект с информацией о кафе
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCafeDTO);
    }
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    //GET http://localhost:8080/cafe/full/1
    //3 Get cafes by id with all pizza details listed
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cafes/full/{id}")
    public ResponseEntity<Cafes> getCafeByIdWithPizzas(@PathVariable(name = "id") Long id) {
        Cafes cafe = repository.findByIdWithPizzas(id);
        if (cafe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cafe, HttpStatus.OK);
    }

    //4 Update cafes details (identified by id)
    //http://localhost:8080/cafes/1
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/cafes/{id}")
    public ResponseEntity<Cafes> updateCafeById(
            @PathVariable(name = "id") Long id,
            @RequestBody Cafes cafeRequest
    ) {
        Cafes cafe = repository.findById(id).orElse(null);
        if (cafe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        cafe.setName(cafeRequest.getName());
        cafe.setAddress(cafeRequest.getAddress());
        cafe.setCity(cafeRequest.getCity());
        cafe.setEmail(cafeRequest.getEmail());
        cafe.setPhone(cafeRequest.getPhone());
        cafe.setOpenAt(cafeRequest.getOpenAt());
        cafe.setCloseAt(cafeRequest.getCloseAt());
        cafe = repository.save(cafe);
        return new ResponseEntity<>(cafe, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cafes/{id}")
    public ResponseEntity<Void> deleteCafeById(@PathVariable(name = "id") Long id) {
        Cafes cafe = repository.findById(id).orElse(null);
        if (cafe == null) {
            return ResponseEntity.notFound().build();
        }
        repository.delete(cafe);
        return ResponseEntity.noContent().build();
    }


    //GET http://localhost:8080/cafes?address=BERLIN
    //6 Основной поиск по адресу кафе (должен возвращать все кафе, чьи имена содержат заданный поисковый термин).
    // Basic search by cafe address (should return all cafes whose name contains search term)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cafe/search")
    public ResponseEntity<List<Cafes>> getCafesByAddress(@RequestParam(name = "address") String address) {
        Iterable<Cafes> cafesIterable = repository.findByAddressContainingIgnoreCase(address);
        List<Cafes> cafes = StreamSupport.stream(cafesIterable.spliterator(), false).collect(Collectors.toList());
        if (cafes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cafes, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cafes/addCafes")
    ResponseEntity <String> addCafes(
            @Valid @RequestBody Cafes cafe
    ) {
        repository.save(cafe);
        return ResponseEntity.ok(
                "Cafe is valid"
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





























