package com.expensetracker.model;

import java.util.List;

public record DashboardResponse(
        String month,
        String currency,
        DashboardSummary summary,
        List<CategoryTotal> categoryBreakdown,
        List<Expense> recentExpenses
) {
}
