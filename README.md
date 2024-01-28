# Проект Pizza

## Функциональность

Этот проект реализует функциональность для управления информацией о кафе и пиццериях. Приложение имеет две основные сущности: "Пицца" и "Кафе". Каждая пицца описывается названием, описанием, ценой и списком ингредиентов. Каждое кафе описывается названием, адресом и списком доступных пицц. Для хранения данных используется база данных, а для работы с ней — слой репозитория.

## Классы и методы

### Сервис и Репозиторий

Существует слой сервиса для реализации бизнес-логики. Репозитории (PizzaRepository и CafeRepository) предоставляют методы для сохранения, удаления и получения объектов пицц и кафе соответственно.

### Контроллеры

В проекте есть PizzaController и CafeController, предоставляющие API для получения информации о пиццах и кафе, а также для создания, обновления и удаления объектов пиццы и кафе.

### Spring Security

Spring Security используется для обеспечения безопасности веб-приложения. Конфигурация безопасности находится в классе SecurityConfiguration, который определяет правила доступа к разным ресурсам, а также конфигурацию аутентификации и авторизации пользователей.

## CRUD Функциональность

Система предоставляет CRUD функциональность для обеих сущностей и обеспечивает безопасность через аутентификацию администратора с именем пользователя "admin".

## Описание сущностей

- **Cafe (Кафе):**
  - id - уникальный идентификатор кафе
  - name - название кафе
  - city - город, в котором расположено кафе
  - address - адрес кафе
  - email - email-адрес кафе
  - phone - телефонный номер кафе
  - openAt - время открытия кафе
  - closeAt - время закрытия кафе

- **Pizza (Пицца):**
  - id - уникальный идентификатор пиццы
  - name - название пиццы
  - description - описание пиццы
  - price - цена пиццы
  - isVegetarian - флаг, указывающий, является ли пицца вегетарианской
  - isGlutenFree - флаг, указывающий, является ли пицца безглютеновой
  - imageUrl - URL изображения пиццы

## Взаимосвязи

- Каждая пицца принадлежит к одному кафе.
- Каждое кафе может иметь несколько видов пицц.

## Особенности контроллеров

- **CafesController:**
  - getAllCafes() - возвращает список всех кафе
  - addCafe() - добавляет новое кафе
  - getCafeByIdWithPizzas() - возвращает информацию о кафе по его идентификатору и список всех пицц в кафе
  - updateCafeById() - обновляет информацию о кафе по его идентификатору
  - deleteCafeById() - удаляет кафе по его идентификатору
  - getCafesByAddress() - ищет кафе по адресу
  - addCafes() - добавляет новое кафе
  - handleValidationExceptions() - обрабатывает ошибки валидации при добавлении нового кафе

- **PizzasController:**
  - getPizzasByCafeId() - возвращает список всех пицц в конкретном кафе
  - addPizzaToCafe() - добавляет новую пиццу в конкретное кафе
  - getPizzaById() - возвращает информацию о конкретной пицце по ее идентификатору
  - updatePizzaById() - обновляет информацию о конкретной пицце по ее идентификатору
  - deletePizza() - удаляет конкретную пиццу по ее идентификатору
  - getAllPizzas() - возвращает список всех пицц
  - searchPizzasByName() - выполняет поиск пицц по названию
  - addPizzas() - добавляет новую пиццу в базу данных

## Пользователи и Безопасность

- **User (Пользователь):**
  - id - уникальный идентификатор пользователя
  - username - имя пользователя
  - password - пароль пользователя
  - role - роль пользователя

- **SecurityConfiguration:**
  - getEncoder() - возвращает NoOpPasswordEncoder для шифрования пароля
  - getChain() - возвращает цепочку фильтров безопасности для HttpSecurity

## Репозитории

- **CafesRepository:**
  - Методы для поиска кафе по различным параметрам, а также для получения списка кафе с пиццами.

- **PizzasRepository:**
  - Методы для поиска пицц по различным параметрам и для получения списка пицц, которые можно заказать в


