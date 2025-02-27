package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setTitle("Fibonacci & Expense Tracker");

		// Fibonacci UI (unchanged)
		Label fibLabel = new Label("Enter number of terms:");
		TextField fibInput = new TextField();
		Button generateFib = new Button("Generate Fibonacci");
		Label fibOutput = new Label();

		generateFib.setOnAction(e -> {
			try {
				int terms = Integer.parseInt(fibInput.getText());
				fibOutput.setText(FibonacciGenerator.generateFibonacci(terms));
			} catch (NumberFormatException ex) {
				fibOutput.setText("Invalid input!");
			}
		});

		// Expense Management UI
		Label amountLabel = new Label("Amount:");
		TextField amountInput = new TextField();

		Label categoryLabel = new Label("Category:");
		TextField categoryInput = new TextField();

		// ==================== DATE INPUT WITH VALIDATION & HINT ====================
		Label dateLabel = new Label("Date:");
		TextField dateInput = new TextField();
		Label dateFormatHint = new Label("(yyyy-mm-dd)");
		dateFormatHint.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
		HBox dateBox = new HBox(5, dateInput, dateFormatHint); // Input + hint
		// ===========================================================================

		Label descLabel = new Label("Description:");
		TextField descInput = new TextField();

		Button addExpense = new Button("Add Expense");

		// TableView for displaying expenses
		TableView<Expense> expenseTable = new TableView<>();
		expenseTable.setItems(getExpenseList());

		// Table columns (unchanged)
		TableColumn<Expense, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

		TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

		TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<Expense, String> descCol = new TableColumn<>("Description");
		descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

		// Edit Button Column (unchanged)
		TableColumn<Expense, Void> editCol = new TableColumn<>("Edit");
		editCol.setCellFactory(param -> new TableCell<>() {
			private final Button editButton = new Button("Edit");

			{
				editButton.setOnAction(e -> {
					Expense expense = getTableView().getItems().get(getIndex());
					editExpense(expense, expenseTable);
				});

			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : editButton);
			}
		});

		// Delete Button Column (unchanged)
		TableColumn<Expense, Void> deleteCol = new TableColumn<>("Delete");
		deleteCol.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(e -> {
					Expense expense = getTableView().getItems().get(getIndex());
					ExpenseDAO.deleteExpense(expense.getId());
					expenseTable.setItems(getExpenseList());
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		// Add columns to table
		expenseTable.getColumns().addAll(idCol, amountCol, categoryCol, dateCol, descCol, editCol, deleteCol);

		// Add Expense Button Action (with date validation)
		addExpense.setOnAction(e -> {
			try {
				double amount = Double.parseDouble(amountInput.getText());
				String category = categoryInput.getText().trim();
				String date = dateInput.getText().trim(); // Trim whitespace
				String description = descInput.getText().trim();

				// ================ STRICT DATE VALIDATION ================
				LocalDate.parse(date); // Throws error if invalid
				// ========================================================

				ExpenseDAO.addExpense(amount, category, date, description);
				expenseTable.setItems(getExpenseList());

				// Clear fields only if successful
				amountInput.clear();
				categoryInput.clear();
				dateInput.clear();
				descInput.clear();
			} catch (DateTimeParseException ex) {
				System.out.println("❌ Invalid date ");
			} catch (NumberFormatException ex) {
				System.out.println("❌ Invalid amount!");
			}
		});

		// Layout (updated date section)
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		layout.getChildren().addAll(fibLabel, fibInput, generateFib, fibOutput, amountLabel, amountInput, categoryLabel,
				categoryInput, dateLabel, dateBox, // Updated date input with hint
				descLabel, descInput, addExpense, expenseTable);

		Scene scene = new Scene(layout, 600, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Add CSS
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	// Fetch expenses (unchanged)
	private ObservableList<Expense> getExpenseList() {
		ObservableList<Expense> expenses = FXCollections.observableArrayList();
		for (String data : ExpenseDAO.getExpenses()) {
			String[] parts = data.split(",");
			if (parts.length != 5) {
				System.err.println("Invalid data format: " + data);
				continue;
			}
			try {
				int id = Integer.parseInt(parts[0]);
				double amount = Double.parseDouble(parts[1]);
				String category = parts[2];
				String date = parts[3];
				String description = parts[4];
				expenses.add(new Expense(id, amount, category, date, description));
			} catch (NumberFormatException ex) {
				System.err.println("Error parsing data: " + data);
			}
		}
		return expenses;
	}

	// Edit expense (with date validation)
	private void editExpense(Expense expense, TableView<Expense> expenseTable) {
		Stage editStage = new Stage();
		editStage.setTitle("Edit Expense");

		TextField amountInput = new TextField(String.valueOf(expense.getAmount()));
		TextField categoryInput = new TextField(expense.getCategory());
		TextField dateInput = new TextField(expense.getDate());
		TextField descInput = new TextField(expense.getDescription());

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			try {
				double newAmount = Double.parseDouble(amountInput.getText());
				String newCategory = categoryInput.getText().trim();
				String newDate = dateInput.getText().trim(); // Trim whitespace
				String newDesc = descInput.getText().trim();

				// ================ STRICT DATE VALIDATION ================
				LocalDate.parse(newDate); // Throws error if invalid
				// ========================================================

				ExpenseDAO.updateExpense(expense.getId(), newAmount, newCategory, newDate, newDesc);
				expenseTable.setItems(getExpenseList());
				editStage.close();
			} catch (DateTimeParseException ex) {
				System.out.println("❌ Invalid date ");
			} catch (NumberFormatException ex) {
				System.out.println("❌ Invalid amount!");
			}
		});

		VBox editLayout = new VBox(10, new Label("Amount:"), amountInput, new Label("Category:"), categoryInput,
				new Label("Date:"), dateInput, new Label("Description:"), descInput, saveButton);
		editLayout.setPadding(new Insets(20));
		editStage.setScene(new Scene(editLayout, 300, 250));
		editStage.show();
	}
	

	public static void main(String[] args) {
		
		launch(args);
	}
	
}