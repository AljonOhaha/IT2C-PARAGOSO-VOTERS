
package Reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static votersp.config.connectDB;

/**
 *
 * @author SCC-COLLEGE
 */
public class RegistrationR {
    
    
   public void individualReports() {
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter Voter ID for the report: ");
    int id = sc.nextInt();

    String sql = "SELECT * FROM registration WHERE RID = ?";
    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println("\n===== Individual Voter Report =====");
            System.out.println("Voter ID: " + rs.getInt("RID"));
            System.out.println("First Name: " + rs.getString("RFNAME"));
            System.out.println("Last Name: " + rs.getString("RLNAME"));
            System.out.println("Age: " + rs.getInt("AGE"));
            System.out.println("Purok: " + rs.getString("RPUROK"));
        } else {
            System.out.println("No voter found with ID: " + id);
        }
    } catch (SQLException e) {
        System.out.println("Error fetching voter report: " + e.getMessage());
    }
}
   
   
 public void generalReport() {
    String sql = "SELECT RFNAME, RLNAME, AGE, RPUROK FROM registration ORDER BY RPUROK";

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        System.out.println("\n===== General Voter Report =====");
        System.out.println("Detailed Voter Information:");
        System.out.printf("%-15s %-15s %-5s %-10s%n", "First Name", "Last Name", "Age", "Purok");
        System.out.println("--------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-15s %-15s %-5d %-10s%n",
                    rs.getString("RFNAME"),
                    rs.getString("RLNAME"),
                    rs.getInt("AGE"),
                    rs.getString("RPUROK"));
        }
    } catch (SQLException e) {
        System.out.println("Error generating general report: " + e.getMessage());
    }
}
}
