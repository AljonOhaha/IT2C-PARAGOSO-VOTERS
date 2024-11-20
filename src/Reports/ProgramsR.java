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
public class ProgramsR {
    
    public void individualReports() {
    Scanner sc = new Scanner(System.in);

    
    int id = 0;
    boolean validId = false;
    while (!validId) {
        System.out.print("Enter Voter ID for Program Report: ");
        if (sc.hasNextInt()) {
            id = sc.nextInt();
            if (id > 0) {
                validId = true;
            } else {
                System.out.println("Voter ID must be a positive number.");
            }
        } else {
            System.out.println("Invalid input! Please enter a valid integer for Voter ID.");
            sc.next(); 
        }
    }

    
    String sql = "SELECT e.VID, e.fname, e.lname, p.programs " +
                 "FROM programs p " +
                 "JOIN eligibility e ON p.PVID = e.VID " +
                 "WHERE e.VID = ?";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println("\n===== Individual Program Report =====");
            System.out.println("Voter ID: " + rs.getInt("VID"));
            System.out.println("Name: " + rs.getString("fname") + " " + rs.getString("lname"));
            System.out.println("Enrolled Programs:");
            do {
                System.out.println("- " + rs.getString("programs"));
            } while (rs.next());
        } else {
            System.out.println("No programs found for Voter ID: " + id);
        }
    } catch (SQLException e) {
        System.out.println("Error generating individual program report: " + e.getMessage());
    }
}
    
    
    
    public void generalReport() {
    String sql = "SELECT e.VID, e.fname, e.lname, p.programs " +
                 "FROM programs p " +
                 "JOIN eligibility e ON p.PVID = e.VID " +
                 "ORDER BY e.VID";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        
        if (!rs.isBeforeFirst()) {
            System.out.println("No programs found in the system.");
            return;
        }

        System.out.println("\n===== General Program Report =====");
        System.out.printf("%-10s %-20s %-25s%n", "Voter ID", "Name", "Program");
        System.out.println("--------------------------------------------------");

        while (rs.next()) {
            int voterID = rs.getInt("VID");
            String voterName = rs.getString("fname") + " " + rs.getString("lname");
            String programName = rs.getString("programs");

            System.out.printf("%-10d %-20s %-25s%n", voterID, voterName, programName);
        }
    } catch (SQLException e) {
        System.out.println("Error generating general program report: " + e.getMessage());
    }
}
    
    
}
