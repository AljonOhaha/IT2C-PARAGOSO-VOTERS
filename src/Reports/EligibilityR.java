/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import static votersp.config.connectDB;

/**
 *
 * @author SCC-COLLEGE
 */
public class EligibilityR {
    
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
