package com.example.PizzaProject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Pizzas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PIZZA_NAME")
    private String name;

    @Column(name = "PIZZA_DESCRIPTION")
    private String description;

    @Column(name = "PIZZA_PRICE")
    private Double price;

    @Column(name = "PIZZA_ISVEGETARIAN")
    private Boolean isVegetarian;

    @Column(name = "PIZZA_ISGLUTENFREE")
    private Boolean isGlutenFree;

    @Column(name = "PIZZA_IMAGEURL")
    private String imageUrl;

      public Pizzas(String name, String description, Double price, Boolean isVegetarian, Boolean isGlutenFree, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVegetarian = isVegetarian;
        this.isGlutenFree = isGlutenFree;
        this.imageUrl = imageUrl;
    }

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false,
            cascade = CascadeType.MERGE
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cafe_id", nullable = false)
    @JsonBackReference
    private Cafes cafe;
}
