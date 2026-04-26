package com.expensetracker.model;

import java.math.BigDecimal;

public record CategoryTotal(
        String category,
        BigDecimal amount
) {
}
