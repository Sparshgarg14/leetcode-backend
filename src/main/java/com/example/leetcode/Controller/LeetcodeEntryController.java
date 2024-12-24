package com.example.leetcode.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/leetcode")
public class LeetcodeEntryController {

    private final String BASE_API_URL = "https://leetcode-stats-api.herokuapp.com/";

    @Autowired
    private RestTemplate restTemplate;

    // Endpoint to fetch user's solved questions and ranking
    @GetMapping("/user/{username}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable String username) {
        String url = BASE_API_URL + username;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                Object totalSolved = responseBody.get("totalSolved");
                Object ranking = responseBody.get("ranking");

                if (totalSolved != null && ranking != null) {
                    return ResponseEntity.ok(Map.of(
                            "totalSolved", totalSolved,
                            "ranking", ranking
                    ));
                } else {
                    return ResponseEntity.badRequest().body("Invalid username or incomplete data.");
                }
            } else {
                return ResponseEntity.badRequest().body("No data found for the username.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching data: " + e.getMessage());
        }
    }
}
