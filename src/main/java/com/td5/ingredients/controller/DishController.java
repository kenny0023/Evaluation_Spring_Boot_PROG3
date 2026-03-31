package com.td5.ingredients.controller;

import com.td5.ingredients.entity.Ingredient;
import com.td5.ingredients.repository.DishRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishRepository;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @GetMapping
    public ResponseEntity<List<com.td5.ingredients.entity.Dish>> getAllDishes() {
        List<com.td5.ingredients.entity.Dish> dishes = dishRepository.findAll();
        return ResponseEntity.ok(dishes);
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody List<com.td5.ingredients.entity.Ingredient> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Le corps de la requête est obligatoire");
        }

        try {
            dishRepository.updateDishIngredients(id, ingredients);
            return ResponseEntity.ok("Ingrédients mis à jour pour le plat " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Dish.id=" + id + " is not found");
        }
    }

    @GetMapping(value = "/{id}/ingredients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDishIngredientsWithFilters(
            @PathVariable Integer id,
            @RequestParam(value = "ingredientName", required = false) String ingredientName,
            @RequestParam(value = "ingredientPriceAround", required = false) Double ingredientPriceAround) {

        if (!dishRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Dish.id=" + id + " is not found");
        }

        List<Ingredient> ingredients = dishRepository.findIngredientsByDishIdWithFilters(
                id, ingredientName, ingredientPriceAround);

        return ResponseEntity.ok(ingredients);
    }
}