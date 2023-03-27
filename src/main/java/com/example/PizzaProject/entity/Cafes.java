package com.example.PizzaProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cafes")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cafes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(max = 50, message = "Name length should be less than or equal to 50")
    @Column(name = "CAFES_NAME", length = 50, nullable = false, unique = false)
    private String name;

    @NotNull(message = "City cannot be null")
    @Size(max = 50, message = "Name length should be less than or equal to 50")
    @Column(name = "CAFES_CITY", length = 50, nullable = false, unique = false)
    private String city;

    @NotNull(message = "Address cannot be null")
    @Size(max = 100, message = "Address length should be less than or equal to 100")
    @Column(name = "CAFES_ADDRESS", length = 100, nullable = false, unique = false)
    private String address;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    @Column(name = "CAFES_EMAIL", length = 30, nullable = false, unique = false)
    private String email;
    @NotNull(message = "Phone cannot be null")
    @Size(max = 50, message = "Phone length should be less than or equal to 50")
    @Column(name = "CAFES_PHONE", length = 50, nullable = false, unique = false)
    private String phone;

    @NotNull(message = "OpenAt cannot be null")
    @Column(name = "CAFES_OPEN_AT", length = 50, nullable = false, unique = false)
    private String openAt; //LocalTime

    @NotNull(message = "CloseAt cannot be null")
    @Column(name = "CAFES_CLOSE_AT", length = 50, nullable = false, unique = false)
    private String closeAt; //LocalTim

    public Cafes(String name, String city, String address, String email, String phone, String openAt, String closeAt) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)

    private Set<Pizzas> pizzas = new HashSet<>();
}
