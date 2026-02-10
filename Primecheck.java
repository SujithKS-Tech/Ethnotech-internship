public class Primecheck {

    public static void main(String[] args) {

        int num1 = 19;
        int num2 = 49;

        checkPrime(num1);
        checkPrime(num2);
    }

    public static void checkPrime(int num) {
        int count = 0;

        if (num <= 1) {
            System.out.println(num + " is Not Prime");
            return;
        }

        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) {
                count++;
                break;
            }
        }

        if (count == 0)
            System.out.println(num + " is Prime");
        else
            System.out.println(num + " is Not Prime");
    }
}