package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Expence {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty description;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty category;
    private final SimpleObjectProperty<LocalDate> date;

    public Expence(int id, String description, double amount, String category, LocalDate date) {
        this.id = new SimpleIntegerProperty(id);
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleObjectProperty<>(date);
    }

    // Getters for properties
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty descriptionProperty() { return description; }
    public SimpleDoubleProperty amountProperty() { return amount; }
    public SimpleStringProperty categoryProperty() { return category; }
    public SimpleObjectProperty<LocalDate> dateProperty() { return date; }

    // Regular getters
    public int getId() { return id.get(); }
    public String getDescription() { return description.get(); }
    public double getAmount() { return amount.get(); }
    public String getCategory() { return category.get(); }
    public LocalDate getDate() { return date.get(); }
}