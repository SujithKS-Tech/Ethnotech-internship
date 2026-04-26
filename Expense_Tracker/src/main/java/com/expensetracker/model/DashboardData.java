package com.expensetracker.model;

import java.util.List;

public record DashboardData(
        DashboardSummary summary,
        List<CategoryTotal> categoryBreakdown,
        List<Expense> recentExpenses
) {
}
