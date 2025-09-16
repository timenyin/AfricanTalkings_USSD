package com.harmony2k.africantalkings_ussd.controller;

import com.harmony2k.africantalkings_ussd.model.Session;
import com.harmony2k.africantalkings_ussd.service.BalanceService;
import com.harmony2k.africantalkings_ussd.service.SessionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@RestController
@RequestMapping("/ussd")
public class UssdController {

    private final SessionService sessionService;
    private final BalanceService balanceService;

    public UssdController(SessionService sessionService, BalanceService balanceService) {
        this.sessionService = sessionService;
        this.balanceService = balanceService;
    }

    @PostMapping(value = "/callback", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleUssd(
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serviceCode", required = false) String serviceCode,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "text", required = false) String text) {

        System.out.println("Received USSD request: " +
                "sessionId=" + sessionId +
                ", phoneNumber=" + phoneNumber +
                ", text=" + text);

        // Validate required parameters
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return buildEND("Invalid phone number. Please try again.");
        }

        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "sess_" + System.currentTimeMillis() + "_" + phoneNumber;
        }

        // Get or create session
        Session session = sessionService.getSession(sessionId, phoneNumber);
        session.touch();

        // Process input text
        String[] parts = (text == null || text.trim().isEmpty()) ?
                new String[0] : text.split("\\*");

        // Handle back navigation (0)
        if (parts.length > 0 && "0".equals(parts[parts.length - 1])) {
            parts = Arrays.copyOf(parts, parts.length - 1);
        }

