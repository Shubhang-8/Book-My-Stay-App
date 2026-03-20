import java.util.*;

/**
 * Custom Exception for Cancellation Errors.
 */
class CancellationException extends Exception {
    public CancellationException(String message) {
        super(message);
    }
}

/**
 * Handles the logic for reversing confirmed bookings.
 */
class CancellationService {
    private final Map<String, String> activeBookings; // ResID -> RoomType
    private final Map<String, Integer> inventory;
    private final Stack<String> releasedRoomIds = new Stack<>(); // LIFO Rollback Tracking

    public CancellationService(Map<String, String> activeBookings, Map<String, Integer> inventory) {
        this.activeBookings = activeBookings;
        this.inventory = inventory;
    }

    public void cancelBooking(String reservationId, String roomId) throws CancellationException {
        System.out.println("\nCANCELLATION REQUEST: ID " + reservationId);

        // 1. Requirement: Validate reservation existence
        if (!activeBookings.containsKey(reservationId)) {
            throw new CancellationException("FAILED: Reservation " + reservationId + " not found or already cancelled.");
        }

        // 2. Requirement: Identify room type for inventory restoration
        String roomType = activeBookings.get(reservationId);

        // 3. Requirement: LIFO Rollback Logic (Pushing to Stack)
        releasedRoomIds.push(roomId);
        System.out.println("ROLLBACK: Room ID " + roomId + " added to release stack.");

        // 4. Requirement: Restore inventory immediately
        int currentStock = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, currentStock + 1);

        // 5. Requirement: Update system state (Remove from active)
        activeBookings.remove(reservationId);

        System.out.println("SUCCESS: Inventory restored for " + roomType + ". Current Stock: " + (currentStock + 1));
    }

    public void showReleasedRooms() {
        System.out.println("Current Stack of Released Rooms (LIFO): " + releasedRoomIds);
    }
}

public class CancellationApp {
    public static void main(String[] args) {
        // Mocking current system state
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("DELUXE", 0); // Currently sold out

        Map<String, String> activeBookings = new HashMap<>();
        activeBookings.put("RES-001", "DELUXE");
        activeBookings.put("RES-002", "DELUXE");

        CancellationService cancelService = new CancellationService(activeBookings, inventory);

        try {
            // Valid Cancellation
            cancelService.cancelBooking("RES-002", "ROOM-102");

            // Valid Cancellation
            cancelService.cancelBooking("RES-001", "ROOM-101");

            // Requirement: Prevent cancellation of non-existent booking
            cancelService.cancelBooking("RES-999", "ROOM-999");

        } catch (CancellationException e) {
            System.err.println("ERROR: " + e.getMessage());
        }

        // Show the LIFO order of released rooms
        System.out.println("\n--- Final System Audit ---");
        cancelService.showReleasedRooms();
    }
}