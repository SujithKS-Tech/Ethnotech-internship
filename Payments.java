import java.util.Scanner;

interface pays {

    void pay();

    Scanner sc = new Scanner(System.in);
}

class Upi implements pays {

    public void pay() {
        System.out.println("enter amount to be paid...");
        double amount = sc.nextDouble();
        System.out.println("amount " + amount + " rupees paid through UPI Successfully...");
    }
}

class card implements pays {
    public void pay() {
        System.out.println("enter amount to be paid...");
        double amount = sc.nextDouble();
        System.out.println("amount " + amount + " rupees paid through Card Successfully...");
    }
}

class cash implements pays {
    public void pay() {
        System.out.println("enter amount to be paid...");
        double amount = sc.nextDouble();
        System.out.println("amount " + amount + " rupees paid through Cash Successfully...");
    }
}

public class Payments {
    public static void main(String[] args) {
        Upi u = new Upi();
        u.pay();

        card c = new card();
        c.pay();

        cash cs = new cash();
        cs.pay();
    }

}