import java.util.Scanner;

public class LoginValidation {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String correctUsername = "admin";
        int correctPassword = 1234;

        int attempts = 3;

        while (attempts > 0) {

            System.out.print("Enter Username: ");
            String username = sc.nextLine();

            System.out.print("Enter Password: ");
            int password = sc.nextInt();
            sc.nextLine();   

            if (username.equals(correctUsername) && password == correctPassword) {
                System.out.println("Login Successful!");
                break;
            } else {
                attempts--;
                System.out.println("Invalid username or password.");
                
                if (attempts > 0) {
                    System.out.println("You have " + attempts + " attempt(s) left.\n");
                } else {
                    System.out.println("Your account is blocked.");
                }
            }
        }

        sc.close();
    }
}
