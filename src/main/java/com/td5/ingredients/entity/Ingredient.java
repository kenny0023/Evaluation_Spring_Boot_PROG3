package com.td5.ingredients.entity;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.Instant.now;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList;

    public Ingredient() {
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, List<StockMovement> stockMovementList) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockMovementList = stockMovementList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && category == that.category && Objects.equals(price, that.price);
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public StockValue getStockValueAt(Instant t) {
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            StockValue sv = new StockValue();
            sv.setQuantity(0.0);
            sv.setUnit(null);
            return sv;
        }

        Map<Unit, List<StockMovement>> byUnit = stockMovementList.stream()
                .collect(Collectors.groupingBy(sm -> sm.getValue().getUnit()));

        if (byUnit.size() > 1) {
            throw new RuntimeException("Multiple units found - conversion not handled");
        }

        Unit unit = byUnit.keySet().stream().findFirst().orElse(null);

        List<StockMovement> relevantMovements = stockMovementList.stream()
                .filter(mv -> !mv.getCreationDatetime().isAfter(t))
                .toList();

        double totalIn = relevantMovements.stream()
                .filter(mv -> mv.getType() == MovementTypeEnum.IN)
                .mapToDouble(mv -> mv.getValue().getQuantity())
                .sum();

        double totalOut = relevantMovements.stream()
                .filter(mv -> mv.getType() == MovementTypeEnum.OUT)
                .mapToDouble(mv -> mv.getValue().getQuantity())
                .sum();

        double finalQuantity = totalIn - totalOut;

        StockValue result = new StockValue();
        result.setQuantity(finalQuantity);
        result.setUnit(unit);

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price);
    }

    @Override
    public String toString() {
        return "com.td5.ingredients.entity.Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", actualStock=" + getStockValueAt(now()) +
                '}';
    }
}
