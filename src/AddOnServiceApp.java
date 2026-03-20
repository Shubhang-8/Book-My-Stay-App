import java.util.*;

/**
 * Represents an individual optional offering.
 */
class Service {
    private final String name;
    private final double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}

/**
 * Manages the association between reservations and selected services.
 * Uses Composition over Inheritance to keep core logic clean.
 */
class AddOnServiceManager {
    // Key: Reservation ID, Value: List of selected services
    private final Map<String, List<Service>> reservationServices = new HashMap<>();

    // Requirement: Allow multiple services to be attached to a single reservation
    public void addServiceToReservation(String resId, Service service) {
        reservationServices.computeIfAbsent(resId, k -> new ArrayList<>()).add(service);
        System.out.println("ADD-ON: Added " + service.getName() + " to Reservation " + resId);
    }

    // Requirement: Calculate total additional cost
    public double calculateTotalAddOnCost(String resId) {
        List<Service> services = reservationServices.getOrDefault(resId, Collections.emptyList());
        return services.stream().mapToDouble(Service::getPrice).sum();
    }

    public List<Service> getSelectedServices(String resId) {
        return reservationServices.getOrDefault(resId, Collections.emptyList());
    }
}

public class AddOnServiceApp {
    public static void main(String[] args) {
        // 1. Setup our "Database" of available services
        Service breakfast = new Service("Buffet Breakfast", 25.0);
        Service spa = new Service("Spa Treatment", 80.0);
        Service wifi = new Service("High-Speed WiFi", 10.0);

        // 2. Setup the Manager
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // 3. Simulate a Guest with Reservation ID "RES-101" selecting services
        String resId = "RES-101";
        System.out.println("--- Guest Selecting Add-Ons for " + resId + " ---");

        addOnManager.addServiceToReservation(resId, breakfast);
        addOnManager.addServiceToReservation(resId, wifi);
        addOnManager.addServiceToReservation(resId, spa);

        // 4. Requirement: Cost Aggregation
        double totalExtra = addOnManager.calculateTotalAddOnCost(resId);

        System.out.println("\n--- Summary for Reservation " + resId + " ---");
        System.out.println("Services Selected: " + addOnManager.getSelectedServices(resId));
        System.out.printf("Total Additional Cost: $%.2f%n", totalExtra);

        // 5. Verification: Logic is decoupled
        System.out.println("\nNote: Core Room Inventory remains untouched during this process.");
    }
}