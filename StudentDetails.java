import java.util.Scanner;

public class StudentDetails {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String[] name = new String[5];
        int[] marks = new int[5];

        for (int i = 0; i <= 5; i++) {
            System.out.print("Enter student name: ");
            name[i] = sc.nextLine();

            System.out.print("Enter marks: ");
            marks[i] = sc.nextInt();
            sc.nextLine();  
        }

        System.out.println("\nStudent Details:");
        System.out.println("----------------------------");

        for (int i = 0; i < 5; i++) {

            char grade;

            if (marks[i] >= 90)
                grade = 'A';
            else if (marks[i] >= 75)
                grade = 'B';
            else if (marks[i] >= 50)
                grade = 'C';
            else
                grade = 'F';

            System.out.println("Name: " + name[i]);
            System.out.println("Marks: " + marks[i]);
            System.out.println("Grade: " + grade);
            System.out.println("----------------------------");
        }

        sc.close();
    }
}
