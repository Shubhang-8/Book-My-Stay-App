import java.io.*;
import java.util.*;

/**
 * The SystemState class acts as a "Snapshot" of the entire application.
 * Requirement: Must be Serializable to be written to a file.
 */
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures version compatibility
    public Map<String, Integer> inventory;
    public List<String> bookingHistory;

    public SystemState(Map<String, Integer> inventory, List<String> bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }
}

class PersistenceService {
    private static final String FILE_NAME = "booking_system.ser";

    // Requirement: Serialize data and write to a file
    public void saveState(Map<String, Integer> inventory, List<String> history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            SystemState snapshot = new SystemState(inventory, history);
            oos.writeObject(snapshot);
            System.out.println("PERSISTENCE: System state saved successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("SAVE_ERROR: Could not persist data. " + e.getMessage());
        }
    }

    // Requirement: Restore persisted data during startup
    public SystemState loadState() {
        File file = new File(FILE_NAME);

        // Requirement: Handle missing files gracefully (Failure Tolerance)
        if (!file.exists()) {
            System.out.println("RECOVERY: No save file found. Starting with fresh state.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("RECOVERY: Loading data from " + FILE_NAME + "...");
            return (SystemState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("RECOVERY_ERROR: Save file corrupted. Starting fresh.");
            return null;
        }
    }
}

public class PersistenceApp {
    public static void main(String[] args) {
        PersistenceService persistence = new PersistenceService();

        // --- STEP 1: RECOVERY (Startup) ---
        SystemState recoveredData = persistence.loadState();

        Map<String, Integer> inventory = (recoveredData != null) ? recoveredData.inventory : new HashMap<>();
        List<String> history = (recoveredData != null) ? recoveredData.bookingHistory : new ArrayList<>();

        if (recoveredData == null) {
            // Initializing fresh data if no save was found
            inventory.put("DELUXE", 10);
            history.add("System Initialized at " + new Date());
        }

        // --- STEP 2: OPERATION (Simulate a booking) ---
        System.out.println("Current Inventory: " + inventory);
        System.out.println("Current History Size: " + history.size());

        System.out.println("\n--- Processing new booking ---");
        inventory.put("DELUXE", inventory.get("DELUXE") - 1);
        history.add("Booking for Guest_" + UUID.randomUUID().toString().substring(0, 4));

        // --- STEP 3: PERSISTENCE (Shutdown) ---
        persistence.saveState(inventory, history);
        System.out.println("System shutting down. Run the program again to see data survive!");
    }
}