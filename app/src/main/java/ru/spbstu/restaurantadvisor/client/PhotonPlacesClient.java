package ru.spbstu.restaurantadvisor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.restaurantadvisor.client.PlacesApiException;

import java.util.stream.Collectors;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PhotonPlacesClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PhotonPlacesClient.class);
    
    // Photon API endpoint (gratuit, sans clé)
    private static final String PHOTON_API_URL = "https://photon.komoot.io/api/";
    
    // Requirements from specification
    private static final int TIMEOUT_SECONDS = 5;      // 5 seconds timeout
    private static final int RETRY_DELAY_MS = 2000;    // Retry after 2 seconds
    private static final int MAX_RESULTS = 5;          // Maximum 5 restaurants per search
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public PhotonPlacesClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
    
    /**
     * Search for nearby restaurants using Photon API (OpenStreetMap based)
     * 
     * @param lat Latitude
     * @param lon Longitude
     * @param radius Search radius in meters
     * @param keyword Cuisine type or keyword (can be null)
     * @return List of restaurants (max 5)
     * @throws PlacesApiException On error (timeout, etc.)
     */
    public List<RestaurantDto> searchNearby(double lat, double lon, int radius, String keyword) 
            throws PlacesApiException {
        
        logger.info("Searching restaurants near ({}, {}) with radius {}m, keyword: {}", lat, lon, radius, keyword);
        
        // Build search text
        String searchText = (keyword != null && !keyword.isBlank()) ? keyword : "restaurant";
        
        // Build URL for Photon (sans paramètre distance, on filtre après)
        String url = PHOTON_API_URL + "?" +
                "q=" + URLEncoder.encode(searchText, StandardCharsets.UTF_8) +
                "&lat=" + lat +
                "&lon=" + lon +
                "&limit=" + (MAX_RESULTS * 3) +  // On récupère plus pour filtrer après
                "&osm_tag=amenity:restaurant";
        
        logger.debug("Request URL: {}", url);
        
        // Send request with timeout 5s + retry after 2s
        String responseBody = tryRequestWithRetry(url);
        
        // Parse response et filtrer par distance
        return parseResponseWithDistanceFilter(responseBody, lat, lon, radius);
    }
    

    /**
     * Parse Photon response and filter by distance
     */
    private List<RestaurantDto> parseResponseWithDistanceFilter(String responseBody, double centerLat, double centerLon, int maxRadius) 
            throws PlacesApiException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            List<RestaurantDto> allRestaurants = new ArrayList<>();
            
            // Get features array (results)
            JsonNode features = root.path("features");
            if (!features.isArray()) {
                return allRestaurants;
            }
            
            for (JsonNode feature : features) {
                JsonNode properties = feature.path("properties");
                JsonNode geometry = feature.path("geometry");
                
                String name = properties.path("name").asText();
                if (name.isEmpty()) continue;
                
                // Get coordinates
                JsonNode coords = geometry.path("coordinates");
                if (!coords.isArray() || coords.size() < 2) continue;
                
                double placeLon = coords.get(0).asDouble();
                double placeLat = coords.get(1).asDouble();
                
                // Calculate distance (Haversine formula)
                double distance = calculateDistance(centerLat, centerLon, placeLat, placeLon);
                
                // Filter by radius
                if (distance > maxRadius) continue;
                
                RestaurantDto dto = new RestaurantDto();
                dto.setName(name);
                dto.setLat(placeLat);
                dto.setLon(placeLon);
                
                // Build address
                String street = properties.path("street").asText();
                String city = properties.path("city").asText();
                String housenumber = properties.path("housenumber").asText();
                
                StringBuilder address = new StringBuilder();
                if (!street.isEmpty()) {
                    address.append(street);
                    if (!housenumber.isEmpty()) {
                        address.append(" ").append(housenumber);
                    }
                }
                if (!city.isEmpty()) {
                    if (address.length() > 0) address.append(", ");
                    address.append(city);
                }
                dto.setAddress(address.length() > 0 ? address.toString() : "Address not available");
                
                // Get cuisine type
                String osmValue = properties.path("osm_value").asText();
                dto.setCuisine(!osmValue.isEmpty() ? osmValue : "Restaurant");
                
                allRestaurants.add(dto);
            }
            
            // Limit to MAX_RESULTS
            List<RestaurantDto> filtered = allRestaurants.stream()
                    .limit(MAX_RESULTS)
                    .collect(Collectors.toList());
            
            logger.info("Found {} restaurants within {}m (filtered from {})", filtered.size(), maxRadius, allRestaurants.size());
            return filtered;
            
        } catch (Exception e) {
            logger.error("Error parsing response: {}", e.getMessage());
            throw new PlacesApiException(PlacesApiException.ErrorType.UNKNOWN, 
                    "Error parsing results");
        }
    }

    /**
     * Calculate distance between two points using Haversine formula
     * Returns distance in meters
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth's radius in meters
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    /**
     * Send request with 5s timeout + 1 retry after 2 seconds
     * This matches the specification requirements exactly
     */
    private String tryRequestWithRetry(String url) throws PlacesApiException {
        // First attempt
        String result = sendRequest(url);
        if (result != null) {
            return result;
        }
        
        logger.warn("First attempt failed, retrying after {} ms...", RETRY_DELAY_MS);
        
        // Wait 2 seconds before retry (as specified)
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlacesApiException(PlacesApiException.ErrorType.TIMEOUT, 
                    "Request interrupted");
        }
        
        // Second attempt
        result = sendRequest(url);
        if (result != null) {
            return result;
        }
        
        // Both attempts failed -> error message from requirements
        throw new PlacesApiException(PlacesApiException.ErrorType.TIMEOUT, 
                "Превышено время ожидания. Попробуйте позже");
    }
    
    /**
     * Send single HTTP request with 5 second timeout
     */
    private String sendRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .header("User-Agent", "RestaurantAdvisorBot/1.0")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logger.debug("Request successful");
                return response.body();
            } else {
                logger.error("Photon API returned error: {} - {}", response.statusCode(), response.body());
                return null;
            }
            
        } catch (java.net.http.HttpTimeoutException e) {
            logger.warn("Timeout on request");
            return null;
        } catch (Exception e) {
            logger.error("Error sending request: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse Photon JSON response
     * Photon returns a GeoJSON object with a "features" array
     */
    private List<RestaurantDto> parseResponse(String responseBody) throws PlacesApiException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            List<RestaurantDto> restaurants = new ArrayList<>();
            
            // Get features array (results)
            JsonNode features = root.path("features");
            if (!features.isArray()) {
                logger.warn("No features array in response");
                return restaurants;
            }
            
            for (JsonNode feature : features) {
                JsonNode properties = feature.path("properties");
                JsonNode geometry = feature.path("geometry");
                
                String name = properties.path("name").asText();
                if (name.isEmpty()) continue;
                
                RestaurantDto dto = new RestaurantDto();
                dto.setName(name);
                
                // Build address from components
                String street = properties.path("street").asText();
                String housenumber = properties.path("housenumber").asText();
                String city = properties.path("city").asText();
                String country = properties.path("country").asText();
                
                StringBuilder address = new StringBuilder();
                if (!street.isEmpty()) {
                    address.append(street);
                    if (!housenumber.isEmpty()) {
                        address.append(" ").append(housenumber);
                    }
                }
                if (!city.isEmpty()) {
                    if (address.length() > 0) address.append(", ");
                    address.append(city);
                }
                if (!country.isEmpty() && address.length() == 0) {
                    address.append(country);
                }
                dto.setAddress(address.length() > 0 ? address.toString() : "Address not available");
                
                // Get cuisine type from OSM tags
                String osmValue = properties.path("osm_value").asText();
                String osmKey = properties.path("osm_key").asText();
                if ("cuisine".equals(osmKey) && !osmValue.isEmpty()) {
                    dto.setCuisine(osmValue);
                } else if (!osmValue.isEmpty()) {
                    dto.setCuisine(osmValue);
                } else {
                    dto.setCuisine("Restaurant");
                }
                
                // Coordinates
                JsonNode coords = geometry.path("coordinates");
                if (coords.isArray() && coords.size() >= 2) {
                    dto.setLon(coords.get(0).asDouble());
                    dto.setLat(coords.get(1).asDouble());
                }
                
                restaurants.add(dto);
                
                if (restaurants.size() >= MAX_RESULTS) break;
            }
            
            logger.info("Found {} restaurants (max: {})", restaurants.size(), MAX_RESULTS);
            return restaurants;
            
        } catch (Exception e) {
            logger.error("Error parsing response: {}", e.getMessage());
            throw new PlacesApiException(PlacesApiException.ErrorType.UNKNOWN, 
                    "Error parsing results");
        }
    }
}