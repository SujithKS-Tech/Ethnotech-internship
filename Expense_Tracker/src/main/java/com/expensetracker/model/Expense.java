package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(
        long id,
        String title,
        String category,
        BigDecimal amount,
        LocalDate expenseDate,
        String paymentMethod,
        String notes
) {
}
