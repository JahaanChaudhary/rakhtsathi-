package com.app.blooddonor.config;

import com.app.blooddonor.model.BloodRequest;
import com.app.blooddonor.model.DonorProfile;
import com.app.blooddonor.model.User;
import com.app.blooddonor.repository.BloodRequestRepository;
import com.app.blooddonor.repository.DonorProfileRepository;
import com.app.blooddonor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository         userRepo;
    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;
    private final BCryptPasswordEncoder  passwordEncoder;

    // City center coordinates for map pins
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("Mumbai",    new double[]{ 19.0760,  72.8777 });
        CITY_COORDS.put("Pune",      new double[]{ 18.5204,  73.8567 });
        CITY_COORDS.put("Delhi",     new double[]{ 28.6139,  77.2090 });
        CITY_COORDS.put("Bangalore", new double[]{ 12.9716,  77.5946 });
        CITY_COORDS.put("Chennai",   new double[]{ 13.0827,  80.2707 });
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() > 0) {
            System.out.println("✅ Data already exists — skipping seed.");
            return;
        }

        System.out.println("🌱 Seeding dummy data...");

        // Admin
        saveUser("Admin", "admin@rakhtsathi.com", "admin123", "ADMIN");

        // Mumbai donors
        saveDonor("Rahul Sharma",   "rahul@gmail.com",   "9876543210", "A+",  "Mumbai",    "Maharashtra", 5,  true,  "2024-01-15",  0.012,  0.018);
        saveDonor("Priya Patel",    "priya@gmail.com",   "9823456789", "B+",  "Mumbai",    "Maharashtra", 3,  true,  "2024-02-20", -0.008,  0.022);
        saveDonor("Amit Verma",     "amit@gmail.com",    "9812345678", "O+",  "Mumbai",    "Maharashtra", 8,  true,  "2023-11-10",  0.020, -0.015);
        saveDonor("Sneha Joshi",    "sneha@gmail.com",   "9745678901", "AB+", "Mumbai",    "Maharashtra", 1,  true,  "2024-03-01", -0.015, -0.010);
        saveDonor("Vikram Singh",   "vikram@gmail.com",  "9934567890", "A-",  "Mumbai",    "Maharashtra", 12, false, "2023-09-25",  0.030,  0.005);
        saveDonor("Anjali Mehta",   "anjali@gmail.com",  "9867890123", "O-",  "Mumbai",    "Maharashtra", 2,  true,  "2024-01-30", -0.025,  0.030);
        saveDonor("Rohit Kumar",    "rohit@gmail.com",   "9756789012", "B-",  "Mumbai",    "Maharashtra", 0,  true,  "2023-12-15",  0.010, -0.025);
        saveDonor("Deepa Nair",     "deepa@gmail.com",   "9645678901", "AB-", "Mumbai",    "Maharashtra", 4,  false, "2024-02-10", -0.020,  0.012);

        // Pune donors
        saveDonor("Arjun Reddy",    "arjun@gmail.com",   "9534567890", "A+",  "Pune",      "Maharashtra", 7,  true,  "2023-10-20",  0.015,  0.020);
        saveDonor("Kavya Iyer",     "kavya@gmail.com",   "9423456789", "B+",  "Pune",      "Maharashtra", 1,  true,  "2024-01-05", -0.010, -0.018);
        saveDonor("Manish Gupta",   "manish@gmail.com",  "9312345678", "O+",  "Pune",      "Maharashtra", 3,  true,  "2024-03-10",  0.025, -0.008);
        saveDonor("Pooja Shah",     "pooja@gmail.com",   "9201234567", "A-",  "Pune",      "Maharashtra", 6,  false, "2023-08-15", -0.018,  0.025);

        // Delhi donors
        saveDonor("Suresh Yadav",   "suresh@gmail.com",  "9190123456", "B+",  "Delhi",     "Delhi",       9,  true,  "2024-02-25",  0.018,  0.015);
        saveDonor("Nisha Agarwal",  "nisha@gmail.com",   "9089012345", "O+",  "Delhi",     "Delhi",       2,  true,  "2023-11-30", -0.012,  0.022);
        saveDonor("Kiran Bose",     "kiran@gmail.com",   "8978901234", "AB+", "Delhi",     "Delhi",       0,  true,  "2024-01-20",  0.008, -0.020);

        // Bangalore donors
        saveDonor("Ravi Pillai",    "ravi@gmail.com",    "8867890123", "A+",  "Bangalore", "Karnataka",   4,  false, "2023-12-01",  0.022,  0.010);
        saveDonor("Meena Krishnan", "meena@gmail.com",   "8756789012", "O-",  "Bangalore", "Karnataka",   11, true,  "2024-03-05", -0.015, -0.015);
        saveDonor("Sanjay Tiwari",  "sanjay@gmail.com",  "8645678901", "B+",  "Bangalore", "Karnataka",   1,  true,  "2023-10-10",  0.005,  0.025);

        // Chennai donors
        saveDonor("Asha Pandey",    "asha@gmail.com",    "8534567890", "A+",  "Chennai",   "Tamil Nadu",  5,  true,  "2024-02-15",  0.018, -0.012);
        saveDonor("Nikhil Desai",   "nikhil@gmail.com",  "8423456789", "O+",  "Chennai",   "Tamil Nadu",  3,  true,  "2023-09-30", -0.010,  0.018);

        // Blood requests
        saveRequest("Sunil Mehta",  "sunil@gmail.com",  "9111222333", "A+",  "Mumbai",    "Lilavati Hospital",      "FULFILLED", 3, 9);
        saveRequest("Geeta Singh",  "geeta@gmail.com",  "9222333444", "O+",  "Mumbai",    "Kokilaben Hospital",     "FULFILLED", 4, 8);
        saveRequest("Raj Malhotra", "raj@gmail.com",    "9333444555", "B+",  "Pune",      "Ruby Hall Clinic",       "PENDING",   2, 6);
        saveRequest("Sunita Rao",   "sunita@gmail.com", "9444555666", "AB+", "Mumbai",    "Breach Candy Hospital",  "PENDING",   1, 5);
        saveRequest("Anil Kapoor",  "anil@gmail.com",   "9555666777", "O-",  "Delhi",     "AIIMS Delhi",            "FULFILLED", 2, 4);
        saveRequest("Rekha Sharma", "rekha@gmail.com",  "9666777888", "A-",  "Bangalore", "Manipal Hospital",       "PENDING",   1, 3);
        saveRequest("Mohan Das",    "mohan@gmail.com",  "9777888999", "B-",  "Mumbai",    "Hinduja Hospital",       "PENDING",   0, 2);
        saveRequest("Sita Devi",    "sita@gmail.com",   "9888999000", "O+",  "Chennai",   "Apollo Hospital Chennai", "FULFILLED", 3, 1);

        System.out.println("✅ Dummy data seeded successfully!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 Admin  : admin@rakhtsathi.com / admin123");
        System.out.println("👤 Donor  : rahul@gmail.com      / donor123");
        System.out.println("📊 20 donors seeded with map coordinates");
        System.out.println("📋 8 blood requests seeded");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private User saveUser(String name, String email, String password, String role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        return userRepo.save(u);
    }

    private void saveDonor(String name, String email, String phone,
                            String bloodGroup, String city, String state,
                            int donations, boolean available, String lastDonated,
                            double latOffset, double lngOffset) {
        User user = saveUser(name, email, "donor123", "DONOR");

        double[] base = CITY_COORDS.getOrDefault(city,
            new double[]{ 19.0760, 72.8777 });

        DonorProfile p = new DonorProfile();
        p.setUser(user);
        p.setPhone(phone);
        p.setBloodGroup(bloodGroup);
        p.setCity(city);
        p.setState(state);
        p.setTotalDonations(donations);
        p.setAvailable(available);
        p.setLastDonated(LocalDate.parse(lastDonated));
        p.setLatitude(base[0]  + latOffset);
        p.setLongitude(base[1] + lngOffset);
        donorRepo.save(p);
    }

    private void saveRequest(String name, String email, String phone,
                              String bloodGroup, String city, String hospital,
                              String status, int notified, int daysAgo) {
        BloodRequest r = new BloodRequest();
        r.setRequesterName(name);
        r.setRequesterEmail(email);
        r.setRequesterPhone(phone);
        r.setBloodGroup(bloodGroup);
        r.setCity(city);
        r.setHospital(hospital);
        r.setStatus(status);
        r.setDonorsNotified(notified);
        r.setMessage("Urgent blood required. Please contact immediately.");
        r.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        requestRepo.save(r);
    }
}
