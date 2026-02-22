package com.std;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Student_deatils {
	static String URL = "jdbc:mysql://localhost:3306/college";
    static String USER = "root";
    static String PASSWORD = "Ajay.1@123";

	public static void main(String[] args) {
		


	        Scanner sc = new Scanner(System.in);

	        try {
	            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

	            int choice = 0;

	           
	            while (choice != 5) {

	                System.out.println("\n===== STUDENT CRUD MENU =====");
	                System.out.println("1. Insert");
	                System.out.println("2. Display");
	                System.out.println("3. Update");
	                System.out.println("4. Delete");
	                System.out.println("5. Exit");
	                System.out.print("Enter choice: ");

	                choice = sc.nextInt();

	                switch (choice) {

	                    case 1:
	                        System.out.print("Enter Student Name: ");
	                        String name = sc.next();
	                        System.out.print("Enter Marks: ");
	                        int marks = sc.nextInt();

	                        char grade = calculateGrade(marks);

	                        String insertQuery = "INSERT INTO student(name, marks, grade) VALUES (?, ?, ?)";
	                        PreparedStatement ps = con.prepareStatement(insertQuery);
	                        ps.setString(1, name);
	                        ps.setInt(2, marks);
	                        ps.setString(3, String.valueOf(grade));

	                        ps.executeUpdate();
	                        System.out.println("Student inserted successfully!");
	                        break;

	                    case 2:
	                        String selectQuery = "SELECT * FROM student";
	                        Statement stmt = con.createStatement();
	                        ResultSet rs = stmt.executeQuery(selectQuery);

	                        System.out.println("\nID | Name | Marks | Grade");
	                        while (rs.next()) {
	                            System.out.println(
	                                    rs.getInt("id") + " | " +
	                                    rs.getString("name") + " | " +
	                                    rs.getInt("marks") + " | " +
	                                    rs.getString("grade"));
	                        }
	                        break;

	                    case 3:
	                        System.out.print("Enter Student ID to update: ");
	                        int id = sc.nextInt();
	                        System.out.print("Enter New Marks: ");
	                        int newMarks = sc.nextInt();

	                        char newGrade = calculateGrade(newMarks);

	                        String updateQuery = "UPDATE student SET marks=?, grade=? WHERE id=?";
	                        PreparedStatement ps2 = con.prepareStatement(updateQuery);
	                        ps2.setInt(1, newMarks);
	                        ps2.setString(2, String.valueOf(newGrade));
	                        ps2.setInt(3, id);

	                        int rowsUpdated = ps2.executeUpdate();
	                        if (rowsUpdated > 0)
	                            System.out.println(" Student updated successfully!");
	                        else
	                            System.out.println(" Student ID not found!");
	                        break;

	                    case 4:
	                        System.out.print("Enter Student ID to delete: ");
	                        int deleteId = sc.nextInt();

	                        String deleteQuery = "DELETE FROM student WHERE id=?";
	                        PreparedStatement ps3 = con.prepareStatement(deleteQuery);
	                        ps3.setInt(1, deleteId);

	                        int rowsDeleted = ps3.executeUpdate();
	                        if (rowsDeleted > 0)
	                            System.out.println(" Student deleted successfully!");
	                        else
	                            System.out.println(" Student ID not found!");
	                        break;

	                    case 5:
	                        System.out.println(" Exiting program...");
	                        break;

	                    default:
	                        System.out.println(" Invalid choice! Try again.");
	                }
	            }
	            
	           
	           
	            
	            	

	            con.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        sc.close();
	    }

	    public static char calculateGrade(int marks) {
	        if (marks > 90) return 'A';
	        else if (marks >= 80) return 'B';
	        else if (marks >= 60) return 'C';
	        else if (marks >= 35) return 'D';
	        else return 'F';
	    }

	}


