package com.example.leetcode.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/leetcode")
public class LeetcodeEntryController {

    private final String GRAPHQL_API_URL = "https://leetcode.com/graphql";

    @Autowired
    private RestTemplate restTemplate;

    // Endpoint to fetch user's solved questions and ranking
    @GetMapping("/user/{username}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable String username) {
        String query = "query getUserProfile($username: String!) {\n" +
                "  matchedUser(username: $username) {\n" +
                "    profile {\n" +
                "      ranking\n" +
                "    }\n" +
                "    submitStats {\n" +
                "      acSubmissionNum {\n" +
                "        difficulty\n" +
                "        count\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, Object> requestPayload = Map.of(
                "query", query,
                "variables", Map.of("username", username)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GRAPHQL_API_URL,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                Map<String, Object> matchedUser = (Map<String, Object>) data.get("matchedUser");

                if (matchedUser != null) {
                    Map<String, Object> profile = (Map<String, Object>) matchedUser.get("profile");
                    Map<String, Object> submitStats = (Map<String, Object>) matchedUser.get("submitStats");

                    if (profile != null && submitStats != null) {
                        Object ranking = profile.get("ranking");
                        Object acSubmissionNum = submitStats.get("acSubmissionNum");

                        return ResponseEntity.ok(Map.of(
                                "totalSolved", acSubmissionNum,
                                "ranking", ranking
                        ));
                    }
                }
                return ResponseEntity.badRequest().body("Invalid username or incomplete data.");
            } else {
                return ResponseEntity.badRequest().body("No data found for the username.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching data: " + e.getMessage());
        }
    }
}
