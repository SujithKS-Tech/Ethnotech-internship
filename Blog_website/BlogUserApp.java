import java.util.ArrayList;
import java.util.Scanner;

class Blog {

    int id;
    String title;
    String content;
    String author;
    String category;
    String date;

    Blog(int id, String title, String content,
         String author, String category, String date) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.date = date;
    }
}

public class BlogUserApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Blog> list = new ArrayList<>();

        list.add(new Blog(1, "Java Basics",
                "Java is a programming language.",
                "Sujith", "Education", "2026-02-23"));

        list.add(new Blog(2, "Cricket News",
                "India won the match.",
                "Rahul", "Sports", "2026-02-20"));

        list.add(new Blog(3, "AI Future",
                "Artificial Intelligence is growing fast.",
                "Meena", "Technology", "2026-02-18"));

        int choice = 0;

        while (choice != 5) {

            System.out.println("\n----- USER BLOG MENU -----");
            System.out.println("1. View All Blogs");
            System.out.println("2. View Blog by ID");
            System.out.println("3. Search Blog by Title");
            System.out.println("4. Filter by Category");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    for (Blog b : list) {
                        System.out.println("\nID: " + b.id);
                        System.out.println("Title: " + b.title);
                        System.out.println("Author: " + b.author);
                        System.out.println("Category: " + b.category);
                        System.out.println("Date: " + b.date);
                        System.out.println("----------------------");
                    }
                    break;

                case 2:
                    System.out.print("Enter Blog ID: ");
                    int id = sc.nextInt();
                    boolean found = false;

                    for (Blog b : list) {
                        if (b.id == id) {
                            System.out.println("\nTitle: " + b.title);
                            System.out.println("Author: " + b.author);
                            System.out.println("Category: " + b.category);
                            System.out.println("Date: " + b.date);
                            System.out.println("Content: " + b.content);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println("Blog Not Found");
                    }
                    break;

                case 3:
                    sc.nextLine();
                    System.out.print("Enter Title to Search: ");
                    String search = sc.nextLine();
                    boolean titleFound = false;

                    for (Blog b : list) {
                        if (b.title.toLowerCase().contains(search.toLowerCase())) {
                            System.out.println("\nID: " + b.id);
                            System.out.println("Title: " + b.title);
                            System.out.println("Author: " + b.author);
                            titleFound = true;
                        }
                    }

                    if (!titleFound) {
                        System.out.println("No Matching Blog Found");
                    }
                    break;

                case 4:
                    sc.nextLine();
                    System.out.print("Enter Category: ");
                    String cat = sc.nextLine();
                    boolean catFound = false;

                    for (Blog b : list) {
                        if (b.category.equalsIgnoreCase(cat)) {
                            System.out.println("\nID: " + b.id);
                            System.out.println("Title: " + b.title);
                            System.out.println("Author: " + b.author);
                            catFound = true;
                        }
                    }

                    if (!catFound) {
                        System.out.println("No Blogs in this Category");
                    }
                    break;

                case 5:
                    System.out.println("Thank You...");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }
        }
    }
}