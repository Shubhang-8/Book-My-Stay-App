import java.util.*;
import java.util.concurrent.*;

/**
 * Represents the finalized booking with an assigned Room ID.
 */
class ConfirmedBooking {
    String guestName;
    String roomType;
    String assignedRoomId;

    public ConfirmedBooking(String guestName, String roomType, String assignedRoomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.assignedRoomId = assignedRoomId;
    }

    @Override
    public String toString() {
        return String.format("CONFIRMED: [Guest: %-7s | Type: %-8s | RoomID: %s]",
                guestName, roomType, assignedRoomId);
    }
}

/**
 * Manages room availability and prevents double-booking using Sets.
 */
class InventoryService {
    // Map: Room Type -> Set of Allocated Room IDs (Uniqueness Enforcement)
    private final Map<String, Set<String>> allocatedRooms = new ConcurrentHashMap<>();
    // Map: Room Type -> Current Available Count
    private final Map<String, Integer> inventoryCount = new ConcurrentHashMap<>();

    public InventoryService() {
        // Initialize with some dummy inventory
        inventoryCount.put("DELUXE", 2);
        inventoryCount.put("SUITE", 1);
        allocatedRooms.put("DELUXE", Collections.synchronizedSet(new HashSet<>()));
        allocatedRooms.put("SUITE", Collections.synchronizedSet(new HashSet<>()));
    }

    public synchronized ConfirmedBooking allocateRoom(String guestName, String roomType) {
        int available = inventoryCount.getOrDefault(roomType, 0);

        if (available > 0) {
            // Generate a unique Room ID (In real world, this might be "101A")
            String newRoomId = roomType + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            // UC6 Requirement: Use Set to prevent reuse/double-booking
            Set<String> assignedSet = allocatedRooms.get(roomType);

            if (assignedSet.add(newRoomId)) { // Returns true if unique
                // Atomic Logical Operation: Decrement inventory immediately
                inventoryCount.put(roomType, available - 1);
                return new ConfirmedBooking(guestName, roomType, newRoomId);
            }
        }
        return null; // No inventory or collision occurred
    }
}

public class BookingAllocationApp {
    public static void main(String[] args) {
        // 1. Setup the Queue (from UC5)
        Queue<String[]> requestQueue = new LinkedList<>();
        requestQueue.add(new String[]{"Alice", "DELUXE"});
        requestQueue.add(new String[]{"Bob", "DELUXE"});
        requestQueue.add(new String[]{"Charlie", "DELUXE"}); // This should fail (only 2 Deluxe)
        requestQueue.add(new String[]{"Diana", "SUITE"});

        // 2. Setup the Inventory Service (UC6)
        InventoryService inventoryService = new InventoryService();

        System.out.println("--- Processing Dequeued Requests ---");

        // 3. FIFO Processing Loop
        while (!requestQueue.isEmpty()) {
            String[] request = requestQueue.poll();
            String guest = request[0];
            String type = request[1];

            ConfirmedBooking result = inventoryService.allocateRoom(guest, type);

            if (result != null) {
                System.out.println(result);
            } else {
                System.out.println("REJECTED:  [Guest: " + guest + " | Reason: No " + type + " available]");
            }
        }
    }
}