import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

// Main class (entry point)
public class BookingSystemDemo {

    // Reservation class
    static class Reservation {
        private final String guestName;
        private final String roomType;
        private final LocalDateTime requestTime;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.requestTime = LocalDateTime.now();
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public LocalDateTime getRequestTime() {
            return requestTime;
        }

        @Override
        public String toString() {
            return "Reservation{" +
                    "guestName='" + guestName + '\'' +
                    ", roomType='" + roomType + '\'' +
                    ", requestTime=" + requestTime +
                    '}';
        }
    }

    // Booking Request Queue
    static class BookingRequestQueue {
        private final Queue<Reservation> requestQueue = new LinkedList<>();

        // Add request (intake only)
        public void submitRequest(Reservation reservation) {
            requestQueue.offer(reservation);
            System.out.println("Added to queue: " + reservation);
        }

        // Get next request (FIFO)
        public Reservation getNextRequest() {
            return requestQueue.poll();
        }

        public boolean isEmpty() {
            return requestQueue.isEmpty();
        }
    }

    // Main method
    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();

        // Simulating multiple booking requests
        queue.submitRequest(new Reservation("Alice", "Deluxe"));
        queue.submitRequest(new Reservation("Bob", "Standard"));
        queue.submitRequest(new Reservation("Charlie", "Suite"));

        System.out.println("\n--- Processing Requests in FIFO Order ---");

        // Processing queue
        while (!queue.isEmpty()) {
            Reservation reservation = queue.getNextRequest();
            System.out.println("Processing: " + reservation);
        }
    }
}