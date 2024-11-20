package votersp;

import java.sql.*;
import java.util.Scanner;

public class Eligibility {
    private static final int MIN_ELIGIBLE_AGE = 15;
    private static final int MAX_ELIGIBLE_AGE = 30;

    private Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:votersp.db");
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return con;
    }

  public void addEligibility() {
    Scanner sc = new Scanner(System.in);

    System.out.print("Enter Registration ID: ");
    int regId = sc.nextInt();
    sc.nextLine(); 

    String sqlCheckRegistration = "SELECT COUNT(*) FROM registration WHERE RID = ?";
    String sqlCheckEligibility = "SELECT COUNT(*) FROM eligibility WHERE RRID = ?";
    String sqlSelect = "SELECT RFNAME, RLNAME, AGE, RPUROK FROM registration WHERE RID = ?";
    String sqlInsert = "INSERT INTO eligibility (RRID, fname, lname, purok, eage, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = connectDB();
         PreparedStatement pstmtCheckRegistration = conn.prepareStatement(sqlCheckRegistration);
         PreparedStatement pstmtCheckEligibility = conn.prepareStatement(sqlCheckEligibility);
         PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
         PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {

        
        pstmtCheckRegistration.setInt(1, regId);
        ResultSet rsCheckRegistration = pstmtCheckRegistration.executeQuery();
        if (rsCheckRegistration.next() && rsCheckRegistration.getInt(1) == 0) {
            System.out.println("Error: Registration ID does not exist. Please provide a valid ID.");
            return; 
        }

        
        pstmtCheckEligibility.setInt(1, regId);
        ResultSet rsCheckEligibility = pstmtCheckEligibility.executeQuery();
        if (rsCheckEligibility.next() && rsCheckEligibility.getInt(1) > 0) {
            System.out.println("Error: Eligibility record already exists for this Registration ID.");
            return; 
        }

        
        pstmtSelect.setInt(1, regId);
        ResultSet rsSelect = pstmtSelect.executeQuery();
        if (rsSelect.next()) {
            String fname = rsSelect.getString("RFNAME");
            String lname = rsSelect.getString("RLNAME");
            int age = rsSelect.getInt("AGE");
            String purok = rsSelect.getString("RPUROK");

            
            String status = (age >= MIN_ELIGIBLE_AGE && age <= MAX_ELIGIBLE_AGE)
                    ? "Eligible for SK programs"
                    : "Not eligible for SK programs";

            
            pstmtInsert.setInt(1, regId);
            pstmtInsert.setString(2, fname);
            pstmtInsert.setString(3, lname);
            pstmtInsert.setString(4, purok);
            pstmtInsert.setInt(5, age);
            pstmtInsert.setString(6, status);
            pstmtInsert.executeUpdate();

            System.out.println("Eligibility record added successfully!");
        }
    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
}

   public void viewEligibility() {
    String sql = "SELECT VID, fname, lname, eage, purok, status FROM eligibility";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        System.out.printf("%-5s %-15s %-15s %-5s %-10s %-25s%n", 
                          "ID", "First Name", "Last Name", "Age", "Purok", "Status");
        System.out.println("-----------------------------------------------------------------------");

        while (rs.next()) {
            int vid = rs.getInt("VID");
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            int age = rs.getInt("eage");
            String purok = rs.getString("purok");
            String status = rs.getString("status");

            System.out.printf("%-5d %-15s %-15s %-5d %-10s %-25s%n", 
                              vid, fname, lname, age, purok, status);
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving eligibility data: " + e.getMessage());
    }
}

  


    public void deleteEligibility() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Eligibility ID to Delete: ");
        int vid = sc.nextInt();

        String sql = "DELETE FROM eligibility WHERE VID = ?";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vid);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Eligibility record deleted successfully!");
            } else {
                System.out.println("No eligibility record found with ID: " + vid);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting eligibility record: " + e.getMessage());
        }
    }



public void individualReports() {
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter Registration ID: ");
    int regId = sc.nextInt();

    String sqlSelect = "SELECT fname, lname, purok, eage, status FROM eligibility WHERE RRID = ?";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {

        pstmt.setInt(1, regId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println("\n===== Individual Eligibility Report =====");
            System.out.println("First Name: " + rs.getString("fname"));
            System.out.println("Last Name: " + rs.getString("lname"));
            System.out.println("Age: " + rs.getInt("eage"));
            System.out.println("Purok: " + rs.getString("purok"));
            System.out.println("Status: " + rs.getString("status"));
        } else {
            System.out.println("No eligibility record found for Registration ID: " + regId);
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving individual eligibility report: " + e.getMessage());
    }
}




public void generalReports() {
    String sqlSelect = "SELECT fname, lname, purok, eage, status FROM eligibility";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
         ResultSet rs = pstmt.executeQuery()) {

        int eligibleCount = 0;
        int ineligibleCount = 0;

        System.out.println("\n===== General Eligibility Report =====");
        System.out.printf("%-15s %-15s %-10s %-5s %-25s%n", "First Name", "Last Name", "Purok", "Age", "Status");
        System.out.println("-----------------------------------------------------------------------");

        while (rs.next()) {
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            String purok = rs.getString("purok");
            int age = rs.getInt("eage");
            String status = rs.getString("status");

            if ("Eligible for SK programs".equalsIgnoreCase(status)) {
                eligibleCount++;
            } else {
                ineligibleCount++;
            }

            System.out.printf("%-15s %-15s %-10s %-5d %-25s%n", fname, lname, purok, age, status);
        }

        System.out.println("\nSummary:");
        System.out.println("Total Eligible: " + eligibleCount);
        System.out.println("Total Not Eligible: " + ineligibleCount);

    } catch (SQLException e) {
        System.out.println("Error retrieving general eligibility report: " + e.getMessage());
    }
}
}
