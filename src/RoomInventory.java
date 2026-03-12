import java.util.HashMap;
import java.util.Map;

public class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single", 10);
        inventory.put("Double", 7);
        inventory.put("Suite", 3);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }

    public boolean bookRoom(String roomType) {
        int available = getAvailability(roomType);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        } else {
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("Current Room Inventory:");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}