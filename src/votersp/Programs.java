package votersp;

import java.sql.*;
import java.util.Scanner;

public class Programs {
    private static final String SPORTS_LEAGUE_PROGRAM = "Sports League";
    private static final String SCHOLARSHIP_PROGRAM = "Scholarship";

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

    // Method to enroll a voter in a program
    public void enrollInProgram() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Voter ID: ");
        int id = sc.nextInt();
        
        // Validate if the Voter ID exists in the Eligibility table
        if (!isVoterIDValid(id)) {
            System.out.println("Error: Voter ID does not exist in the eligibility table.");
            return; // Exit if the Voter ID is invalid
        }

        // Check if the voter is already enrolled in any program
        if (isAlreadyEnrolled(id)) {
            System.out.println("This voter is already enrolled in a program.");
            return;
        }

        System.out.print("Choose Program (1 - Scholarship, 2 - Sports League): ");
        int program = sc.nextInt();

        String programName = program == 1 ? SCHOLARSHIP_PROGRAM : SPORTS_LEAGUE_PROGRAM;
        String sql = "INSERT INTO programs (PVID, programs) VALUES (?, ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, programName);
            pstmt.executeUpdate();
            System.out.println("Enrolled in " + programName);
        } catch (SQLException e) {
            System.out.println("Error enrolling in program: " + e.getMessage());
        }
    }

   
    private boolean isVoterIDValid(int id) {
        String sql = "SELECT COUNT(*) FROM eligibility WHERE VID = ?";
        
        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            System.out.println("Error checking Voter ID validity: " + e.getMessage());
        }
        return false;  
    }

    
    private boolean isAlreadyEnrolled(int id) {
        String sql = "SELECT COUNT(*) FROM programs WHERE PVID = ?";
        
        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  
            }
        } catch (SQLException e) {
            System.out.println("Error checking enrollment: " + e.getMessage());
        }
        return false;
    }

    
    public void viewPrograms() {
        String sql = "SELECT PID, programs, fname, lname " +
                     "FROM programs " +
                     "JOIN eligibility e ON programs.PVID = e.VID";

        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            System.out.printf("%-10s %-20s %-25s%n", "Voter ID", "Program", "Name");
            System.out.println("----------------------------------------------");

            while (rs.next()) {
                int voterID = rs.getInt("PID");
                String programName = rs.getString("programs");
                String voterName = rs.getString("fname") + " " + rs.getString("lname");
                System.out.printf("%-10d %-20s %-25s%n", voterID, programName, voterName);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving programs: " + e.getMessage());
        }
    }

    
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

        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    // Method to generate a general report for all enrolled voters
    public void generalReports() {
        String sql = "SELECT e.VID, e.fname, e.lname, p.programs " +
                     "FROM programs p " +
                     "JOIN eligibility e ON p.PVID = e.VID " +
                     "ORDER BY e.VID";

        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

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
