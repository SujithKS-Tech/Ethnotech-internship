import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {


    static class Person {
        String firstName, lastName, gender;
        int age, id;

        Person(String fn, String ln, int a, String g, int i) {
            firstName = fn;
            lastName = ln;
            age = a;
            gender = g;
            id = i;   
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

       
        System.out.print("Enter number of people: ");
        int n = Integer.parseInt(sc.nextLine());

        List<Person> list = new ArrayList<>();

        System.out.println("Enter details (FirstName LastName Age Gender):");

        for (int i = 0; i < n; i++) {

            String input = sc.nextLine();
            String[] data = input.split(" ");

            
            list.add(new Person(
                    data[0],
                    data[1],
                    Integer.parseInt(data[2]),
                    data[3],
                    i
            ));
        }

       
        list.sort((p1, p2) -> {
            if (p1.age != p2.age) {
                return p1.age - p2.age;
            }
            return p1.id - p2.id;
        });

   
        System.out.println("\n--- Sorted Directory ---");

        for (Person p : list) {

            String title;

            if (p.gender.equalsIgnoreCase("M")) {
                title = "Mr.";
            } else {
                title = "Ms.";
            }

            System.out.println(title + " " + p.firstName + " " + p.lastName);
        }

        sc.close();
    }
}