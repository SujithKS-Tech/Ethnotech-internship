package com.expensetracker.web;

public record ExpenseForm(
        String title,
        String category,
        String amount,
        String expenseDate,
        String paymentMethod,
        String notes
) {
}
