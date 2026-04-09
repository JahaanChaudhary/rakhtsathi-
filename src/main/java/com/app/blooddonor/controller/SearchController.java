package com.app.blooddonor.controller;

import com.app.blooddonor.dto.DonorDTO;
import com.app.blooddonor.dto.DonorMapper;
import com.app.blooddonor.dto.SearchRequestDTO;
import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final DonorMapper   donorMapper;
    private final ObjectMapper  objectMapper;

    private static final List<String> BLOOD_GROUPS =
            List.of("A+","A-","B+","B-","O+","O-","AB+","AB-");

    // City center coordinates — used to compute donor distances
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("mumbai",    new double[]{ 19.0760,  72.8777 });
        CITY_COORDS.put("pune",      new double[]{ 18.5204,  73.8567 });
        CITY_COORDS.put("delhi",     new double[]{ 28.6139,  77.2090 });
        CITY_COORDS.put("bangalore", new double[]{ 12.9716,  77.5946 });
        CITY_COORDS.put("chennai",   new double[]{ 13.0827,  80.2707 });
        CITY_COORDS.put("hyderabad", new double[]{ 17.3850,  78.4867 });
        CITY_COORDS.put("kolkata",   new double[]{ 22.5726,  88.3639 });
        CITY_COORDS.put("nashik",    new double[]{ 19.9975,  73.7898 });
        CITY_COORDS.put("surat",     new double[]{ 21.1702,  72.8311 });
        CITY_COORDS.put("nagpur",    new double[]{ 21.1458,  79.0882 });
        CITY_COORDS.put("ahmedabad", new double[]{ 23.0225,  72.5714 });
        CITY_COORDS.put("jaipur",    new double[]{ 26.9124,  75.7873 });
    }

    @GetMapping("/search")
    public String searchPage(Model model) {
        model.addAttribute("bloodGroups", BLOOD_GROUPS);
        model.addAttribute("searchForm",  new SearchRequestDTO());
        return "search";
    }

    @PostMapping("/search/results")
    public String searchResults(
            @Valid @ModelAttribute("searchForm") SearchRequestDTO form,
            BindingResult result,
            Model model) {

        model.addAttribute("bloodGroups", BLOOD_GROUPS);
        if (result.hasErrors()) return "search";

        try {
            // 1. Find matching donors
            List<DonorProfile> donors = searchService.searchAndNotify(form);

            // 2. Convert to DTO
            List<DonorDTO> dtos = donors.stream()
                    .map(donorMapper::toDTO)
                    .toList();

            // 3. Get city center for distance calculation
            String cityKey = form.getCity().toLowerCase().trim();
            double[] cityCoords = CITY_COORDS.get(cityKey);

            // 4. Compute distance range for each donor and set it on the DTO.
            //    Donor coordinates are NEVER passed to the frontend — only the
            //    human-readable distance string like "Within 3 km" is sent.
            for (DonorDTO dto : dtos) {
                if (cityCoords != null
                        && dto.getLatitude() != null
                        && dto.getLongitude() != null) {

                    double km = haversineKm(
                            cityCoords[0], cityCoords[1],
                            dto.getLatitude(), dto.getLongitude()
                    );
                    dto.setDistanceRange(toDistanceLabel(km));

                } else {
                    // City not in our map or donor has no coordinates
                    dto.setDistanceRange("Same city");
                }

                // Clear coordinates so they are NOT included in the JSON
                // sent to the frontend — privacy protection
                dto.setLatitude(null);
                dto.setLongitude(null);
            }

            // 5. Serialize to JSON — no coordinates present anymore
            String donorsJson = objectMapper.writeValueAsString(dtos);

            model.addAttribute("donors",     dtos);
            model.addAttribute("donorsJson", donorsJson);
            model.addAttribute("count",      dtos.size());
            model.addAttribute("bloodGroup", form.getBloodGroup());
            model.addAttribute("city",       form.getCity());

            return "search-results";

        } catch (Exception e) {
            log.error("Search error", e);
            model.addAttribute("error", "Something went wrong: " + e.getMessage());
            return "search";
        }
    }

    // ── Haversine formula — straight-line distance between two lat/lng points ──
    // This is the standard formula used in geography and navigation.
    // Earth radius = 6371 km
    // Returns distance in kilometres.
    private double haversineKm(double lat1, double lng1,
                               double lat2, double lng2) {
        final double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ── Convert km distance to a human-readable range label ──────────────────
    // Buckets chosen to be useful without revealing exact location.
    private String toDistanceLabel(double km) {
        if      (km <= 2)  return "Within 2 km";
        else if (km <= 5)  return "Within 5 km";
        else if (km <= 10) return "Within 10 km";
        else if (km <= 20) return "Within 20 km";
        else               return "Same city";
    }
}
