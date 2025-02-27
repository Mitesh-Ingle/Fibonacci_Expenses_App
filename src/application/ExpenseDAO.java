package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

	  // Method to insert a new expense into the database
	public static void addExpense(double amount, String category, String date, String description) {
	    String sql = "INSERT INTO expenses (amount, category, date, description) VALUES (?, ?, ?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection(); // Auto-closes connection
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setDouble(1, amount);
	        pstmt.setString(2, category);
	        pstmt.setString(3, date);
	        pstmt.setString(4, description);
	        pstmt.executeUpdate();
	        System.out.println("✅ Expense added successfully.");

	    } catch (SQLException e) {
	        System.out.println("❌ Error adding expense: " + e.getMessage());
	    }
	}

    // Method to fetch all expenses from the database (FIXED)
    public static List<String> getList() {
        List<String> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date, description FROM expenses"; // Explicitly select columns

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Format data with commas
                String data = String.format("%d,%f,%s,%s,%s",
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("date"),
                        rs.getString("description"));
                expenses.add(data);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching expenses: " + e.getMessage());
        }

        return expenses;
    }

//update or edit expenses
	public static void updateExpense(int id, double amount, String category, String date, String description) {
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("UPDATE expenses SET amount = ?, category = ?, date = ?, description = ? WHERE id = ?")) {
	        
	        stmt.setDouble(1, amount);
	        stmt.setString(2, category);
	        stmt.setString(3, date);
	        stmt.setString(4, description);
	        stmt.setInt(5, id);
	        
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	//to delete the expenses
	public static void deleteExpense(int id) {
	    String sql = "DELETE FROM expenses WHERE id = ?";

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, id);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("✅ Expense deleted successfully.");
	        } else {
	            System.out.println("❌ No expense found with ID: " + id);
	        }
	    } catch (SQLException e) {
	        System.out.println("❌ Error deleting expense: " + e.getMessage());
	    }
	}

	
	// Method to fetch all expenses from the database
	public static List<String> getExpenses() {
	    List<String> expenses = new ArrayList<>();
	    String sql = "SELECT id, amount, category, date, description FROM expenses"; // Explicitly select columns

	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            // Format data with commas
	            String data = String.format("%d,%f,%s,%s,%s",
	                    rs.getInt("id"),
	                    rs.getDouble("amount"),
	                    rs.getString("category"),
	                    rs.getString("date"),
	                    rs.getString("description")); // Include description
	            expenses.add(data);
	        }

	    } catch (SQLException e) {
	        System.out.println("❌ Error fetching expenses: " + e.getMessage());
	    }

	    return expenses;
	}
}
