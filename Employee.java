class Emp {
    private double salary;   

    
    public Emp(double salary) {
        this.salary = salary;
    }

    
    public double getSalary() {
        return salary;
    }


    public void setSalary(double salary) {
        this.salary = salary;
    }
}

public class Employee extends Emp {

    
    public Employee(double salary) {
        super(salary);   
    }

    public static void main(String[] args) {
        Employee e = new Employee(100000);
        System.out.println("Salary is: " + e.getSalary());
    }
}
