package com.td5.ingredients.entity;

import java.time.Instant;

public class Sale {
    private Integer id;
    private Instant creationDatetime;
    private Order order;

    public Sale() {
        this.creationDatetime = Instant.now();
    }

    public Sale(Integer id, Instant creationDatetime, Order order) {
        this.id = id;
        this.creationDatetime = creationDatetime;
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }


    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getTotalAmountWithoutVAT() {
        if (creationDatetime == null) {
            return 0.0;
        }
        Instant now = Instant.now();
        long secondsDifference = now.getEpochSecond() - creationDatetime.getEpochSecond();
        return secondsDifference * 100.0;
    }

    public double getTotalAmountWithVAT() {
        double withoutVAT = getTotalAmountWithoutVAT();
        return withoutVAT * 1.20;
    }

    @Override
    public String toString() {
        return "com.td5.ingredients.entity.Sale{" +
                "id=" + id +
                ", creationDatetime=" + creationDatetime +
                ", orderId=" + (order != null ? order.getId() : "null") +
                '}';
    }
}