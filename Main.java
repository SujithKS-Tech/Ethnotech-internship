class Employee {
    private String name;
    Employee(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
public class Main  extends Employee{
    public static void main(String[] args) {
        Salary emp = new Salary("Sujith", 30000);
        System.out.println("Employee Name: " + emp.getName());
        System.out.println("Salary is: " + emp.getSalary());
    }
}
