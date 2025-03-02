package application;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class MainClass extends Application {
    private TableView<Expence> expenseTable = new TableView<>();
    private TextField descriptionField = new TextField();
    private TextField amountField = new TextField();
    private ComboBox<String> categoryField = new ComboBox<>();
    private DatePicker dateField = new DatePicker();

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password"; // Replace with your MySQL password

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        // Set up input fields
        descriptionField.setPromptText("Description");
        amountField.setPromptText("Amount");
        categoryField.setPromptText("Category");
        categoryField.getItems().addAll("Food", "Transport", "Entertainment", "Utilities", "Other");
        dateField.setPromptText("Date");

        // Set up buttons
        Button addButton = new Button("Add Expense");
        addButton.setOnAction(e -> addExpense());

        Button editButton = new Button("Edit Expense");
        editButton.setOnAction(e -> editExpense());

        Button deleteButton = new Button("Delete Expense");
        deleteButton.setOnAction(e -> deleteExpense());

        Button filterButton = new Button("Filter by Category");
        filterButton.setOnAction(e -> filterByCategory());

        // Set up table columns
        TableColumn<Expence, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Expence, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Expence, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        TableColumn<Expence, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Expence, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        expenseTable.getColumns().addAll(idColumn, descriptionColumn, amountColumn, categoryColumn, dateColumn);

        // Layout
        HBox inputBox = new HBox(10, descriptionField, amountField, categoryField, dateField, addButton);
        HBox buttonBox = new HBox(10, editButton, deleteButton, filterButton);
        VBox root = new VBox(10, inputBox, expenseTable, buttonBox);
        root.setPadding(new Insets(10));

        // Load expenses from the database
        loadExpenses();

        // Set up the scene
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    private void addExpense() {
        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        String category = categoryField.getValue();
        LocalDate date = dateField.getValue();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO expenses (description, amount, category, date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setDate(4, Date.valueOf(date));
            pstmt.executeUpdate();

            // Clear input fields
            descriptionField.clear();
            amountField.clear();
            categoryField.setValue(null);
            dateField.setValue(null);

            // Refresh the table
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editExpense() {
        Expence selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedExpense == null) return;

        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        String category = categoryField.getValue();
        LocalDate date = dateField.getValue();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE expenses SET description = ?, amount = ?, category = ?, date = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setDate(4, Date.valueOf(date));
            pstmt.setInt(5, selectedExpense.getId());
            pstmt.executeUpdate();

            // Refresh the table
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteExpense() {
        Expence selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedExpense == null) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM expenses WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, selectedExpense.getId());
            pstmt.executeUpdate();

            // Refresh the table
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterByCategory() {
        String category = categoryField.getValue();
        if (category == null) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM expenses WHERE category = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            expenseTable.getItems().clear();
            while (rs.next()) {
                expenseTable.getItems().add(new Expence(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getDate("date").toLocalDate()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM expenses ORDER BY date DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            expenseTable.getItems().clear();
            while (rs.next()) {
                expenseTable.getItems().add(new Expence(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getDate("date").toLocalDate()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}