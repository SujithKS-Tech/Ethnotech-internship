import java.util.*;

// Custom Exception
class TaxiNotAvailableException extends Exception {
    TaxiNotAvailableException(String msg) {
        super(msg);
    }
}

// Booking Class
class Booking {
    int CustomerId, BookingId;
    int DropTime, PickUpTime;
    char From, To;
    int Amount;
}

// Taxi Details Class
class Taxidetails {
    int TaxiId;
    char CurrentLocation;
    int FreeTime;
    int Earnings;
    List<Booking> Bookings;

    Taxidetails(int id) {
        this.TaxiId = id;
        this.CurrentLocation = 'A';
        this.FreeTime = 0;
        this.Earnings = 0;
        this.Bookings = new ArrayList<>();
    }
}

// Main Class
public class TaxiBooking {

    static List<Taxidetails> taxis = new ArrayList<>();
    static int bookingCounter = 1;

    // Distance Calculation
    static int calculateDistance(char from, char to) {
        return Math.abs(from - to) * 15;
    }

    // Fare Calculation
    static int calculateEarning(int distance) {
        if (distance <= 5)
            return 100;
        return 100 + (distance - 5) * 10;
    }

    // Travel Time Calculation
    static int calculateTravelTime(char from, char to) {
        return Math.abs(from - to);
    }

    // Booking Logic
    static Taxidetails bookTaxi(int customerId, char from, char to, int pickUpTime)
            throws TaxiNotAvailableException {

        Taxidetails allocatedTaxi = null;
        int minDistance = Integer.MAX_VALUE;

        for (Taxidetails taxi : taxis) {

            if (taxi.FreeTime <= pickUpTime) {

                int distance = Math.abs(taxi.CurrentLocation - from);

                if (distance < minDistance) {
                    minDistance = distance;
                    allocatedTaxi = taxi;
                }
            }
        }

        if (allocatedTaxi == null) {
            throw new TaxiNotAvailableException("Booking Rejected. No Taxi available");
        }

        Booking booking = new Booking();
        booking.BookingId = bookingCounter++;
        booking.CustomerId = customerId;
        booking.From = from;
        booking.To = to;
        booking.PickUpTime = pickUpTime;

        int travelTime = calculateTravelTime(from, to);
        booking.DropTime = pickUpTime + travelTime;

        int totalDistance = calculateDistance(from, to);
        int amount = calculateEarning(totalDistance);
        booking.Amount = amount;

        allocatedTaxi.Bookings.add(booking);
        allocatedTaxi.Earnings += amount;
        allocatedTaxi.CurrentLocation = to;
        allocatedTaxi.FreeTime = booking.DropTime;

        return allocatedTaxi;
    }

    // Display Taxi Details
    static void displayTaxiDetails() {

        for (Taxidetails taxi : taxis) {

            System.out.println("Taxi " + taxi.TaxiId + " Total Earnings: Rs." + taxi.Earnings);

            for (Booking booking : taxi.Bookings) {

                System.out.println(
                        "Booking ID: " + booking.BookingId +
                        " | Customer ID: " + booking.CustomerId +
                        " | From: " + booking.From +
                        " | To: " + booking.To +
                        " | Pickup: " + booking.PickUpTime +
                        " | Drop: " + booking.DropTime +
                        " | Amount: " + booking.Amount
                );
            }

            System.out.println();
        }
    }

    // Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Creating taxis
        for (int i = 1; i <= 2; i++) {
            taxis.add(new Taxidetails(i));
        }

        while (true) {

            System.out.println("\n1. Book Taxi");
            System.out.println("2. Display Details");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();

            if (choice == 1) {

                try {

                    System.out.print("Customer ID: ");
                    int customerId = sc.nextInt();

                    System.out.print("Pickup Point: ");
                    char from = sc.next().charAt(0);

                    System.out.print("Drop Point: ");
                    char to = sc.next().charAt(0);

                    System.out.print("Pickup Time: ");
                    int pickUpTime = sc.nextInt();

                    Taxidetails taxi = bookTaxi(customerId, from, to, pickUpTime);

                    System.out.println("Taxi-" + taxi.TaxiId + " Allocated Successfully");

                } catch (Exception e) {

                    System.out.println(e.getMessage());

                }

            } else if (choice == 2) {

                displayTaxiDetails();

            } else {

                System.out.println("Thank You");
                break;
            }
        }

        sc.close();
    }
}
