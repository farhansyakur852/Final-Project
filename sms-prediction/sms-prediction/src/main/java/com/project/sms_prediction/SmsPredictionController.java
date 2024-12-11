package com.project.sms_prediction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SmsPredictionController {

    private final String API_URL = "http://127.0.0.1:8000/predict/";

    @GetMapping("/")
    public String index() {
        return "index";  // Menampilkan halaman input
    }

    @PostMapping("/predict/")
    public String predict(@RequestParam String text, Model model) {
        // Kirimkan permintaan POST ke API FastAPI
        RestTemplate restTemplate = new RestTemplate();

        // Siapkan request body dalam format yang benar
        String requestJson = "{\"text\": \"" + text + "\"}";

        // Siapkan headers dengan Content-Type application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Buat HttpEntity dengan body dan headers
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // Dapatkan respons dari API
        String response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class).getBody();

        try {
            // Parsing JSON untuk mendapatkan nilai dari 'prediction'
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            String prediction = jsonResponse.get("prediction").asText();

            // Menambahkan hasil prediksi ke model dan mengarahkan ke halaman hasil
            model.addAttribute("prediction", prediction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "result";  // Menampilkan halaman hasil prediksi
    }
}
