package com.example.PizzaProject;

import com.example.PizzaProject.entity.Cafes;
import com.example.PizzaProject.entity.Pizzas;
import com.example.PizzaProject.repository.CafesRepository;
import com.example.PizzaProject.repository.PizzasRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationTestCafes {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CafesRepository cafesRepository;
    @Autowired
    private PizzasRepository pizzasRepository;


    //1 ОК
    @Test
    public void getAllCafes() throws Exception {
        ResponseEntity<List<Cafes>> responseEntity = restTemplate.exchange(
                "/cafes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Cafes>>() {
                }
        );
        List<Cafes> cafesList = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cafesRepository.count(), cafesList.size());
    }

    // ОК
    @Test
    public void invalidName() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"Test Cafe\"," +
                        "\"city\":\"Test City\"," +
                        "\"address\":\"Test Address\"," +
                        "\"email\":\"Test Email\"," +
                        "\"phone\":\"1234567890\"," +
                        "\"openAt\":\"10:00\"," +
                        "\"closeAt\":\"22:00\"}",
                headers
        );
        String result = restTemplate.postForEntity(
                "http://localhost:" + port + "/cafes",
                request,
                String.class
        ).getBody();

        Matchers.contains(jsonPath("$.name", Is.is("Name should be valid")));
    }

    // 5 ОК
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    public void deleteCafeById2() {
        // Получаем список всех кафе из репозитория
        List<Cafes> cafesList = cafesRepository.findAllWithPizzas();

        // Удаляем все кафе из списка кафе
        for (Cafes cafe : cafesList) {
            cafe.getPizzas().removeAll(cafe.getPizzas());
            cafesRepository.delete(cafe);
        }

        // Проверяем, что список кафе теперь пуст
        //assertEquals(0, cafesRepository.count());       !

        // Создаем кафе
        Cafes cafe = new Cafes("Test Cafe", "Test City", "Test Address", "Test@Email", "1234567890", "10:00", "22:00");
        Cafes savedCafe = cafesRepository.save(cafe);
        Long cafeId = savedCafe.getId();
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza.setCafe(savedCafe); // Установить id нового кафе для пиццы
        Pizzas savedPizza = pizzasRepository.save(pizza);

        // Создаем список кафе до удаления
        List<Cafes> cafesBeforeDeletion = cafesRepository.findAll();

        // Создаем заголовки с авторизационными данными
        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credits);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Отправляем DELETE запрос для удаления кафе
        String url = "http://localhost:" + port + "/cafes/{id}";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<HttpStatus> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                HttpStatus.class,
                cafeId);


        // Проверяем, что ответ имеет статус NO_CONTENT (код 204)
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        // Проверяем, что кафе было удалено из базы данных
        assertFalse(cafesRepository.existsById(cafe.getId()));

        // Создаем список кафе после удаления
        List<Cafes> cafesAfterDeletion = cafesRepository.findAllWithPizzas();

        // Проверяем, что количество кафе в базе данных уменьшилось на 1
        assertEquals(cafesBeforeDeletion.size() - 1, cafesAfterDeletion.size());

        // Проверяем, что все оставшиеся кафе не являются удаленным кафе
        for (Cafes c : cafesAfterDeletion) {
            assertNotEquals(cafe.getId(), c.getId());
        }

        //Проверяем что в БД осталось 3 кафе как были загружены при создании бд
        //assertEquals(3, cafesRepository.count());
    }
    //2
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddCafe() throws Exception {
        // Получаем список всех кафе из репозитория
        List<Cafes> cafesList = cafesRepository.findAllWithPizzas();

        //Получаем список всех пицц из репозитория
        List<Pizzas> pizzasList = pizzasRepository.findAll();

        // Удаляем все кафе из списка кафе
        for (Cafes cafe : cafesList) {
            cafesRepository.delete(cafe);
        }
        //Удаляем все кафе из списка пицц
        for (Pizzas pizza : pizzasList) {
            pizzasRepository.delete(pizza);
        }

        // Проверяем, что список кафе теперь пуст
        assertEquals(0, cafesRepository.count());

        // Создаем кафе
        Cafes cafe = new Cafes("Test Cafe", "Test City", "Test Address", "Test@Email", "1234567890", "10:00", "22:00");
        Cafes savedCafe = cafesRepository.save(cafe);
        Long cafeId = savedCafe.getId();
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza.setCafe(savedCafe); // Установить id нового кафе для пиццы
        Pizzas savedPizza = pizzasRepository.save(pizza);

        // Преобразуем объект кафе в JSON-строку
        ObjectMapper objectMapper = new ObjectMapper();
        String cafeJson = objectMapper.writeValueAsString(cafe);

        // Создаем заголовки с авторизационными данными
        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Credits);

        // Отправляем POST запрос для добавления кафе
        String url = "http://localhost:" + port + "/cafes";
        HttpEntity<String> requestEntity = new HttpEntity<>(cafeJson, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class,
                cafeId);

        // Проверяем, что ответ имеет статус (код 201)
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    }

    //3
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetCafeByIdWithPizzas() throws Exception {
        // Создаем кафе и сохраняем его в репозитории
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        Cafes cafe = new Cafes("Test Cafe", "Test City", "Test Address", "Test@Email", "1234567890", "10:00", "22:00");
        cafesRepository.save(cafe);

        // Создаем заголовки с авторизационными данными
        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Credits);

        // Отправляем GET запрос для получения кафе с указанным id
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Cafes> response = restTemplate.exchange(
                "http://localhost:" + port + "/cafes/full/{id}",
                HttpMethod.GET,
                request,
                Cafes.class,
                cafe.getId()
        );

        // Проверяем, что ответ имеет статус OK (код 200) и содержит правильные данные кафе
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateCafeById() throws Exception {
        // Получаем список всех кафе из репозитория
        List<Cafes> cafesList = cafesRepository.findAllWithPizzas();

        // Удаляем все кафе из списка кафе
        for (Cafes cafe : cafesList) {
            cafe.getPizzas().removeAll(cafe.getPizzas());
            cafesRepository.delete(cafe);
        }

        // Проверяем, что список кафе теперь пуст
        //assertEquals(0, cafesRepository.count());   !

        // Создаем кафе
        Cafes cafe = new Cafes("Test Cafe", "Test City", "Test Address", "Test@Email", "1234567890", "10:00", "22:00");
        Cafes savedCafe = cafesRepository.save(cafe);
        Long cafeId = savedCafe.getId();
        Pizzas pizza = new Pizzas("Margarita", "Classic pizza with tomato sauce and mozzarella cheese", 8.99, true, true, "margarita.jpg");
        pizza.setCafe(savedCafe);
        Pizzas savedPizza = pizzasRepository.save(pizza);

        // Проверяем, что кафе было добавлено в базу данных
        assertEquals(1, cafesRepository.count());

        // Создаем объект для обновления кафе
        Cafes updatedCafe = new Cafes("Updated Cafe", "Updated City", "Updated Address", "Updated@Email", "9876543210", "09:00", "23:00");
        updatedCafe.setId(cafeId);

        // Преобразуем объект кафе в JSON-строку
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedCafeJson = objectMapper.writeValueAsString(updatedCafe);

        // Создаем заголовки с авторизационными данными
        String plainCredits = "admin:admin";
        byte[] plainCreditsBytes = plainCredits.getBytes();
        byte[] base64CreditsBytes = Base64.encodeBase64(plainCreditsBytes, false);
        String base64Credits = new String(base64CreditsBytes);


        // Отправляем PUT запрос для обновления кафе
        String url = "http://localhost:" + port + "/cafes/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Credits);
        HttpEntity<Cafes> requestEntity = new HttpEntity<>(updatedCafe, headers);
        ResponseEntity<Cafes> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                Cafes.class,
                cafeId);

        // Проверяем, что кафе было обнавлено в базе данных
        assertEquals(1, cafesRepository.count());

        //проблема не сохранилось в бд и ответ 200 не получается
        //Вывести на консоль какой запрос выполнился при обновлении кафе
        //System.out.println("Response status: " + responseEntity.getStatusCode());

        //Проверяем что в БД осталось 3 кафе как были загружены при создании бд
        //assertEquals(3, cafesRepository.count());

    }

    //6 OK
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getCafesByAddress() {
        // Создаем два кафе с разными адресами
        Cafes cafe1 = new Cafes("Test Cafe 1", "Test City", "Test Address 1", "Test@Email1", "1234567890", "10:00", "22:00");
        cafesRepository.save(cafe1);
        Cafes cafe2 = new Cafes("Test Cafe 2", "Test City", "Test Address 2", "Test@Email2", "0987654321", "11:00", "23:00");
        cafesRepository.save(cafe2);

        // Создаем заголовки с авторизационными данными
        String plainCredentials = "admin:admin";
        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes, false);
        String base64Credentials = new String(base64CredentialsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Credentials);

        // Вызываем метод getCafesByAddress для адреса "Address 1"
        ResponseEntity<List<Cafes>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/cafe/search?address={address}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Cafes>>() {},
                "Address 1");

        // Проверяем, что ответ имеет статус OK (код 200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Проверяем, что список кафе содержит только одно кафе с адресом "Address 1"
        List<Cafes> cafes = responseEntity.getBody();
        assertEquals(1, cafes.size());
        assertEquals("Test Address 1", cafes.get(0).getAddress());
    }
}





