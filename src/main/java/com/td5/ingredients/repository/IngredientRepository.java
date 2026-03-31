package com.td5.ingredients.repository;

import com.td5.ingredients.entity.Ingredient;
import com.td5.ingredients.entity.CategoryEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // a) GET /ingredients
    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, category, price FROM ingredient_new";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ing.setPrice(rs.getDouble("price"));
                ingredients.add(ing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    // b) GET /ingredients/{id}
    public Ingredient findById(Integer id) {
        String sql = "SELECT id, name, category, price FROM ingredient_new WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ing.setPrice(rs.getDouble("price"));
                    return ing;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}