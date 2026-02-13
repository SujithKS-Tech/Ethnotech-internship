import java.util.Scanner;

public class Simple_calculator {
    public static void main(String[] args) {
        System.out.println("welcome to calculator application");
        System.out.println("enter your choice of operation .......");
        System.out.println("1...addition\n" + "2...substraction");
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();

        System.out.println("enter first number");
        double a = s.nextDouble();
        System.out.println("Enter second number");
        double b = s.nextDouble();

        switch (choice) {
            case 1:
                System.out.println(addition(a, b));
                break;
            case 2:
                System.out.println(substraction(a, b));
                break;

            default:
                System.out.println("invalid choice");
        }
        s.close();
    }

    static double addition(double a, double b) {
        return a + b;
    }

    static double substraction(double a, double b) {
        return a - b;
    }

}