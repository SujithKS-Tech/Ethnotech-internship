import java.util.Scanner;

class parent {
    double amount;
    Scanner sc = new Scanner(System.in);

    void upi_pay() {

    }

    void cash_pay() {

    }

    void card_pay() {

    }
}

public class Payment extends parent {
    @Override
    public void upi_pay() {
        System.out.println("enter amount to be paid...");
        amount = sc.nextDouble();
        System.out.println("you have paid " + amount + " through upi...");
    }

    @Override
    public void cash_pay() {
        System.out.println("enter amount to be paid...");
        amount = sc.nextDouble();
        System.out.println("you have paid " + amount + " through cash...");
    }

    @Override
    public void card_pay() {
        System.out.println("enter amount to be paid...");
        amount = sc.nextDouble();
        System.out.println("you have paid " + amount + " through card...");
    }

    public void show() {
        while (true) {
            System.out.println("enter choice of payment methods..");
            System.out.println("1.upi_pay");
            System.out.println("2.cash_pay...");
            System.out.println("3.card_pay");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    upi_pay();
                    break;
                case 2:
                    cash_pay();
                    break;
                case 3:
                    card_pay();
                    break;

                default:
                    System.out.println("invalid choice");
                    break;
            }

        }

    }

    public static void main(String[] args) {
        Payment p = new Payment();
        p.show();
    }

}