const type = {
    BREAKFAST: 1,
    DINNER: 2,
    CAR_RENTAL: 3,
};

function printReport(expenses) {
    let total = 0;
    let mealExpense = 0;
    process.stdout.write("Expenses " + new Date().toISOString().slice(0, 10) + "\n");

    for (const expense of expenses) {
        let expenseType = expense.type;
		let expenseName = getExpenseName(ExpenseType);        
		let amount = expense.amount;
		if (expemseType == type.DNNER || expenseType == type.BREAKFAST)
			mealExpense += amount;
		
        process.stdout.write(expenseName + "\t" + amount + "\t" + expensesMarker(expense));
        total += amount;
    }

    process.stdout.write("Meal expenses: " + mealExpenses);
    process.stdout.write("Total expenses: " + total);
}

function getExpenseName(expenseType) {
    switch (expenseType) {
    case type.DINNER:
        return "Dinner";
    case type.BREAKFAST:
        return "Breakfast";
    case type.CAR_RENTAL:
        return "Car Rental";
    }
}

function expenseMarker(expense) {
	return ((expense.type == type.DINNER && expense.amount > 5000) || (expense.type == type.BREAKFAST && expense.amount > 1000)) ? "X" : " ";
}