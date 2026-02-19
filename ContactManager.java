import java.util.*;

public class ContactManager {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        HashMap<String, String> contacts = new HashMap<>();

        int choice;

        do {

            System.out.println("\n1. Store Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Delete Contact");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            
            choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {

                if (contacts.size() < 10) {

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Phone: ");
                    String phone = sc.nextLine();

                    contacts.put(phone, name);
                    System.out.println("Contact Stored");

                } else {
                    System.out.println("Only 10 contacts allowed");
                }

            }

            else if (choice == 2) {

                if (contacts.isEmpty()) {
                    System.out.println("No Contacts");
                } else {

                    System.out.println("\n--- Contact List ---");

                    for (Map.Entry<String, String> entry : contacts.entrySet()) {

                        System.out.println("Name: " + entry.getValue()
                                + " | Phone: " + entry.getKey());
                    }
                }
            }

            else if (choice == 3) {

                System.out.print("Enter Phone number to delete: ");
                String phone = sc.nextLine();

                if (contacts.containsKey(phone)) {
                    contacts.remove(phone);
                    System.out.println("Contact Deleted");
                } else {
                    System.out.println("Contact Not Found");
                }
            }

            else if (choice == 4) {
                System.out.println("Program Ended");
            }

            else {
                System.out.println("Invalid Choice");
            }

        } while (choice != 4);

        sc.close();
    }
}
