import java.util.*;
import java.util.concurrent.*;

/**
 * Represents the Shared Inventory.
 * This is the "Shared Mutable State" that needs protection.
 */
class ThreadSafeInventory {
    private int deluxeRooms = 2; // Only 2 rooms available
    private final Set<String> confirmedGuests = Collections.synchronizedSet(new HashSet<>());

    // Requirement: Ensure inventory updates are performed in a thread-safe manner
    public synchronized boolean allocateRoom(String guestName) {
        System.out.println("[THREAD " + Thread.currentThread().getId() + "] Checking for: " + guestName);

        // CRITICAL SECTION START
        if (deluxeRooms > 0) {
            // Simulate a small delay to expose potential race conditions
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            deluxeRooms--;
            confirmedGuests.add(guestName);
            System.out.println("SUCCESS: Room allocated to " + guestName + ". Rooms left: " + deluxeRooms);
            return true;
        }
        // CRITICAL SECTION END

        System.out.println("FAILURE: No rooms left for " + guestName);
        return false;
    }

    public void printFinalState() {
        System.out.println("\n--- FINAL SYSTEM STATE ---");
        System.out.println("Remaining Rooms: " + deluxeRooms);
        System.out.println("Confirmed Guests: " + confirmedGuests);
    }
}

public class ConcurrencySimulation {
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeInventory inventory = new ThreadSafeInventory();

        // Requirement: Simulate multiple booking requests at the same time
        // Using a thread pool to run 5 guests simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(5);
        String[] guests = {"Alice", "Bob", "Charlie", "Diana", "Edward"};

        System.out.println("--- Starting Concurrent Booking (2 Rooms available for 5 Guests) ---");

        for (String guest : guests) {
            executor.submit(() -> {
                inventory.allocateRoom(guest);
            });
        }

        // Shut down and wait for all threads to finish
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Requirement: Maintain consistent system state
        inventory.printFinalState();
    }
}