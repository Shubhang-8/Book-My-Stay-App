import java.util.HashMap;
import java.util.Map;

class Room {

    private String type;
    private double price;
    private String amenities;

    public Room(String type, double price, String amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + amenities);
    }
}

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 0);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }
}

class SearchService {

    public void searchAvailableRooms(RoomInventory inventory, Map<String, Room> roomDetails) {

        System.out.println("Available Rooms:\n");

        for (Map.Entry<String, Room> entry : roomDetails.entrySet()) {

            String roomType = entry.getKey();
            Room room = entry.getValue();

            int available = inventory.getAvailability(roomType);

            if (available > 0) {
                room.displayDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("------------------------");
            }
        }
    }
}

public class RoomSearchApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        Map<String, Room> roomDetails = new HashMap<>();

        roomDetails.put("Single", new Room("Single", 1500, "WiFi, TV"));
        roomDetails.put("Double", new Room("Double", 2500, "WiFi, TV, AC"));
        roomDetails.put("Suite", new Room("Suite", 5000, "WiFi, TV, AC, Jacuzzi"));

        SearchService searchService = new SearchService();

        searchService.searchAvailableRooms(inventory, roomDetails);
    }
}