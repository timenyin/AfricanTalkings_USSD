package com.harmony2k.africantalkings_ussd.service;

import com.harmony2k.africantalkings_ussd.model.Customer;
import com.harmony2k.africantalkings_ussd.model.Session;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UssdService {

    // Thread-safe session storage
    private Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    private Map<String, Customer> customerDatabase = new HashMap<>();

    // Initialize with test customers including your number
    public UssdService() {
        // Add your test customer
        Customer testCustomer = new Customer("08100974728", "Test User");
        testCustomer.setWalletBalance(15250.0);
        testCustomer.setCreditBalance(7500.0);
        customerDatabase.put("08100974728", testCustomer);

        // Add another test customer
        Customer testCustomer2 = new Customer("08031111111", "John Doe");
        testCustomer2.setWalletBalance(5000.0);
        testCustomer2.setCreditBalance(2500.0);
        customerDatabase.put("08031111111", testCustomer2);

        System.out.println("✅ Test customers initialized:");
        System.out.println("   - 08100974728: Test User (Wallet: ₦15,250.00)");
        System.out.println("   - 08031111111: John Doe (Wallet: ₦5,000.00)");
    }

    public String processUssdRequest(String sessionId, String phoneNumber,
                                     String serviceCode, String text, String networkCode) {

        System.out.println("🔄 PROCESSING USSD REQUEST ===");
        System.out.println("Session: " + sessionId);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Text Input: '" + text + "'");
        System.out.println("Network: " + networkCode);

        // Validate required parameters
        if (sessionId == null || phoneNumber == null) {
            System.err.println("❌ Missing required parameters: sessionId or phoneNumber");
            return "CON Welcome to Balanceè\n\n1. Accept and Continue\n2. Reject";
        }

        // Get or create session
        Session session = activeSessions.get(sessionId);
        if (session == null) {
            System.out.println("🆕 Creating NEW session: " + sessionId);
            session = new Session(sessionId, phoneNumber, serviceCode, text, networkCode);
            activeSessions.put(sessionId, session);
        } else {
            System.out.println("📝 Using EXISTING session: " + sessionId);
            session.setText(text);
            session.setNetworkCode(networkCode);
        }

        String[] inputs = text != null ? text.split("\\*") : new String[0];
        session.setCurrentMenuLevel(inputs.length);

        System.out.println("📋 Input array: " + java.util.Arrays.toString(inputs));
        System.out.println("📊 Menu level: " + inputs.length);

        try {
            // Route based on current menu level and inputs
            if (inputs.length == 0) {
                return showWelcomeScreen(session);
            }

            return routeToMenu(session, inputs);
        } catch (Exception e) {
            System.err.println("❌ Error processing USSD request: " + e.getMessage());
            // Return to welcome screen on any error
            return "CON Welcome to Balanceè\n\n1. Accept and Continue\n2. Reject";
        }
    }

    private String showWelcomeScreen(Session session) {
        return "CON Welcome to Balanceè\nA fee of ₦6.98 will be charged from your airtime\n\n1. Accept and Continue\n2. Reject";
    }

    private String routeToMenu(Session session, String[] inputs) {
        String firstInput = inputs[0];

        switch (firstInput) {
            case "1": // Accept charges
                return handleAuthentication(session, inputs);
            case "2": // Reject
                activeSessions.remove(session.getSessionId());
                return "END Thank you for using Balanceè. Service cancelled.";
            default:
                return "CON Invalid option selected.\n\n1. Accept and Continue\n2. Reject";
        }
    }

    private String handleAuthentication(Session session, String[] inputs) {
        if (inputs.length == 1) {
            return "CON Enter your registered phone number:\n\nExample: 08100974728\nor +2348100974728";
        }

        if (inputs.length == 2) {
            String phoneNumber = inputs[1];
            return processPhoneNumber(session, phoneNumber);
        }

        // Route to main menu if we have more inputs
        return handleMainMenuRouting(session, inputs);
    }

    private String processPhoneNumber(Session session, String phoneNumber) {
        String cleanPhone = cleanPhoneNumber(phoneNumber);

        // Validate Nigerian number format
        if (!isValidNigerianNumber(cleanPhone)) {
            return "CON Invalid phone number format.\n\nPlease enter a valid Nigerian number:\nExample: 08100974728\nor +2348100974728";
        }

        Customer customer = customerDatabase.get(cleanPhone);

        if (customer != null) {
            // Existing customer
            session.setCustomerName(customer.getFullName());
            session.setCustomerPhone(cleanPhone);
            session.setWalletBalance(customer.getWalletBalance());

            System.out.println("✅ Authenticated existing customer: " + customer.getFullName());
            return "CON Authentication successful!\nWelcome back, " + customer.getFullName() + "!\n\n1. Continue to Main Menu\n0. Exit";
        } else {
            // New customer
            session.setCustomerPhone(cleanPhone);
            return "CON Phone number " + cleanPhone + " not registered.\n\n1. Register with this number\n2. Try different number\n0. Back";
        }
    }

    private String handleMainMenuRouting(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleMainMenuRouting");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));
        System.out.println("Inputs length: " + inputs.length);

        if (inputs.length == 2) {
            String option = inputs[1];

            if ("1".equals(option)) {
                // Continue to main menu
                return showMainMenu(session);
            } else if ("0".equals(option)) {
                activeSessions.remove(session.getSessionId());
                return "END Thank you for using Balanceè. Goodbye!";
            }
        }

        // Handle registration flow
        if (inputs.length >= 2) {
            String registrationOption = inputs[1];
            if ("1".equals(registrationOption) && inputs.length <= 3) {
                return handleRegistration(session, inputs);
            } else if ("2".equals(registrationOption)) {
                return "CON Enter different phone number:\n\nExample: 08100974728";
            } else if ("0".equals(registrationOption)) {
                return handleAuthentication(session, new String[]{inputs[0]});
            }
        }

        // Handle menu navigation for inputs with length >= 3
        if (inputs.length >= 3) {
            return handleMenuNavigation(session, inputs);
        }

        return showMainMenu(session);
    }

    private String handleRegistration(Session session, String[] inputs) {
        if (inputs.length == 2) {
            return "CON Registration for: " + session.getCustomerPhone() + "\n\nEnter your full name:";
        }

        if (inputs.length == 3) {
            String fullName = inputs[2].trim();

            // Validate name
            if (fullName.length() < 2 || fullName.length() > 50 || !fullName.matches("[a-zA-Z ]+")) {
                return "CON Invalid name format.\n\nPlease enter alphabetic characters only (2-50 characters):\nExample: Chinedu Okoro";
            }

            // Create new customer
            Customer newCustomer = new Customer(session.getCustomerPhone(), fullName);
            newCustomer.setWalletBalance(0.0);
            newCustomer.setCreditBalance(0.0);
            customerDatabase.put(session.getCustomerPhone(), newCustomer);

            session.setCustomerName(fullName);
            session.setWalletBalance(0.0);

            System.out.println("✅ New customer registered: " + fullName + " (" + session.getCustomerPhone() + ")");

            return "CON Registration successful!\nWelcome to Balanceè, " + fullName + "!\n\n1. Continue to Main Menu\n0. Exit";
        }

        return showMainMenu(session);
    }

    private String showMainMenu(Session session) {
        return "CON Welcome to Balanceè, " + session.getCustomerName() + "!\n\n" +
                "1. SOS or repairs\n" +
                "2. Money matters\n" +
                "3. Product purchase\n" +
                "4. Talk to an Agent\n" +
                "0. Exit";
    }

    private String handleMenuNavigation(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleMenuNavigation");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));
        System.out.println("Inputs length: " + inputs.length);

        if (inputs.length < 4) {
            System.out.println("❌ Not enough inputs for menu navigation, showing main menu");
            return showMainMenu(session);
        }

        // The main menu selection is at index 3 (fourth input) after:
        // [0]="1" (accept), [1]=phone, [2]="1" (continue), [3]=main menu option
        String mainMenuOption = inputs[3];
        System.out.println("Main menu option selected: " + mainMenuOption);

        switch (mainMenuOption) {
            case "1": // SOS services
                return handleSosServices(session, inputs);
            case "2": // Money matters
                return handleMoneyMatters(session, inputs);
            case "3": // Product purchase
                return "CON Product purchase service coming soon!\n\n0. Back to main menu";
            case "4": // Talk to agent
                return "CON Agent service coming soon!\n\n0. Back to main menu";
            case "0": // Exit
                activeSessions.remove(session.getSessionId());
                return "END Thank you for using Balanceè. Goodbye!";
            default:
                System.out.println("❌ Invalid main menu option: " + mainMenuOption);
                return "CON Invalid option selected.\n\n" + showMainMenu(session);
        }
    }

    private String handleSosServices(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleSosServices");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));

        if (inputs.length == 4) {
            return "CON SOS Services:\n\n1. SOS towing help\n2. Find a mechanic\n0. Back to main menu";
        }

        return "CON SOS services coming soon!\n\n0. Back to main menu";
    }

    private String handleMoneyMatters(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleMoneyMatters");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));

        if (inputs.length == 4) {
            return "CON Money Services:\n\n1. Check balance\n2. Add money\n3. Transaction history\n0. Back to main menu";
        }

        if (inputs.length == 5) {
            String moneyOption = inputs[4]; // This is the 5th input

            switch (moneyOption) {
                case "1": // Check balance
                    return handleCheckBalance(session, inputs);
                case "2": // Add money
                    return handleAddMoney(session, inputs);
                case "3": // Transaction history
                    return handleTransactionHistory(session, inputs);
                case "0": // Back
                    return showMainMenu(session);
                default:
                    return "CON Invalid option.\n\n1. Check balance\n2. Add money\n3. Transaction history\n0. Back to main menu";
            }
        }

        return handleMoneySubMenu(session, inputs);
    }

    private String handleCheckBalance(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleCheckBalance");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));

        if (inputs.length == 5) {
            return "CON Select account to check:\n\n1. Wallet balance\n2. Credit balance\n3. Both balances\n0. Back";
        }

        if (inputs.length == 6) {
            String balanceOption = inputs[5]; // This is the 6th input
            Customer customer = customerDatabase.get(session.getCustomerPhone());

            if (customer == null) {
                return "END Customer not found. Please restart session.";
            }

            switch (balanceOption) {
                case "1": // Wallet balance
                    return "END Account Balance:\n\n💰 Wallet: ₦" + String.format("%,.2f", customer.getWalletBalance());
                case "2": // Credit balance
                    return "END Account Balance:\n\n💳 Credit: ₦" + String.format("%,.2f", customer.getCreditBalance());
                case "3": // Both balances
                    double total = customer.getWalletBalance() + customer.getCreditBalance();
                    return "END Account Balances:\n\n💰 Wallet: ₦" + String.format("%,.2f", customer.getWalletBalance()) +
                            "\n💳 Credit: ₦" + String.format("%,.2f", customer.getCreditBalance()) +
                            "\n────────────────────\n📊 Total: ₦" + String.format("%,.2f", total) +
                            "\n\nLast updated: " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                case "0": // Back
                    return handleMoneyMatters(session, new String[]{inputs[0], inputs[1], inputs[2], inputs[3]});
                default:
                    return "CON Invalid option.\n\n1. Wallet balance\n2. Credit balance\n3. Both balances\n0. Back";
            }
        }

        return handleMoneyMatters(session, inputs);
    }

    private String handleAddMoney(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleAddMoney");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));

        if (inputs.length == 5) {
            return "CON Add money to:\n\n1. Wallet\n2. Credit account\n0. Back";
        }

        if (inputs.length == 6) {
            String accountType = inputs[5];
            if ("0".equals(accountType)) {
                return handleMoneyMatters(session, new String[]{inputs[0], inputs[1], inputs[2], inputs[3]});
            }
            return "CON Enter amount to add:\n\nMinimum: ₦500\nMaximum: ₦100,000";
        }

        if (inputs.length == 7) {
            try {
                double amount = Double.parseDouble(inputs[6]);
                if (amount < 500 || amount > 100000) {
                    return "CON Invalid amount: ₦" + String.format("%,.0f", amount) +
                            "\n\nMust be between ₦500 - ₦100,000\nPlease enter valid amount:";
                }

                Customer customer = customerDatabase.get(session.getCustomerPhone());
                String accountType = inputs[5];
                String reference = "BAL" + System.currentTimeMillis();

                if ("1".equals(accountType)) {
                    customer.setWalletBalance(customer.getWalletBalance() + amount);
                    return "END ✅ Payment instructions sent via SMS\n\nAdd ₦" + String.format("%,.0f", amount) + " to your Wallet\nReference: " + reference;
                } else {
                    customer.setCreditBalance(customer.getCreditBalance() + amount);
                    return "END ✅ Payment instructions sent via SMS\n\nAdd ₦" + String.format("%,.0f", amount) + " to your Credit\nReference: " + reference;
                }

            } catch (NumberFormatException e) {
                return "CON Invalid amount format.\n\nPlease enter numbers only:\nExample: 10000 for ten thousand naira";
            }
        }

        return handleMoneyMatters(session, inputs);
    }

    private String handleTransactionHistory(Session session, String[] inputs) {
        System.out.println("🔍 DEBUG - handleTransactionHistory");
        System.out.println("Inputs: " + java.util.Arrays.toString(inputs));

        if (inputs.length == 5) {
            return "CON Transaction history for:\n\n1. Last 7 days\n2. Last 30 days\n3. Custom date range\n0. Back";
        }

        if (inputs.length == 6) {
            Customer customer = customerDatabase.get(session.getCustomerPhone());
            double currentBalance = customer != null ? customer.getWalletBalance() : 0.0;

            String history = "END Recent Transactions (Last 7 days):\n\n" +
                    "📅 " + java.time.LocalDate.now().minusDays(2) + " - Towing service: -₦4,250\n" +
                    "📅 " + java.time.LocalDate.now().minusDays(5) + " - Wallet top-up: +₦20,000\n" +
                    "📅 " + java.time.LocalDate.now().minusDays(7) + " - Mechanic fee: -₦500\n" +
                    "📅 " + java.time.LocalDate.now().minusDays(10) + " - Fuel purchase: -₦5,000\n" +
                    "────────────────────\n" +
                    "💼 Current balance: ₦" + String.format("%,.2f", currentBalance);

            return history;
        }

        return handleMoneyMatters(session, inputs);
    }

    private String handleMoneySubMenu(Session session, String[] inputs) {
        return handleMoneyMatters(session, inputs);
    }

    // Utility methods
    private String cleanPhoneNumber(String phone) {
        if (phone == null) return "";
        if (phone.startsWith("+234")) {
            return "0" + phone.substring(4);
        }
        return phone.replaceAll("[^0-9]", "");
    }

    private boolean isValidNigerianNumber(String phone) {
        return phone != null && phone.matches("^0[7-9][0-9]{9}$");
    }

    public Customer getCustomer(String phoneNumber) {
        return customerDatabase.get(cleanPhoneNumber(phoneNumber));
    }

    public void cleanupExpiredSessions() {
        // Simple cleanup - remove sessions older than 1 hour
        long currentTime = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry -> {
            // You can add timestamp tracking to Session class for proper cleanup
            return activeSessions.size() > 100; // Keep only recent 100 sessions
        });
    }

    // Add this method to UssdService.java for quick testing
    public String quickTest(String sessionId, String phoneNumber) {
        return "CON Balanceè USSD Service - Live\n\n1. Continue to Main Menu\n0. Exit";
    }
}