class Laptop {
    int price = 0;
    String lapname = "a";
    String processor = "a";

    public static void main(String[] args) {
        Laptop l1 = new Laptop();

        l1.price = 50000;
        l1.processor = "i5";
        l1.lapname = "Lenovo";

        System.out.println("Price: " + l1.price);
        System.out.println("Processor: " + l1.processor);
        System.out.println("Name: " + l1.lapname);
    }
}
