PizzaProject Functionality
This project implements functionality for managing information about cafes and pizzerias. The application has two main entities: "Pizza" and "Cafe". Each pizza is described by a name, description, price, and a list of ingredients. Each cafe is described by a name, address, and a list of available pizzas.
A database is used to store the data, and a repository layer is used to work with the database. The repository classes (PizzaRepository and CafeRepository) provide methods for saving, deleting, and retrieving pizza and cafe objects, respectively.
A service layer is used to implement the business logic. The service classes (PizzaService and CafeService) contain methods for creating, updating, and deleting pizza and cafe objects, as well as methods for retrieving information about objects.
Spring Security is used to ensure the security of the web application. The security configuration is located in the SecurityConfiguration class. This class defines access rules for different resources, as well as the configuration for user authentication and authorization.
In addition, the project includes PizzaController and CafeController classes, which provide APIs for retrieving information about pizzas and cafes, as well as for creating, updating, and deleting pizza and cafe objects.
The system provides CRUD functionality for both entities and ensures security through authentication of an administrator with the username "admin".
For the "Pizza" entity, the administrator can create new records by specifying information such as the pizza name, size, main ingredients, and the ID of the cafe where it can be ordered. The administrator can also view, update, and delete existing records.
For the "Cafe" entity, the administrator can create new records by specifying information such as the cafe name, city, address, email, phone, opening and closing times. The administrator can also view, update, and delete existing records.
Administrator authentication and access restrictions to CRUD functionality, as well as POST, DELETE, and PUT requests, are implemented using Spring Security. Users can retrieve all pizzas, a specific pizza, all cafes, a specific cafe, and so on.
The "Cafe" and "Pizza" entities have a bidirectional @OneToMany relationship, which means that each cafe can have multiple pizzas, but each pizza can only belong to one cafe.
The application provides CRUD functionality for cafes and pizzas. To ensure security for CRUD functionality and POST, DELETE, and PUT requests, an administrator username of "admin" is required. The application allows for retrieving all records about cafes and pizzas, as well as selectively retrieving a record.
Each pizza belongs to one pizzeria, and each pizzeria can have multiple types of pizza. The relationship between cafes and pizzas is implemented using the bidirectional @OneToMany annotation.
For cafes, the following fields are defined in the Cafes class:
•	id - unique identifier of the cafe
•	name - name of the cafe
•	city - city where the cafe is located
•	address - address of the cafe
•	email - email address of the cafe
•	phone - phone number of the cafe
•	openAt - opening time of the cafe
•	closeAt - closing time of the cafe
For pizzas, the following fields are defined in the Pizzas class:
•	id - unique identifier of the pizza
•	name - name of the pizza
•	description - description of the pizza
•	price - price of the pizza
•	isVegetarian - flag indicating whether the pizza is vegetarian
•	isGlutenFree - flag indicating whether the pizza is gluten-free
•	imageUrl - URL of the pizza image
The relationship between cafes and pizzas is implemented using the @OneToMany annotation in the Cafes class. Each pizza can belong to only one cafe. The relationship is implemented in the pizzas field of the Cafes class, which stores a set of pizzas belonging to the cafe. When a cafe is deleted, all associated pizza records are also deleted.
To ensure the security of the application, Spring Security is used, which verifies user credentials and restricts access to certain parts of the application depending on their role.

CafesController class - This code is a Java class that serves as a controller for a web application. Its purpose is to handle requests from the client and call the corresponding methods to process these requests. The controller includes several methods for handling different HTTP requests, such as GET, POST, PUT, and DELETE.
Each method has its own web request address, for example, the getAllCafes() method handles the GET request at the /cafes address. Methods can also accept query parameters passed as method arguments, for example, the getCafeByIdWithPizzas() method accepts the cafe identifier as a path parameter.
The controller uses the Spring Framework for inversion of control and dependency management. It also uses Spring Security to check user authorization and restrict access to controller methods depending on the user's role.
The controller is also linked to a repository that handles requests to the database. This code provides methods for working with the "Cafes" and "Pizzas" tables.
Finally, the controller also includes an exception handler that intercepts validation errors and returns error messages as a response to the request.
This code is written in Java and represents an implementation of a REST controller for managing cafes. It uses the Spring Framework and includes the following methods:
getAllCafes() - returns a list of all cafes
addCafe() - adds a new cafe to the system
getCafeByIdWithPizzas() - returns information about a cafe with a given identifier and a list of all pizzas in the cafe
updateCafeById() - updates information about a cafe with a given identifier
deleteCafeById() - deletes a cafe with a given identifier
getCafesByAddress() - searches for cafes by a given address
addCafes() - adds a new cafe to the system
handleValidationExceptions() - handles validation errors when adding a new cafe.

