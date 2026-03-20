import java.util.*;

/**
 * Custom Exception for Domain-Specific Errors.
 */
class BookingValidationException extends Exception {
    public BookingValidationException(String message) {
        super(message);
    }
}

/**
 * Validates state and input before any inventory mutation occurs.
 */
class BookingValidator {
    private final Set<String> validRoomTypes = Set.of("DELUXE", "SUITE", "SINGLE");

    public void validateRequest(String roomType, int availableCount) throws BookingValidationException {
        // Requirement: Validate room types
        if (!validRoomTypes.contains(roomType.toUpperCase())) {
            throw new BookingValidationException("INVALID_ROOM_TYPE: '" + roomType + "' is not offered at this hotel.");
        }

        // Requirement: Prevent negative inventory
        if (availableCount <= 0) {
            throw new BookingValidationException("OUT_OF_STOCK: No available rooms for type '" + roomType + "'.");
        }
    }
}

/**
 * Enhanced Booking Service with Error Handling.
 */
class SecureBookingService {
    private final Map<String, Integer> inventory = new HashMap<>();
    private final BookingValidator validator = new BookingValidator();

    public SecureBookingService() {
        inventory.put("DELUXE", 1); // Only one room for testing
        inventory.put("SUITE", 10);
    }

    public void processBooking(String guest, String roomType) {
        try {
            System.out.println("\nPROCESSING: Request from " + guest + " for " + roomType);

            // Fail-Fast: Validate before doing anything else
            int currentStock = inventory.getOrDefault(roomType.toUpperCase(), 0);
            validator.validateRequest(roomType, currentStock);

            // If we reach here, validation passed
            inventory.put(roomType.toUpperCase(), currentStock - 1);
            System.out.println("SUCCESS: Room allocated for " + guest);

        } catch (BookingValidationException e) {
            // Requirement: Display clear failure messages
            System.err.println("VALIDATION_ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("SYSTEM_ERROR: An unexpected error occurred.");
        } finally {
            // Requirement: Ensure system remains stable
            System.out.println("STATUS: System ready for next request.");
        }
    }
}

public class ValidationApp {
    public static void main(String[] args) {
        SecureBookingService service = new SecureBookingService();

        // Scenario 1: Invalid Room Type
        service.processBooking("Alice", "PRESIDENTIAL_SUITE");

        // Scenario 2: Valid Request (Consumes the last Deluxe room)
        service.processBooking("Bob", "DELUXE");

        // Scenario 3: Out of Stock (Fail-Fast on second Deluxe request)
        service.processBooking("Charlie", "DELUXE");

        // Scenario 4: Valid Request
        service.processBooking("Diana", "SUITE");
    }
}