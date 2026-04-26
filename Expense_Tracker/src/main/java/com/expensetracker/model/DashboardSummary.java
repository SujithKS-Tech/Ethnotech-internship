package com.expensetracker.model;

import java.math.BigDecimal;

public record DashboardSummary(
        BigDecimal monthlyBudget,
        BigDecimal totalSpent,
        BigDecimal remainingBudget,
        int expenseCount,
        String topCategory
) {
}
