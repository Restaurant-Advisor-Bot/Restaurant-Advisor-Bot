package ru.spbstu.restaurantadvisor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.spbstu.restaurantadvisor.client.PlacesApiException;

import java.util.List;

public class PhotonPlacesClientManualTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing PhotonPlacesClient (gratuit, sans clé) ===\n");
        
        ObjectMapper objectMapper = new ObjectMapper();
        PhotonPlacesClient client = new PhotonPlacesClient(objectMapper);
        
        // Moscow coordinates
        double lat = 55.751244;
        double lon = 37.618423;
        int radius = 2000; // 2km (as required for /random command)
        
        // Test 1: Search restaurants (no specific cuisine)
        System.out.println("--- Test 1: Search restaurants near Moscow ---");
        try {
            List<RestaurantDto> restaurants = client.searchNearby(lat, lon, radius, null);
            displayResults(restaurants);
        } catch (PlacesApiException e) {
            System.out.println("✗ Error: " + e.getMessage());
            System.out.println("  Error type: " + e.getErrorType());
        }
        
        // Test 2: Search pizza
        System.out.println("\n--- Test 2: Search pizza restaurants ---");
        try {
            List<RestaurantDto> restaurants = client.searchNearby(lat, lon, radius, "pizza");
            displayResults(restaurants);
        } catch (PlacesApiException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 3: Search Japanese
        System.out.println("\n--- Test 3: Search Japanese restaurants ---");
        try {
            List<RestaurantDto> restaurants = client.searchNearby(lat, lon, radius, "japanese");
            displayResults(restaurants);
        } catch (PlacesApiException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("\n=== All tests completed ===");
    }
    
    private static void displayResults(List<RestaurantDto> restaurants) {
        if (restaurants.isEmpty()) {
            System.out.println("  No results found.");
        } else {
            System.out.println("✓ Found " + restaurants.size() + " restaurants:");
            for (int i = 0; i < restaurants.size(); i++) {
                RestaurantDto r = restaurants.get(i);
                System.out.println((i + 1) + ". " + r.getName());
                System.out.println("   Cuisine: " + r.getCuisine());
                System.out.println("   Address: " + r.getAddress());
                System.out.println("   Coordinates: " + r.getLat() + ", " + r.getLon());
                System.out.println();
            }
        }
    }
}