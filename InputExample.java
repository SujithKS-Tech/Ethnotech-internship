import java.util.Scanner;
class InputExample{
    public static void main(String [] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("enter your name:");
        String name=sc.nextLine();
        System.out.println("enter your age:");
        int age=sc.nextInt();
        System.out.println("name: "+ name);
        System.out.println("age: "+ age);
    }
}