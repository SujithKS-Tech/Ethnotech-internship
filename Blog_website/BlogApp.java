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

public class BlogApp {

    static ArrayList<Blog> list = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static int id = 1;

    public static void main(String[] args) {

        int choice = 0;

        while (choice != 7) {

            System.out.println("\n----- BLOG MENU -----");
            System.out.println("1. Add Blog (Admin)");
            System.out.println("2. Delete Blog (Admin)");
            System.out.println("3. View All Blogs");
            System.out.println("4. View Blog by ID");
            System.out.println("5. Search Blog by Title");
            System.out.println("6. Filter by Category");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    addBlog();
                    break;

                case 2:
                    deleteBlog();
                    break;

                case 3:
                    viewAll();
                    break;

                case 4:
                    viewById();
                    break;

                case 5:
                    searchTitle();
                    break;

                case 6:
                    filterCategory();
                    break;

                case 7:
                    System.out.println("Thank You");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    // Add Blog
    static void addBlog() {

        sc.nextLine();

        System.out.print("Enter Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Content: ");
        String content = sc.nextLine();

        System.out.print("Enter Author: ");
        String author = sc.nextLine();

        System.out.print("Enter Category: ");
        String category = sc.nextLine();

        String date = java.time.LocalDate.now().toString();

        Blog b = new Blog(id, title, content, author, category, date);
        list.add(b);

        id++;

        System.out.println("Blog Added Successfully");
    }

    // Delete Blog
    static void deleteBlog() {

        System.out.print("Enter Blog ID to Delete: ");
        int blogId = sc.nextInt();

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).id == blogId) {
                list.remove(i);
                found = true;
                System.out.println("Blog Deleted");
                break;
            }
        }

        if (!found) {
            System.out.println("Blog Not Found");
        }
    }

    // View All Blogs
    static void viewAll() {

        if (list.size() == 0) {
            System.out.println("No Blogs Available");
            return;
        }

        for (Blog b : list) {
            System.out.println("\nID: " + b.id);
            System.out.println("Title: " + b.title);
            System.out.println("Author: " + b.author);
            System.out.println("Category: " + b.category);
            System.out.println("Date: " + b.date);
            System.out.println("---------------------");
        }
    }

    // View by ID
    static void viewById() {

        System.out.print("Enter Blog ID: ");
        int blogId = sc.nextInt();

        boolean found = false;

        for (Blog b : list) {

            if (b.id == blogId) {

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
    }

    // Search by Title
    static void searchTitle() {

        sc.nextLine();
        System.out.print("Enter Title to Search: ");
        String search = sc.nextLine();

        boolean found = false;

        for (Blog b : list) {

            if (b.title.toLowerCase().contains(search.toLowerCase())) {

                System.out.println("\nID: " + b.id);
                System.out.println("Title: " + b.title);
                System.out.println("Author: " + b.author);

                found = true;
            }
        }

        if (!found) {
            System.out.println("No Matching Blog Found");
        }
    }

    // Filter by Category
    static void filterCategory() {

        sc.nextLine();
        System.out.print("Enter Category: ");
        String cat = sc.nextLine();

        boolean found = false;

        for (Blog b : list) {

            if (b.category.equalsIgnoreCase(cat)) {

                System.out.println("\nID: " + b.id);
                System.out.println("Title: " + b.title);
                System.out.println("Author: " + b.author);

                found = true;
            }
        }

        if (!found) {
            System.out.println("No Blogs in this Category");
        }
    }
}