The PizzasController class is a Spring controller for managing pizzas. It contains several methods for interacting with the database and providing information about pizzas in JSON format.
The methods of the class are:
•	getPizzasByCafeId: a method for getting a list of all pizzas from a specific cafe. The method takes a cafe_id parameter and uses the findByCafeId method of the PizzasRepository interface to find all pizzas from the cafe with the specified cafe_id.
•	addPizzaToCafe: a method for adding a new pizza to a specific cafe. The method takes a Pizzas object containing information about the pizza to be added as a parameter and uses the save method of the PizzasRepository interface to save the new pizza to the database.
•	getPizzaById: a method for getting information about a specific pizza by its id. The method takes an id parameter for the pizza and uses the findById method of the PizzasRepository interface to find the pizza with the specified id.
•	updatePizzaById: a method for updating information about a specific pizza by its id. The method takes an id parameter for the pizza and a Pizzas object containing the new information about the pizza as parameters. It uses the findById method of the PizzasRepository interface to find the pizza with the specified id, and then updates its fields with the new values using the setName, setDescription, setPrice, setIsVegetarian, setIsGlutenFree, and setImageUrl methods. After this, it saves the modified pizza to the database using the save method.
•	deletePizza: a method for deleting a specific pizza by its id. The method takes an id parameter for the pizza and uses the deleteById method of the PizzasRepository interface to delete the pizza with the specified id.
•	getAllPizzas: a method for getting a list of all pizzas from the database. It uses the findAll method of the PizzasRepository interface to find all pizzas in the database.
•	searchPizzasByName: a method for performing a basic search by pizza name. The method takes a name parameter, which should contain a part of the pizza name. It uses the findByNameContainingIgnoreCase method of the PizzasRepository interface to find all pizzas.

@GetMapping("/pizzas"): the getPizzasByCafeId method returns a list of all pizzas available at a specific café. This method can be accessed at http://localhost:8080/pizzas?cafe_id=1 by specifying the café ID in the request. If the pizza list is empty, the method returns HttpStatus.NOT_FOUND, otherwise it returns HttpStatus.OK along with the pizza list.
@PostMapping("/pizzas"): the addPizzaToCafe method adds a new pizza to a specific café. The method accepts a Pizzas object in the request body and saves it in the repository. If the operation is successful, the method returns HttpStatus.CREATED along with the Pizzas object.
@GetMapping("/pizza/{id}"): the getPizzaById method returns the details of a specific pizza. This method can be accessed at http://localhost:8080/pizza/1 by specifying the pizza ID in the request. If the pizza is not found, the method returns HttpStatus.NOT_FOUND, otherwise it returns HttpStatus.OK along with the Pizzas object.
@PutMapping("/pizza/{id}"): the updatePizzaById method updates the details of a specific pizza. The method accepts the pizza ID and a Pizzas object in the request body. If the pizza is not found, the method returns HttpStatus.NOT_FOUND, otherwise it updates the pizza details and returns HttpStatus.OK along with the Pizzas object.
@DeleteMapping("/pizza/{id}"): the deletePizza method deletes a specific pizza. The method accepts the pizza ID and removes it from the repository. If the operation is successful, the method returns HttpStatus.NO_CONTENT, otherwise it returns HttpStatus.INTERNAL_SERVER_ERROR.
@GetMapping("/pizzas/all"): the getAllPizzas method returns a list of all pizzas from the database. If the pizza list is empty, the method returns HttpStatus.NOT_FOUND, otherwise it returns HttpStatus.OK along with the pizza list.
@GetMapping("/pizzas/search"): the searchPizzasByName method searches for pizzas by name. The method accepts a name string as a query parameter and returns a list of all pizzas whose name contains the specified string. If the pizza list is empty, the method returns HttpStatus.NOT_FOUND, otherwise it returns HttpStatus.OK along with the pizza list.
@PostMapping("/pizzas/addPizzas"): the addPizzas method adds a new pizza to the database. The method accepts a Pizzas object in the request body, which contains information about the new pizza. If the Pizzas object passes the validation check, the method saves the new pizza to the database and returns a message indicating a successful save. If the Pizzas object fails the validation check, the method returns an error message in the format of Map<String, String>.
The handleValidationExceptions method is an error handler that handles exceptions of type MethodArgumentNotValidException. This method intercepts the exception and returns an error message in the format of Map<String, String>, which contains information about which field of the Pizzas object failed the validation check and why.

The User class represents a user model and contains the following fields:
•	id: a unique user identifier of type Long.
•	username: a user name of type String.
•	password: a user password of type String.
•	role: a user role of type String.
The class also has a constructor that takes a user name, password, and role as arguments.
The SecurityConfiguration class represents a security configuration and contains the following methods:
•	getEncoder(): a method that returns a NoOpPasswordEncoder for password encryption.
•	getChain(): a method that returns a security filter chain for HttpSecurity. This method configures access rights, authentication, authorization, and other parameters.
The CafesRepository interface extends JpaRepository for the Cafes class and contains the following methods:
•	findByName(String name): returns a cafe with the specified name.
•	findByCity(String city): returns a list of cafes located in the specified city.
•	findByAddress(String address): returns a list of cafes located at the specified address.
•	findByPhone(String phone): returns a list of cafes with the specified phone number.
•	findByEmail(String email): returns a list of cafes with the specified email.
•	findByOpenAt(String openAt): returns a list of cafes open at the specified time.
•	findByCloseAt(String closeAt): returns a list of cafes closed at the specified time.
•	findByAddressContainingIgnoreCase(String address): returns a list of cafes whose address contains the specified string (case-insensitive).
•	findByIdWithPizzas(Long id): returns the cafe with the specified id and a list of pizzas that can be ordered at this cafe.
•	findAllWithPizzas(): returns a list of all cafes with pizzas.
The PizzasRepository interface also extends JpaRepository for the Pizzas class and contains the following methods:
•	findByName(String name): returns the pizza with the specified name.
•	findByPrice(Double price): returns the pizza with the specified price.
•	findByNameContaining(String name): returns a list of pizzas whose names contain the specified string.
•	findByNameContainingIgnoreCase(String name): returns a list of pizzas whose names contain the specified string (case-insensitive).
•	findByCafeId(@Param("cafeId") Long cafeId): returns a list of pizzas that can be ordered at the cafe with the specified id.

