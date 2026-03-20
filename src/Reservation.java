import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents a guest's intent to book.
 */
class Reservation {
    private final String guestName;
    private final String roomId;
    private final long timestamp;

    public Reservation(String guestName, String roomId) {
        this.guestName = guestName;
        this.roomId = roomId;
        this.timestamp = System.nanoTime(); // Used for demonstration
    }

    @Override
    public String toString() {
        return String.format("Reservation[Guest: %s, Room: %s]", guestName, roomId);
    }
}

/**
 * Manages the intake of booking requests.
 */
class BookingRequestQueue {
    // Standard FIFO Queue that is Thread-Safe
    private final Queue<Reservation> queue = new ConcurrentLinkedQueue<>();

    // Requirement: Accept booking requests and store in queue
    public void submitRequest(Reservation request) {
        queue.add(request);
        System.out.println("INTAKE: Added to queue -> " + request);
    }

    // Requirement: Prepare requests for processing (Peek/Poll)
    public void processAllRequests() {
        System.out.println("\n--- Starting Allocation Processing (FIFO Order) ---");
        while (!queue.isEmpty()) {
            Reservation nextRequest = queue.poll();
            System.out.println("PROCESSING: Handling " + nextRequest);
            // Allocation logic/Inventory updates would happen here in the next stage
        }
    }
}

public class BookingSystemApp {
    public static void main(String[] args) throws InterruptedException {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Simulating high demand with a Thread Pool (Multiple guests at once)
        ExecutorService demandSimulator = Executors.newFixedThreadPool(5);

        System.out.println("--- Peak Demand Started ---");

        // Guests submitting requests concurrently
        String[] guests = {"Alice", "Bob", "Charlie", "Diana", "Edward"};
        for (String guest : guests) {
            demandSimulator.submit(() -> {
                Reservation req = new Reservation(guest, "Room-101");
                bookingQueue.submitRequest(req);
            });
        }

        // Shutdown simulator and wait for all "intakes" to finish
        demandSimulator.shutdown();
        demandSimulator.awaitTermination(5, TimeUnit.SECONDS);

        // Requirement: Process requests in the order they arrived
        bookingQueue.processAllRequests();
    }
}