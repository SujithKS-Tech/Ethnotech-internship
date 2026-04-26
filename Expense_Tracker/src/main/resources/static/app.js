const state = {
    month: "",
    category: "All",
    currency: "INR",
    expenses: [],
    dashboard: null,
    editingId: null
};

const defaultCategories = [
    "All",
    "Food",
    "Groceries",
    "Travel",
    "Rent",
    "Bills",
    "Shopping",
    "Health",
    "Education",
    "Entertainment",
    "Other"
];

const API_BASE = resolveApiBase();

const elements = {
    monthPicker: document.getElementById("monthPicker"),
    categoryFilter: document.getElementById("categoryFilter"),
    refreshButton: document.getElementById("refreshButton"),
    budgetValue: document.getElementById("budgetValue"),
    budgetCaption: document.getElementById("budgetCaption"),
    spentValue: document.getElementById("spentValue"),
    spentCaption: document.getElementById("spentCaption"),
    remainingValue: document.getElementById("remainingValue"),
    remainingCaption: document.getElementById("remainingCaption"),
    topCategoryValue: document.getElementById("topCategoryValue"),
    topCategoryCaption: document.getElementById("topCategoryCaption"),
    budgetForm: document.getElementById("budgetForm"),
    budgetInput: document.getElementById("budgetInput"),
    expenseForm: document.getElementById("expenseForm"),
    titleInput: document.getElementById("titleInput"),
    amountInput: document.getElementById("amountInput"),
    dateInput: document.getElementById("dateInput"),
    categoryInput: document.getElementById("categoryInput"),
    paymentMethodInput: document.getElementById("paymentMethodInput"),
    notesInput: document.getElementById("notesInput"),
    formTitle: document.getElementById("formTitle"),
    formSubtitle: document.getElementById("formSubtitle"),
    submitExpenseButton: document.getElementById("submitExpenseButton"),
    resetFormButton: document.getElementById("resetFormButton"),
    expenseList: document.getElementById("expenseList"),
    expenseCountLabel: document.getElementById("expenseCountLabel"),
    breakdownList: document.getElementById("breakdownList"),
    recentList: document.getElementById("recentList"),
    budgetProgressCard: document.getElementById("budgetProgressCard"),
    categorySuggestions: document.getElementById("categorySuggestions"),
    toast: document.getElementById("toast")
};

let toastTimer;

document.addEventListener("DOMContentLoaded", async () => {
    const defaults = defaultDateParts();
    state.month = defaults.month;
    elements.monthPicker.value = defaults.month;
    elements.dateInput.value = defaults.date;

    renderCategoryControls();
    bindEvents();

    await loadDashboard();
});

function bindEvents() {
    elements.monthPicker.addEventListener("change", async (event) => {
        state.month = event.target.value;
        await loadDashboard();
    });

    elements.categoryFilter.addEventListener("change", async (event) => {
        state.category = event.target.value;
        await loadExpenses();
    });

    elements.refreshButton.addEventListener("click", async () => {
        await loadDashboard();
        showToast("Dashboard refreshed.");
    });

    elements.budgetForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const payload = new URLSearchParams({
            month: state.month,
            budget: elements.budgetInput.value.trim()
        });

        try {
            await sendForm("/api/budget", "POST", payload);
            await loadDashboard();
            showToast("Budget saved.");
        } catch (error) {
            showToast(error.message, true);
        }
    });

    elements.expenseForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const payload = new URLSearchParams({
            title: elements.titleInput.value.trim(),
            amount: elements.amountInput.value.trim(),
            expenseDate: elements.dateInput.value,
            category: elements.categoryInput.value.trim(),
            paymentMethod: elements.paymentMethodInput.value,
            notes: elements.notesInput.value.trim()
        });

        const isEditing = Boolean(state.editingId);
        const method = isEditing ? "PUT" : "POST";
        const url = isEditing ? `/api/expenses/${state.editingId}` : "/api/expenses";

        try {
            await sendForm(url, method, payload);
            resetExpenseForm();
            await loadDashboard();
            showToast(isEditing ? "Expense updated." : "Expense added.");
        } catch (error) {
            showToast(error.message, true);
        }
    });

    elements.resetFormButton.addEventListener("click", () => {
        resetExpenseForm();
    });

    elements.expenseList.addEventListener("click", async (event) => {
        const button = event.target.closest("button[data-action]");
        if (!button) {
            return;
        }

        const expenseId = Number(button.dataset.id);
        if (!Number.isFinite(expenseId) || expenseId <= 0) {
            return;
        }

        if (button.dataset.action === "edit") {
            const expense = state.expenses.find((item) => item.id === expenseId);
            if (expense) {
                populateExpenseForm(expense);
            }
            return;
        }

        if (button.dataset.action === "delete") {
            const confirmed = window.confirm("Delete this expense?");
            if (!confirmed) {
                return;
            }

            try {
                await sendJson(`/api/expenses/${expenseId}`, "DELETE");
                await loadDashboard();
                showToast("Expense deleted.");
            } catch (error) {
                showToast(error.message, true);
            }
        }
    });
}

