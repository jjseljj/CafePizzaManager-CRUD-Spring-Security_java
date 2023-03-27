package com.example.PizzaProject;

import com.example.PizzaProject.entity.Cafes;
import com.example.PizzaProject.entity.Pizzas;
import com.example.PizzaProject.repository.CafesRepository;
import com.example.PizzaProject.repository.PizzasRepository;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationPizzaTest {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PizzasRepository repository;
    @Autowired
    private CafesRepository cafesRepository;

    //7
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void testGetPizzasByCafeId() {

        Cafes cafe = cafesRepository.findById(1L).orElseThrow();
        Hibernate.initialize(cafe.getPizzas());

        // Удаляем все пиццы из кафе
        cafe.getPizzas().removeAll(cafe.getPizzas());

        // Создаем 2 пиццы для кафе
        Pizzas pizza1 = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza1.setCafe(cafe);
        repository.save(pizza1);

        Pizzas pizza2 = new Pizzas("Pepperoni", "Pizza with tomato sauce, mozzarella cheese, and pepperoni", 10.99, true, true, "pepperoni.jpg");
        pizza2.setCafe(cafe);
        repository.save(pizza2);

        // Создаем еще одну пиццу для другого кафе
        Cafes cafe2 = new Cafes("CafeTest", "CityTest","AddressTest", "Email@test" ,"+49 30 29009100", "12:00", "23:00");
        cafesRepository.save(cafe2);

        Pizzas pizza3 = new Pizzas("Hawaiian", "Pizza with tomato sauce, mozzarella cheese, ham, and pineapple", 11.99, true, true, "hawaiian.jpg");
        pizza3.setCafe(cafe2);
        repository.save(pizza3);

        // Получаем список пицц для кафе
        List<Pizzas> pizzas = repository.findByCafeId(cafe.getId());

        //String plainCredits = "admin:admin";
        //byte[] plainCreditsBytes = plainCredits.getBytes();
        //byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        //String base64Credits = new String(base64CreditsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        //headers.add("Authorization", "Basic " + base64Credits);

        // Отправляем GET запрос для получения списка пицц для определенного кафе
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<List<Pizzas>> response = restTemplate.exchange(
                "http://localhost:" + port + "/pizzas?cafe_id=" + cafe.getId(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<Pizzas>>() {});

        // Проверяем, что ответ имеет статус OK (код 200)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что возвращаются только пиццы, созданные в рамках теста
        assertEquals(2, pizzas.size());
        assertTrue(pizzas.contains(pizza1));
        assertTrue(pizzas.contains(pizza2));

        // Проверяем, что в списке есть только пиццы pizza и pizza2
        for (Pizzas pizza : pizzas) {
            assertTrue(pizza.getName().equals("Margarita") || pizza.getName().equals("Pepperoni"));
        }
    }

    //8
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void createPizza() throws Exception {
        assertEquals(repository.count(), 7L);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"Test Margarita\"," +
                        "\"description\":\"Test description\"," +
                        "\"price\":\"10\"," +
                        "\"isVegetarian\":\"true\"," +
                        "\"isGlutenFree\":\"true\"," +
                        "\"imageUrl\":\"https://www.pizzahut.com/assets/pizza/hero/hero_italian.png\"}",
                headers
        );
        String result =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/pizzas",
                        request,
                        String.class
                ).getBody();
        Assert.assertEquals(repository.count(), 7L);

        restTemplate.exchange(
                "http://localhost:" + port + "/pizzas",
                HttpMethod.POST, null, String.class);

        // проверяем, что кафе успешно удалено
        assertEquals(repository.count(), 7L);
    }
    //12
    @Test
    public void getAllPizzas() throws Exception {
        ResponseEntity<List<Pizzas>> response = restTemplate.exchange(
                "http://localhost:" + port + "/pizzas/all",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Pizzas>>() {
                });
        List<Pizzas> pizzasList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repository.count(), pizzasList.size());
    }

    //9
    @Test
    public void testGetPizzaById() {
        // Создаем кафе
        Cafes cafe = new Cafes("Test Cafe", "Test City", "Test Address", "Test@Email", "1234567890", "10:00", "22:00");
        Cafes savedCafe = cafesRepository.save(cafe);
        Long cafeId = savedCafe.getId();

        // Создаем пиццу и сохраняем ее в базе данных
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza.setCafe(savedCafe);
        Pizzas savedPizza = repository.save(pizza);
        Long pizzaId = savedPizza.getId();

        // Создаем заголовки с авторизационными данными
        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credits);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Отправляем GET запрос для получения пиццы
        String url = "http://localhost:" + port + "/pizza/{id}";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Pizzas> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Pizzas.class,
                pizzaId);

        // Проверяем, что ответ имеет статус OK (код 200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Проверяем, что полученные данные соответствуют сохраненной пицце
        Pizzas returnedPizza = responseEntity.getBody();
        assertEquals(savedPizza.getName(), returnedPizza.getName());
        assertEquals(savedPizza.getDescription(), returnedPizza.getDescription());
        assertEquals(savedPizza.getPrice(), returnedPizza.getPrice());
        assertEquals(savedPizza.getIsVegetarian(), returnedPizza.getIsVegetarian());
        assertEquals(savedPizza.getIsGlutenFree(), returnedPizza.getIsGlutenFree());
        assertEquals(savedPizza.getImageUrl(), returnedPizza.getImageUrl());
    }


    //10
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdatePizzaById2() throws Exception {
        // Create a cafe
        Cafes cafe = new Cafes();
        cafe.setName("Test Cafe");
        cafe.setCity("Test City");
        cafe.setAddress("Test Address");
        cafe.setEmail("test@example.com");
        cafe.setPhone("+1-555-555-5555");
        cafe.setOpenAt("9:00");
        cafe.setCloseAt("18:00");
        cafe = cafesRepository.save(cafe);

        // Create a pizza
        Pizzas pizza = new Pizzas();
        pizza.setName("Test Pizza");
        pizza.setDescription("Test Description");
        pizza.setPrice(10.00);
        pizza.setIsVegetarian(true);
        pizza.setIsGlutenFree(false);
        pizza.setImageUrl("http://example.com/test.jpg");
        pizza.setCafe(cafe);
        pizza = repository.save(pizza);

        // Update the pizza
        Pizzas updatedPizza = new Pizzas();
        updatedPizza.setName("Updated Test Pizza");
        updatedPizza.setDescription("Updated Test Description");
        updatedPizza.setPrice(15.00);
        updatedPizza.setIsVegetarian(false);
        updatedPizza.setIsGlutenFree(true);
        updatedPizza.setImageUrl("http://example.com/updated_test.jpg");
        updatedPizza.setCafe(cafe);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin");

        HttpEntity<Pizzas> requestEntity = new HttpEntity<>(updatedPizza, headers);

        ResponseEntity<Pizzas> responseEntity = restTemplate.exchange(
                "/pizza/{id}",
                HttpMethod.PUT,
                requestEntity,
                Pizzas.class,
                pizza.getId());

        // Assert that the response has HTTP status code 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the pizza was updated successfully
        Pizzas returnedPizza = responseEntity.getBody();
        assertNotNull(returnedPizza);
        assertEquals(updatedPizza.getName(), returnedPizza.getName());
        assertEquals(updatedPizza.getDescription(), returnedPizza.getDescription());
        assertEquals(updatedPizza.getPrice(), returnedPizza.getPrice(), 0.001);
        assertEquals(updatedPizza.getIsVegetarian(), returnedPizza.getIsVegetarian());
        assertEquals(updatedPizza.getIsGlutenFree(), returnedPizza.getIsGlutenFree());
        assertEquals(updatedPizza.getImageUrl(), returnedPizza.getImageUrl());
    }


    //11
    @Test
    public void testDeletePizza() {
        Cafes cafe = cafesRepository.findById(1L).get();
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza.setCafe(cafe);
        Pizzas savedPizza = repository.save(pizza);

        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credits);

        // Отправляем DELETE запрос для удаления сохраненной пиццы
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/pizza/" + savedPizza.getId(),
                HttpMethod.DELETE,
                request,
                Void.class
        );

        // Проверяем, что ответ имеет статус NO_CONTENT (код 204)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Проверяем, что пицца была удалена из базы данных
        assertFalse(repository.existsById(savedPizza.getId()));
    }
    //13
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void searchPizzasByNameTest() throws Exception {
        // Задаем URL для API-метода, который мы хотим вызвать
        String url = "http://localhost:" + port + "/pizzas/search?name=MARGARITA";

        // Вызываем API-метод с помощью метода exchange() и получаем ответ в объект ResponseEntity
        ResponseEntity<List<Pizzas>> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Pizzas>>(){});

        // Получаем тело ответа из объекта ResponseEntity
        List<Pizzas> pizzas = responseEntity.getBody();

        // Проверяем статус ответа
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Проверяем, что список пицц не пустой и содержит только пиццы с названием, содержащим "MARGARITA"
        assertNotNull(pizzas);
        assertTrue(pizzas.size() > 0);
        for (Pizzas pizza : pizzas) {
            assertTrue(pizza.getName().toLowerCase().contains("margarita"));
        }
    }
}
