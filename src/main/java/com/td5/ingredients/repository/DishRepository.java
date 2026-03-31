package com.td5.ingredients.repository;

import com.td5.ingredients.entity.Dish;
import com.td5.ingredients.entity.Ingredient;
import com.td5.ingredients.entity.CategoryEnum;
import com.td5.ingredients.entity.DishTypeEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // d) GET /dishes
    public List<Dish> findAll() {
        String sql = """
            SELECT d.id, d.name, d.selling_price, d.dish_type
            FROM dish d
            """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    // b) GET /dishes/{id}
    public Dish findById(Integer id) {
        String sql = """
            SELECT id, name, selling_price, dish_type 
            FROM dish 
            WHERE id = ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Dish dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("selling_price"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    return dish;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // e) PUT /dishes/{id}/ingredients
    public void updateDishIngredients(Integer dishId, List<Ingredient> ingredients) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM dishingredient WHERE id_dish = ?")) {
                pstmt.setInt(1, dishId);
                pstmt.executeUpdate();
            }
            String sql = """
                INSERT INTO dishingredient (id_dish, id_ingredient, required_quantity, unit)
                VALUES (?, ?, ?, ?)
                """;

            for (Ingredient ing : ingredients) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, dishId);
                    pstmt.setInt(2, ing.getId());
                    pstmt.setDouble(3, 1.0);
                    pstmt.setString(4, "PCS");
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NOUVEAU: GET /dishes/{id}/ingredients
    public List<Ingredient> findIngredientsByDishIdWithFilters(Integer dishId, String ingredientName, Double ingredientPriceAround) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT i.id, i.name, i.category, i.price
            FROM ingredient_new i
            JOIN dishingredient di ON i.id = di.id_ingredient
            WHERE di.id_dish = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(dishId);

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (ingredientPriceAround != null) {
            double minPrice = ingredientPriceAround - 50;
            double maxPrice = ingredientPriceAround + 50;
            sql.append(" AND i.price BETWEEN ? AND ?");
            params.add(minPrice);
            params.add(maxPrice);
        }

        sql.append(" ORDER BY i.name");

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ing.setPrice(rs.getDouble("price"));
                    ingredients.add(ing);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}