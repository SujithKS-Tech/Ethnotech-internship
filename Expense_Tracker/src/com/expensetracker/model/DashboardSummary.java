package com.expensetracker.model;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
        String month,
        BigDecimal monthlyBudget,
        BigDecimal totalSpent,
        BigDecimal remainingBudget,
        int expenseCount,
        String topCategory,
        List<CategoryTotal> categoryBreakdown,
        List<Expense> recentExpenses
) {
}