        // Route based on menu level
        try {
            if (parts.length == 0) {
                return buildMainMenu();
            }

            String topLevelChoice = parts[0];
            switch (topLevelChoice) {
                case "1": return handleSosFlow(parts, session);
                case "2": return handleMoneyFlow(parts, session);
                case "3": return handleProductsFlow(parts, session);
                case "4": return handleAgentFlow(parts, session);
                default: return buildEND("Invalid option. Thank you for using Balanceè.");
            }
        } catch (Exception e) {
            System.err.println("Error processing USSD request: " + e.getMessage());
            e.printStackTrace();
            return buildEND("Sorry, an error occurred. Please try again later.");
        }
    }

    private String buildMainMenu() {
        return buildCON(
                "Welcome to Balanceè",
                "1. SOS or repairs",
                "2. Money matters",
                "3. Product purchase",
                "4. Talk to an Agent"
        );
    }

    private String handleSosFlow(String[] parts, Session session) {
        if (parts.length == 1) {
            return buildCON(
                    "SOS Services:",
                    "1. SOS towing help",
                    "2. Find a mechanic",
                    "0. Back to main menu"
            );
        }

        String subChoice = parts[1];

        if ("1".equals(subChoice)) {
            // SOS towing help flow
            if (parts.length == 2) {
                return buildCON("SOS towing service selected. Please provide your location:");
            } else if (parts.length == 3) {
                // User entered location (parts[2])
                String location = parts[2];
                session.setData("sos_location", location);

                // Calculate estimated cost based on location
                int distance = calculateDistance(location);
                BigDecimal cost = calculateTowingCost(distance);

                return buildCON(
                        "Estimated towing cost: ₦" + cost + " (Distance: " + distance + "km)",
                        "1. Confirm and pay 50% deposit",
                        "2. Cancel request",
                        "0. Back"
                );
            } else if (parts.length >= 4) {
                String confirmation = parts[3];
                if ("1".equals(confirmation)) {
                    return buildEND("SOS towing confirmed! Tow truck dispatched. ETA: 25 mins.");
                } else {
                    return buildEND("SOS request cancelled.");
                }
            }
        } else if ("2".equals(subChoice)) {
            // Find a mechanic flow
            if (parts.length == 2) {
                return buildCON("Please enter your location for mechanic search:");
            } else if (parts.length == 3) {
                // User entered location (parts[2])
                String location = parts[2];
                session.setData("mechanic_location", location);

                return buildCON(
                        "Nearest mechanics near " + location + ":",
                        "1. Musa Workshop - 1.5km (ETA: 15 mins)",
                        "2. Tunde Motors - 2.1km (ETA: 20 mins)",
                        "3. Akin Garage - 2.8km (ETA: 25 mins)",
                        "0. Back"
                );
            } else if (parts.length >= 4) {
                String mechanicChoice = parts[3];
                String mechanicName = "";

                switch (mechanicChoice) {
                    case "1": mechanicName = "Musa Workshop"; break;
                    case "2": mechanicName = "Tunde Motors"; break;
                    case "3": mechanicName = "Akin Garage"; break;
                    default: return buildEND("Invalid mechanic selection.");
                }

                return buildEND("Mechanic " + mechanicName + " contacted! They will call you within 2 minutes.");
            }
        }

        return buildEND("Invalid SOS service selection.");
    }

    private String handleMoneyFlow(String[] parts, Session session) {
        if (parts.length == 1) {
            return buildCON(
                    "Money Services:",
                    "1. Check balance",
                    "2. Add money",
                    "0. Back to main menu"
            );
        }

        String subChoice = parts[1];

        if ("1".equals(subChoice)) {
            // Check balance
            BigDecimal walletBalance = balanceService.getWalletBalance(session.getPhoneNumber());
            BigDecimal creditBalance = balanceService.getCreditBalance(session.getPhoneNumber());
            return buildEND("Wallet: ₦" + formatMoney(walletBalance) +
                    ", Credit: ₦" + formatMoney(creditBalance));
        } else if ("2".equals(subChoice)) {
            // Add money flow
            if (parts.length == 2) {
                return buildCON(
                        "Add money to:",
                        "1. Wallet",
                        "2. Credit",
                        "0. Back"
                );
            } else if (parts.length == 3) {
                String accountType = parts[2];
                session.setData("account_type", accountType);
                return buildCON("Enter amount to add:");
            } else if (parts.length >= 4) {
                String amount = parts[3];
                String accountType = session.getData("account_type");
                String accountName = "1".equals(accountType) ? "Wallet" : "Credit";
                return buildEND("₦" + amount + " added to your " + accountName + " successfully!");
            }
        }

        return buildEND("Money service completed!");
    }

    private String handleProductsFlow(String[] parts, Session session) {
        if (parts.length == 1) {
            return buildCON(
                    "Product Services:",
                    "1. Fuel",
                    "2. Car accessories",
                    "3. Spare parts",
                    "0. Back to main menu"
            );
        }

        String subChoice = parts[1];

        if ("1".equals(subChoice)) {
            // Fuel submenu
            if (parts.length == 2) {
                return buildCON(
                        "Select Fuel Station:",
                        "1. Mobil - 1.2km",
                        "2. Total - 2.5km",
                        "3. NNPC - 3km",
                        "0. Back"
                );
            } else if (parts.length == 3) {
                String stationChoice = parts[2];
                session.setData("fuel_station", stationChoice);
                return buildCON(
                        "Select Fuel Type:",
                        "1. Petrol - ₦250/L",
                        "2. Diesel - ₦300/L",
                        "0. Back"
                );
            } else if (parts.length == 4) {
                String fuelType = parts[3];
                session.setData("fuel_type", fuelType);
                return buildCON("Enter amount in ₦:");
            } else if (parts.length >= 5) {
                String amount = parts[4];
                return buildEND("Fuel order confirmed! Delivery ETA: 30 mins. Amount: ₦" + amount);
            }
        } else if ("2".equals(subChoice)) {
            // Car accessories submenu
            if (parts.length == 2) {
                return buildCON(
                        "Car Accessories:",
                        "1. Car mats - ₦8,000",
                        "2. Phone holder - ₦3,500",
                        "0. Back"
                );
            } else if (parts.length == 3) {
                String accessoryChoice = parts[2];
                session.setData("accessory", accessoryChoice);
                return buildCON("Enter quantity:");
            } else if (parts.length >= 4) {
                String quantity = parts[3];
                String accessory = "1".equals(session.getData("accessory")) ? "Car mats" : "Phone holder";
                return buildEND("Order confirmed! " + quantity + " x " + accessory + ". ETA: 48hrs.");
            }
        } else if ("3".equals(subChoice)) {
            // Spare parts submenu
            if (parts.length == 2) {
                return buildCON(
                        "Spare Parts:",
                        "1. Brake pads - ₦12,000",
                        "2. Oil filter - ₦4,500",
                        "0. Back"
                );
            } else if (parts.length == 3) {
                String partChoice = parts[2];
                session.setData("spare_part", partChoice);
                return buildCON("Enter quantity:");
            } else if (parts.length >= 4) {
                String quantity = parts[3];
                String part = "1".equals(session.getData("spare_part")) ? "Brake pads" : "Oil filter";
                return buildEND("Order confirmed! " + quantity + " x " + part + ". ETA: 48hrs.");
            }
        }

        return buildEND("Invalid product selection");
    }

    private String handleAgentFlow(String[] parts, Session session) {
        if (parts.length == 1) {
            return buildCON(
                    "Agent Services:",
                    "1. Connect to agent",
                    "2. Request callback",
                    "0. Back to main menu"
            );
        }

        String subChoice = parts[1];

        if ("1".equals(subChoice)) {
            if (parts.length == 2) {
                return buildCON(
                        "Connect to Agent:",
                        "1. Voice call (₦7.50/20sec)",
                        "2. Chat message",
                        "0. Back"
                );
            } else if (parts.length >= 3) {
                String contactMethod = parts[2];
                String method = "1".equals(contactMethod) ? "voice call" : "chat message";
                return buildEND("Connecting you to an agent via " + method + "...");
            }
        } else if ("2".equals(subChoice)) {
            if (parts.length == 2) {
                return buildCON("Enter your preferred callback time (e.g., 2:00 PM):");
            } else if (parts.length >= 3) {
                String callbackTime = parts[2];
                return buildEND("Callback request received. We'll call you at " + callbackTime + ".");
            }
        }

        return buildEND("Invalid agent service selection");
    }

    // Helper methods
    private String buildCON(String... lines) {
        StringBuilder response = new StringBuilder("CON ");
        for (int i = 0; i < lines.length; i++) {
            response.append(lines[i]);
            if (i < lines.length - 1) {
                response.append("\n");
            }
        }
        return response.toString();
    }

    private String buildEND(String message) {
        return "END " + message;
    }

    private String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    // Helper methods for SOS calculations
    private int calculateDistance(String location) {
        if (location.toLowerCase().contains("lagos") || location.toLowerCase().contains("mainland")) {
            return 5;
        } else if (location.toLowerCase().contains("ikeja") || location.toLowerCase().contains("airport")) {
            return 8;
        } else {
            return 12;
        }
    }

    private BigDecimal calculateTowingCost(int distance) {
        BigDecimal baseCost = new BigDecimal("5000");
        BigDecimal perKmCost = new BigDecimal("500");
        return baseCost.add(perKmCost.multiply(new BigDecimal(distance)));
    }
}