async function loadDashboard() {
    try {
        const dashboard = await fetchJson(`/api/dashboard?month=${encodeURIComponent(state.month)}`);
        state.dashboard = dashboard;
        state.currency = dashboard.currency || "INR";
        await loadExpenses();
        renderSummary();
        renderBreakdown();
        renderRecentExpenses();
        renderBudgetProgress();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function loadExpenses() {
    try {
        const params = new URLSearchParams({ month: state.month });
        if (state.category !== "All") {
            params.set("category", state.category);
        }

        const payload = await fetchJson(`/api/expenses?${params.toString()}`);
        state.expenses = payload.items || [];
        renderCategoryControls();
        renderExpenses();
    } catch (error) {
        showToast(error.message, true);
    }
}

function renderSummary() {
    if (!state.dashboard) {
        return;
    }

    const summary = state.dashboard.summary;
    const monthlyBudget = Number(summary.monthlyBudget || 0);
    const totalSpent = Number(summary.totalSpent || 0);
    const remainingBudget = Number(summary.remainingBudget || 0);
    const expenseCount = Number(summary.expenseCount || 0);

    elements.budgetValue.textContent = formatCurrency(monthlyBudget);
    elements.spentValue.textContent = formatCurrency(totalSpent);
    elements.remainingValue.textContent = formatCurrency(remainingBudget);
    elements.topCategoryValue.textContent = summary.topCategory || "No expenses yet";

    elements.budgetCaption.textContent = monthlyBudget > 0
        ? `Limit set for ${state.month}.`
        : "Set a budget to activate control mode.";

    elements.spentCaption.textContent = `${expenseCount} expense${expenseCount === 1 ? "" : "s"} logged this month.`;

    if (monthlyBudget === 0) {
        elements.remainingCaption.textContent = "No limit set yet.";
    } else if (remainingBudget >= 0) {
        elements.remainingCaption.textContent = "You are inside your planned range.";
    } else {
        elements.remainingCaption.textContent = "Overspent this month.";
    }

    elements.topCategoryCaption.textContent = expenseCount > 0
        ? "Highest total spend category."
        : "Start adding expenses to show trends.";

    elements.budgetInput.value = monthlyBudget > 0 ? monthlyBudget.toFixed(2) : "";
}

function renderBudgetProgress() {
    if (!state.dashboard) {
        return;
    }

    const summary = state.dashboard.summary;
    const monthlyBudget = Number(summary.monthlyBudget || 0);
    const totalSpent = Number(summary.totalSpent || 0);
    const remainingBudget = Number(summary.remainingBudget || 0);
    const percent = monthlyBudget > 0 ? Math.min((totalSpent / monthlyBudget) * 100, 100) : 0;

    if (monthlyBudget === 0) {
        elements.budgetProgressCard.innerHTML = `
            <div class="empty-state">
                Set a budget for <strong>${escapeHtml(state.month)}</strong> to view progress.
            </div>
        `;
        return;
    }

    elements.budgetProgressCard.innerHTML = `
        <div class="progress-meta">
            <div>
                <p class="meta">Spent</p>
                <h3>${formatCurrency(totalSpent)}</h3>
            </div>
            <div>
                <p class="meta">Usage</p>
                <h3>${percent.toFixed(0)}%</h3>
            </div>
        </div>
        <div class="progress-bar">
            <div class="progress-fill" style="width: ${percent}%;"></div>
        </div>
        <p class="note">
            ${remainingBudget >= 0
                ? `${formatCurrency(remainingBudget)} left in this month's cap.`
                : `${formatCurrency(Math.abs(remainingBudget))} over the cap.`}
        </p>
    `;
}

function renderBreakdown() {
    const items = state.dashboard?.categoryBreakdown || [];
    if (!items.length) {
        elements.breakdownList.innerHTML = `<div class="empty-state">No category split for this month.</div>`;
        return;
    }

    const maxAmount = Math.max(...items.map((item) => Number(item.amount || 0)));
    const totalAmount = items.reduce((sum, item) => sum + Number(item.amount || 0), 0);

    elements.breakdownList.innerHTML = items.map((item) => {
        const amount = Number(item.amount || 0);
        const width = maxAmount > 0 ? (amount / maxAmount) * 100 : 0;
        const share = totalAmount > 0 ? ((amount / totalAmount) * 100).toFixed(0) : 0;

        return `
            <article class="breakdown-item">
                <div class="breakdown-row">
                    <span class="category-pill">${escapeHtml(item.category)}</span>
                    <span class="breakdown-amount">${formatCurrency(amount)} (${share}%)</span>
                </div>
                <div class="mini-bar">
                    <span style="width: ${width}%;"></span>
                </div>
            </article>
        `;
    }).join("");
}

function renderRecentExpenses() {
    const items = state.dashboard?.recentExpenses || [];
    if (!items.length) {
        elements.recentList.innerHTML = `<div class="empty-state">No recent activity yet.</div>`;
        return;
    }

    elements.recentList.innerHTML = items.map((expense) => `
        <article class="recent-item">
            <div class="recent-row">
                <div>
                    <div class="recent-title">${escapeHtml(expense.title)}</div>
                    <div class="recent-meta">
                        <span>${escapeHtml(expense.category)}</span>
                        <span>${formatDate(expense.expenseDate)}</span>
                    </div>
                </div>
                <div class="recent-amount">${formatCurrency(expense.amount)}</div>
            </div>
        </article>
    `).join("");
}

function renderExpenses() {
    const count = state.expenses.length;
    elements.expenseCountLabel.textContent = `${count} expense${count === 1 ? "" : "s"}`;

    if (!count) {
        elements.expenseList.innerHTML = `<div class="empty-state">No expenses for this filter.</div>`;
        return;
    }

    elements.expenseList.innerHTML = state.expenses.map((expense) => `
        <article class="expense-item">
            <div class="expense-row">
                <div>
                    <div class="expense-title">${escapeHtml(expense.title)}</div>
                    <div class="expense-meta">
                        <span class="category-pill">${escapeHtml(expense.category)}</span>
                        <span>${formatDate(expense.expenseDate)}</span>
                        <span>${escapeHtml(expense.paymentMethod)}</span>
                    </div>
                </div>
                <div class="expense-amount">${formatCurrency(expense.amount)}</div>
            </div>
            ${expense.notes ? `<p class="expense-notes">${escapeHtml(expense.notes)}</p>` : ""}
            <div class="expense-actions">
                <button type="button" class="edit-button" data-action="edit" data-id="${expense.id}">Edit</button>
                <button type="button" class="delete-button" data-action="delete" data-id="${expense.id}">Delete</button>
            </div>
        </article>
    `).join("");
}

function renderCategoryControls() {
    const categories = new Set(defaultCategories);
    state.expenses.forEach((expense) => categories.add(expense.category));
    (state.dashboard?.categoryBreakdown || []).forEach((item) => categories.add(item.category));

    const options = Array.from(categories).filter(Boolean);
    if (!options.includes(state.category)) {
        state.category = "All";
    }

    elements.categoryFilter.innerHTML = options
        .map((category) => `<option value="${escapeAttribute(category)}">${escapeHtml(category)}</option>`)
        .join("");
    elements.categoryFilter.value = state.category;

    elements.categorySuggestions.innerHTML = options
        .filter((category) => category !== "All")
        .map((category) => `<option value="${escapeAttribute(category)}"></option>`)
        .join("");
}

function populateExpenseForm(expense) {
    state.editingId = expense.id;
    elements.titleInput.value = expense.title;
    elements.amountInput.value = Number(expense.amount).toFixed(2);
    elements.dateInput.value = expense.expenseDate;
    elements.categoryInput.value = expense.category;
    elements.paymentMethodInput.value = expense.paymentMethod;
    elements.notesInput.value = expense.notes || "";
    elements.formTitle.textContent = "Edit expense";
    elements.formSubtitle.textContent = "Update details and save changes.";
    elements.submitExpenseButton.textContent = "Update Expense";
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetExpenseForm() {
    state.editingId = null;
    elements.expenseForm.reset();

    const defaults = defaultDateParts();
    elements.dateInput.value = defaults.date;
    elements.paymentMethodInput.value = "UPI";
    elements.formTitle.textContent = "Log an expense";
    elements.formSubtitle.textContent = "Add entries fast while details are fresh.";
    elements.submitExpenseButton.textContent = "Save Expense";
}

async function sendForm(path, method, body) {
    const response = await fetch(`${API_BASE}${path}`, {
        method,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Accept": "application/json"
        },
        body: body.toString()
    });

    const payload = await safeReadJson(response);
    if (!response.ok) {
        throw new Error(payload?.message || defaultApiError());
    }
    return payload;
}

async function sendJson(path, method) {
    const response = await fetch(`${API_BASE}${path}`, {
        method,
        headers: {
            "Accept": "application/json"
        }
    });

    const payload = await safeReadJson(response);
    if (!response.ok) {
        throw new Error(payload?.message || defaultApiError());
    }
    return payload;
}

async function fetchJson(path) {
    const response = await fetch(`${API_BASE}${path}`, {
        headers: {
            "Accept": "application/json"
        }
    });

    const payload = await safeReadJson(response);
    if (!response.ok) {
        throw new Error(payload?.message || defaultApiError());
    }
    return payload;
}

async function safeReadJson(response) {
    const text = await response.text();
    if (!text) {
        return {};
    }
    try {
        return JSON.parse(text);
    } catch (error) {
        return {};
    }
}

function resolveApiBase() {
    if (window.location.protocol === "file:") {
        return "http://localhost:8080";
    }

    if (window.location.port === "8080") {
        return "";
    }

    return "http://localhost:8080";
}

function defaultApiError() {
    return "Cannot reach backend. Start Spring Boot on http://localhost:8080.";
}

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: state.currency || "INR",
        maximumFractionDigits: 2
    }).format(Number(value || 0));
}

function formatDate(value) {
    const date = new Date(`${value}T00:00:00`);
    return new Intl.DateTimeFormat("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).format(date);
}

function defaultDateParts() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return {
        month: `${year}-${month}`,
        date: `${year}-${month}-${day}`
    };
}

function showToast(message, isError = false) {
    clearTimeout(toastTimer);
    elements.toast.textContent = message;
    elements.toast.classList.add("visible");
    elements.toast.classList.toggle("error", isError);

    toastTimer = setTimeout(() => {
        elements.toast.classList.remove("visible");
    }, 3000);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

function escapeAttribute(value) {
    return escapeHtml(value);
}
