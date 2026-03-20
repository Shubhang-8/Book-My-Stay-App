import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the final record of a booking for history/auditing.
 */
class BookingRecord {
    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final double totalCost;
    private final long confirmationTimestamp;

    public BookingRecord(String id, String name, String type, double cost) {
        this.reservationId = id;
        this.guestName = name;
        this.roomType = type;
        this.totalCost = cost;
        this.confirmationTimestamp = System.currentTimeMillis();
    }

    public String getRoomType() { return roomType; }
    public double getTotalCost() { return totalCost; }

    @Override
    public String toString() {
        return String.format("[%tT] ID: %s | Guest: %-7s | Type: %-7s | Total: $%.2f",
                confirmationTimestamp, reservationId, guestName, roomType, totalCost);
    }
}

/**
 * Manages the storage of confirmed bookings (Persistence Mindset).
 */
class BookingHistory {
    // Requirement: List used to maintain chronological insertion order
    private final List<BookingRecord> history = new ArrayList<>();

    public void recordBooking(BookingRecord record) {
        history.add(record);
    }

    // Returns a read-only view to ensure reporting doesn't modify data
    public List<BookingRecord> getAllRecords() {
        return Collections.unmodifiableList(history);
    }
}

/**
 * Logic for generating summaries from the history.
 */
class BookingReportService {
    public void generateSummary(BookingHistory history) {
        List<BookingRecord> records = history.getAllRecords();

        System.out.println("\n========== ADMINISTRATIVE REPORT ==========");
        System.out.println("Total Bookings Processed: " + records.size());

        double totalRevenue = records.stream()
                .mapToDouble(BookingRecord::getTotalCost)
                .sum();
        System.out.printf("Total Revenue Generated: $%.2f%n", totalRevenue);

        // Grouping by Room Type (Reporting logic)
        Map<String, Long> countByType = records.stream()
                .collect(Collectors.groupingBy(BookingRecord::getRoomType, Collectors.counting()));

        System.out.println("Bookings by Category: " + countByType);
        System.out.println("===========================================");
    }
}

public class ReportingApp {
    public static void main(String[] args) {
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // 1. Simulating the Flow: Confirmation -> History
        System.out.println("--- Recording Confirmed Bookings ---");
        history.recordBooking(new BookingRecord("RES-001", "Alice", "DELUXE", 225.0));
        history.recordBooking(new BookingRecord("RES-002", "Bob", "SUITE", 500.0));
        history.recordBooking(new BookingRecord("RES-003", "Charlie", "DELUXE", 225.0));

        // 2. Retrieval for Review
        System.out.println("\n--- Full Audit Trail (Insertion Order) ---");
        history.getAllRecords().forEach(System.out::println);

        // 3. Requirement: Generate Summary Reports
        reportService.generateSummary(history);
    }
}