//package mysql;

import java.sql.*;
import java.util.Scanner;

public class FoodDeliveryApp {

    static String Driver = "com.mysql.cj.jdbc.Driver";
    static final String url = "jdbc:mysql://localhost:3306/online_fooddelivery";
    static final String username = "root";
    static final String password = "Ajay.1@123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            Class.forName(Driver);  // Load driver
        } catch (Exception e) {
            System.out.println("Driver not loaded!");
            return;
        }

        while (true) {

            System.out.println("\n===== ONLINE FOOD DELIVERY =====");
            System.out.println("1. Insert Customer");
            System.out.println("2. Insert Restaurant");
            System.out.println("3. Insert Food Item");
            System.out.println("4. Insert Delivery Person");
            System.out.println("5. Insert Order");
            System.out.println("6. View All Tables");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1: insertCustomer(); break;
                case 2: insertRestaurant(); break;
                case 3: insertFoodItem(); break;
                case 4: insertDeliveryPerson(); break;
                case 5: insertOrder(); break;
                case 6: viewAll(); break;
                case 7: System.exit(0);
                default: System.out.println("Invalid Choice");
            }
        }
    }

    // ---------------- INSERT CUSTOMER ----------------

    static void insertCustomer() {
        try (Connection con = DriverManager.getConnection(url, username, password)) {

            System.out.print("Customer Name: ");
            String name = sc.nextLine();

            System.out.print("Phone: ");
            String phone = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Address: ");
            String address = sc.nextLine();

            PreparedStatement ps = con.prepareStatement(
                    "insert into customer(customer_name,phone,email,address) values(?,?,?,?)");

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, address);

            ps.executeUpdate();
            System.out.println("Customer Inserted Successfully!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ---------------- INSERT RESTAURANT ----------------

    static void insertRestaurant() {
        try (Connection con = DriverManager.getConnection(url, username, password)) {

            System.out.print("Restaurant Name: ");
            String name = sc.nextLine();

            System.out.print("Location: ");
            String location = sc.nextLine();

            System.out.print("Phone: ");
            String phone = sc.nextLine();

            PreparedStatement ps = con.prepareStatement(
                    "insert into restaurant(restaurant_name,location,phone) values(?,?,?)");

            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, phone);

            ps.executeUpdate();
            System.out.println("Restaurant Inserted!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ---------------- INSERT FOOD ITEM ----------------

    static void insertFoodItem() {
        try (Connection con = DriverManager.getConnection(url, username, password)) {

            System.out.print("Food Name: ");
            String name = sc.nextLine();

            System.out.print("Price: ");
            double price = sc.nextDouble();
            sc.nextLine();

            System.out.print("Category: ");
            String category = sc.nextLine();

            System.out.print("Restaurant ID: ");
            int restId = sc.nextInt();

            PreparedStatement ps = con.prepareStatement(
                    "insert into food_item(food_name,price,category,restaurant_id) values(?,?,?,?)");

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, category);
            ps.setInt(4, restId);

            ps.executeUpdate();
            System.out.println("Food Item Inserted!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ---------------- INSERT DELIVERY PERSON ----------------

    static void insertDeliveryPerson() {
        try (Connection con = DriverManager.getConnection(url, username, password)) {

            System.out.print("Delivery Name: ");
            String name = sc.nextLine();

            System.out.print("Phone: ");
            String phone = sc.nextLine();

            System.out.print("Vehicle No: ");
            String vehicle = sc.nextLine();

            PreparedStatement ps = con.prepareStatement(
                    "insert into delivery_person(delivery_name,phone,vehicle_no) values(?,?,?)");

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, vehicle);

            ps.executeUpdate();
            System.out.println("Delivery Person Inserted!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ---------------- INSERT ORDER (TRANSACTION SAFE) ----------------

    static void insertOrder() {

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, username, password);
            con.setAutoCommit(false);

            System.out.print("Customer ID: ");
            int customerId = sc.nextInt();

            System.out.print("Delivery Person ID: ");
            int deliveryId = sc.nextInt();

            System.out.print("Food ID: ");
            int foodId = sc.nextInt();

            System.out.print("Quantity: ");
            int qty = sc.nextInt();

            PreparedStatement ps1 = con.prepareStatement(
                    "select price from food_item where food_id=?");
            ps1.setInt(1, foodId);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("Food Not Found!");
                con.rollback();
                return;
            }

            double price = rs.getDouble("price");
            double total = price * qty;

            PreparedStatement ps2 = con.prepareStatement(
                    "insert into orders(order_date,status,total_amount,customer_id,delivery_person_id) values(now(),'ordered',?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps2.setDouble(1, total);
            ps2.setInt(2, customerId);
            ps2.setInt(3, deliveryId);
            ps2.executeUpdate();

            ResultSet keys = ps2.getGeneratedKeys();
            keys.next();
            int orderId = keys.getInt(1);

            PreparedStatement ps3 = con.prepareStatement(
                    "insert into order_item(order_id,food_id,quantity,subtotal) values(?,?,?,?)");

            ps3.setInt(1, orderId);
            ps3.setInt(2, foodId);
            ps3.setInt(3, qty);
            ps3.setDouble(4, total);
            ps3.executeUpdate();

            con.commit();
            System.out.println("Order Placed Successfully!");

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                System.out.println(ex);
            }
            System.out.println("Transaction Failed!");
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    // ---------------- VIEW ALL TABLES ----------------

    static void viewAll() {
        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement st = con.createStatement()) {

            ResultSet rs;

            System.out.println("\n--- Customers ---");
            rs = st.executeQuery("select * from customer");
            while (rs.next())
                System.out.println(rs.getInt("customer_id") + " | " + rs.getString("customer_name"));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}