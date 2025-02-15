import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Expense {
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Expense(double amount, String category, String description, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " | " + amount + " | " + category + " | " + description;
    }
}

class ExpenseManager {
    private List<Expense> expenses;
    private static final String FILE_NAME = "expenses.txt";
    private static final String[] CATEGORIES = {"Food", "Travel", "Utilities", "Entertainment", "Health", "Others"};

    public ExpenseManager() {
        expenses = new ArrayList<>();
        loadFromFile();
    }

    public void addExpense(double amount, String category, String description, LocalDate date) {
        Expense expense = new Expense(amount, category, description, date);
        expenses.add(expense);
        saveToFile();
    }

    public void viewExpensesByCategory() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (String category : CATEGORIES) {
            categoryTotals.put(category, 0.0);
        }

        for (Expense expense : expenses) {
            categoryTotals.put(expense.getCategory(), categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        System.out.println("\nExpense Summary by Category:");
        for (String category : CATEGORIES) {
            System.out.println(category + ": $" + categoryTotals.get(category));
        }
    }

    public void viewExpensesByPeriod(LocalDate startDate, LocalDate endDate) {
        double total = 0;
        System.out.println("\nExpenses from " + startDate + " to " + endDate + ":");
        for (Expense expense : expenses) {
            if (!expense.getDate().isBefore(startDate) && !expense.getDate().isAfter(endDate)) {
                System.out.println(expense);
                total += expense.getAmount();
            }
        }
        System.out.println("Total Expenses: $" + total);
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense expense : expenses) {
                writer.write(expense.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No previous expenses found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length < 4) continue;
                
                LocalDate date = LocalDate.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                double amount = Double.parseDouble(parts[1]);
                String category = parts[2];
                String description = parts[3];

                expenses.add(new Expense(amount, category, description, date));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    public static String[] getCategories() {
        return CATEGORIES;
    }
}

public class DailyExpenseTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();
        
        while (true) {
            System.out.println("\n1. Add Expense\n2. View Expenses by Period\n3. View Expenses by Category\n4. Exit");
            System.out.print("Choose an option: ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter amount: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); 

                        System.out.println("Select a category:");
                        String[] categories = ExpenseManager.getCategories();
                        for (int i = 0; i < categories.length; i++) {
                            System.out.println((i + 1) + ". " + categories[i]);
                        }
                        System.out.print("Choose a category (1-" + categories.length + "): ");
                        
                        int categoryChoice = scanner.nextInt();
                        scanner.nextLine(); 

                        if (categoryChoice < 1 || categoryChoice > categories.length) {
                            System.out.println("Invalid category selection.");
                            break;
                        }

                        String category = categories[categoryChoice - 1];
                        System.out.print("Enter description: ");
                        String description = scanner.nextLine();
                        LocalDate date = LocalDate.now();
                        manager.addExpense(amount, category, description, date);
                        System.out.println("Expense added successfully!");
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Amount must be a number.");
                        scanner.nextLine();
                    }
                    break;
                
                case 2:
                    try {
                        System.out.print("Enter start date (YYYY-MM-DD): ");
                        LocalDate startDate = LocalDate.parse(scanner.nextLine());
                        System.out.print("Enter end date (YYYY-MM-DD): ");
                        LocalDate endDate = LocalDate.parse(scanner.nextLine());
                        manager.viewExpensesByPeriod(startDate, endDate);
                    } catch (Exception e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    break;

                case 3:
                    manager.viewExpensesByCategory();
                    break;
                
                case 4:
                    System.out.println("Exiting program.");
                    scanner.close();
                    return;
                
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
