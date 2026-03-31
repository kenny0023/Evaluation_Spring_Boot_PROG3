package com.td5.ingredients.controller;

import com.td5.ingredients.entity.Ingredient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.td5.ingredients.repository.IngredientRepository;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        try {
            Ingredient ingredient = ingredientRepository.findById(id);
            return ResponseEntity.ok(ingredient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ingredient.id=" + id + " is not found");
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStockAt(
            @PathVariable Integer id,
            @RequestParam Instant at,
            @RequestParam String unit) {

        return ResponseEntity.ok("Stock non implémenté pour le moment");
    }
}