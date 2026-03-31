package com.td5.ingredients.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;

    public Order() {
        this.dishOrders = new ArrayList<>();
        this.creationDateTime = Instant.now();
    }

    public Order(String reference) {
        this();
        this.reference = reference;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders != null ? dishOrders : new ArrayList<>();
    }

    private PaymentStatusEnum paymentStatus = PaymentStatusEnum.UNPAID;
    public PaymentStatusEnum getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void checkModifiable() throws IllegalStateException {
        if (paymentStatus == PaymentStatusEnum.PAID) {
            throw new IllegalStateException("Impossible de modifier une commande déjà payée (PAID)");
        }
    }
    public Double getTotalAmountWithoutVAT() {
        if (dishOrders == null || dishOrders.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (DishOrder line : dishOrders) {
            if (line.getDish() != null && line.getDish().getPrice() != null) {
                total += line.getDish().getPrice() * line.getQuantity();
            }
        }
        return total;
    }

    public Double getTotalAmountWithVAT() {
        Double ht = getTotalAmountWithoutVAT();
        if (ht == null) {
            return null;
        }
        final double TVA_RATE = 0.20;
        return ht * (1 + TVA_RATE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(reference, order.reference) &&
                Objects.equals(creationDateTime, order.creationDateTime) &&
                Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDateTime, dishOrders);
    }

    @Override
    public String toString() {
        return "com.td5.ingredients.entity.Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", totalHT=" + getTotalAmountWithoutVAT() +
                ", totalTTC=" + getTotalAmountWithVAT() +
                ", items=" + (dishOrders != null ? dishOrders.size() : 0) +
                '}';
    }
}