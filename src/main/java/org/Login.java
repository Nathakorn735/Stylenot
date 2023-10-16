package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Login {
    private static final String LOGIN_MOVEMENT_FILE = "src/resources/json/Login_movement.json";
    private static final String USERS_FILE = "src/resources/json/users.json";
    private static final String EARRINGS_FILE = "src/resources/json/Earringsproducts.json";
    private static final String RINGS_FILE = "src/resources/json/Ringsproducts.json";
    private static final int MAX_ATTEMPTS = 5;

    public static void login() {
        JSONArray jsonArray = loadUsersData();
        Scanner scanner = new Scanner(System.in);
        String loggedInEmpID = null;
        String loggedInUser = null;
        String loggedInEmpName = null;

        boolean isAuthenticated = false;
        int attempts = 0;

        while (!isAuthenticated && attempts < MAX_ATTEMPTS) {
            System.out.print("Enter Username: ");
            String inputUsername = scanner.next();
            Product p1 = new Product();
            Rings r1 = new Rings();
            Earring e1 = new Earring();
            ProductSearch ps1 = new ProductSearch();
            Manager m1 = new Manager(inputUsername, inputUsername, loggedInUser, loggedInEmpName, inputUsername,
                    attempts);
            Cashier c1 = new Cashier(inputUsername, inputUsername, loggedInUser, loggedInEmpName, inputUsername);

            System.out.print("Enter Password: ");
            char[] passwordChars = System.console().readPassword();
            String inputPassword = new String(passwordChars);

            isAuthenticated = authenticate(jsonArray, inputUsername, inputPassword);

            if (isAuthenticated) {
                for (Object obj : jsonArray) {
                    JSONObject user = (JSONObject) obj;
                    String storedUsername = (String) user.get("Username");
                    String storedPassword = (String) user.get("Password");

                    if (inputUsername.equals(storedUsername) && inputPassword.equals(storedPassword)) {
                        loggedInUser = storedUsername;
                        loggedInEmpID = (String) user.get("EmpID");
                        loggedInEmpName = (String) user.get("EmpName");
                        System.out.println();
                        System.out.println("Access Granted! Welcome, " + loggedInUser + "!");
                        System.out.println("Employee ID: " + loggedInEmpID);

                        String role = loggedInEmpID.substring(0, 1);
                        int option = 0;

                        if (role.equals("M")) {
                            System.out.println("Role: Manager");
                            option = 1;
                        } else if (role.equals("C")) {
                            System.out.println("Role: Cashier");
                            option = 2;
                        }

                        do {
                            if (option == 1) {
                                System.out.println("กรุณาเลือกรายการ:");
                                System.out.println("1. View Menu");
                                System.out.println("2. Search Product");
                                System.out.println("3. Add Menu");
                                System.out.println("4. Delete Menu");
                                System.out.println("5. Edit Menu");
                                System.out.println("6. Receipt");
                                System.out.println("7. เรียงราคาสินค้าจากน้อยไปมาก");
                                System.out.println("8. SalesReport");

                                int choice = scanner.nextInt();

                                switch (choice) {
                                    case 1:
                                        p1.viewMenu();
                                        e1.viewMenuEarrings();
                                        r1.viewMenuRings();
                                        break;
                                    case 2:
                                        System.out.println("Enter productID to search:");
                                        String productIDToSearch = scanner.next();

                                        ProductSearch productSearch = new ProductSearch();
                                        productSearch.searchProductByID(productIDToSearch, EARRINGS_FILE);
                                        productSearch.searchProductByID(productIDToSearch, RINGS_FILE);
                                        break;

                                    case 3:
                                        System.out.println("Choose product type:");
                                        System.out.println("1. Earrings");
                                        System.out.println("2. Rings");

                                        int productTypeChoice = scanner.nextInt();

                                        if (productTypeChoice == 1) {
                                            m1.addEarring();
                                        } else if (productTypeChoice == 2) {
                                            m1.addRing();
                                        } else {
                                            System.out.println("Invalid product type choice!");
                                        }
                                        break;
                                    case 4:
                                        System.out.println("Choose product type:");
                                        System.out.println("1. Earrings");
                                        System.out.println("2. Rings");

                                        int productTypeChoice2 = scanner.nextInt();

                                        if (productTypeChoice2 == 1) {
                                            m1.deleteEarring();
                                        } else if (productTypeChoice2 == 2) {
                                            m1.deleteRing();
                                        } else {
                                            System.out.println("Invalid product type choice!");
                                        }
                                        break;
                                    case 5:
                                        System.out.println("Select product category:");
                                        System.out.println("1. Earrings");
                                        System.out.println("2. Rings");

                                        int categoryChoice = scanner.nextInt();

                                        switch (categoryChoice) {
                                            case 1:
                                                m1.displayAllProducts(EARRINGS_FILE);
                                                m1.editProduct(EARRINGS_FILE);
                                                break;
                                            case 2:
                                                m1.displayAllProducts(RINGS_FILE);
                                                m1.editProduct(RINGS_FILE);
                                                break;
                                            default:
                                                System.out.println("Invalid choice. Exiting...");
                                        }
                                        break;
                                    case 6:
                                        System.out.println("Not implemented yet");
                                        break;
                                    case 7:
                                        ps1.displaySortedPrices(EARRINGS_FILE);
                                        ps1.displaySortedPrices(RINGS_FILE);
                                        break;
                                    case 8:
                                        SalesReport salesReport = new SalesReport();
                                        salesReport.showReceiptData();
                                        break;
                                    default:
                                        System.out.println("Invalid choice");
                                }
                            } else if (option == 2) {
                                System.out.println("กรุณาเลือกรายการ:");
                                System.out.println("1. View Menu");
                                System.out.println("2. Order");
                                System.out.println("3. Receipt");

                                int choice = scanner.nextInt();

                                switch (choice) {
                                    case 1:
                                        p1.viewMenu();
                                        e1.viewMenuEarrings();
                                        r1.viewMenuRings();
                                        break;
                                    case 2:
                                        System.out.println("สั่งสินค้า");
                                        System.out.println("Choose product type:");
                                        System.out.println("1. Earrings");
                                        System.out.println("2. Rings");

                                        int productTypeChoice2 = scanner.nextInt();

                                        if (productTypeChoice2 == 1) {
                                            c1.orderEarring();
                                        } else if (productTypeChoice2 == 2) {
                                            c1.orderRing();
                                        } else {
                                            System.out.println("Invalid product type choice!");
                                        }
                                        break;
                                    case 3:
                                        System.out.println("Not implemented yet");
                                        break;
                                    default:
                                        System.out.println("Invalid choice");
                                }
                            }

                            updateLoginMovement(loggedInUser, loggedInEmpID, loggedInEmpName);

                            System.out.println("Do you want to continue? (Y/N)");
                        } while (scanner.next().equalsIgnoreCase("Y"));

                        if (option == 1) {
                            System.out.println("Returning to Manager options...");
                        } else if (option == 2) {
                            System.out.println("Returning to Cashier options...");
                        }
                        break; // Exit the main loop when the user is authenticated
                    }
                }
            } else {
                attempts++;
                System.out.println("Authentication failed! Remaining attempts: " + (MAX_ATTEMPTS - attempts));
            }
        }

        scanner.close();
    }

    private static JSONArray loadUsersData() {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(new FileReader(USERS_FILE));
        } catch (Exception e) {
            System.out.println("An error occurred while loading user data: " + e.getMessage());
            return new JSONArray();
        }
    }

    private static boolean authenticate(JSONArray jsonArray, String username, String password) {
        for (Object obj : jsonArray) {
            JSONObject user = (JSONObject) obj;
            String storedUsername = (String) user.get("Username");
            String storedPassword = (String) user.get("Password");

            if (username.equals(storedUsername) && password.equals(storedPassword)) {
                return true; // Matching credentials found
            }
        }
        return false; // No matching credentials found
    }

    private static void updateLoginMovement(String username, String empID, String empName) {
        try {
            JSONArray loginMovementArray;
            try (FileReader fileReader = new FileReader(LOGIN_MOVEMENT_FILE)) {
                if (fileReader.read() == -1) {
                    loginMovementArray = new JSONArray();
                } else {
                    loginMovementArray = (JSONArray) new JSONParser().parse(new FileReader(LOGIN_MOVEMENT_FILE));
                }
            }

            JSONObject loginMovementData = new JSONObject();
            loginMovementData.put("EmpID", empID);
            loginMovementData.put("Username", username);
            loginMovementData.put("EmpName", empName);
            loginMovementData.put("LoginSequence", getNextLoginSequence());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedTime = sdf.format(new Date());

            loginMovementData.put("LoginTime", formattedTime);

            loginMovementArray.add(0, loginMovementData);

            try (FileWriter fileWriter = new FileWriter(LOGIN_MOVEMENT_FILE)) {
                fileWriter.write(loginMovementArray.toJSONString() + "\n");
                System.out.println("Login movement data updated.");
            }
        } catch (Exception e) {
            System.out.println("Error updating login movement data: " + e.getMessage());
        }
    }

    private static long getNextLoginSequence() {
        try {
            JSONArray loginMovementArray;
            try (FileReader fileReader = new FileReader(LOGIN_MOVEMENT_FILE)) {
                if (fileReader.read() == -1) {
                    loginMovementArray = new JSONArray();
                } else {
                    loginMovementArray = (JSONArray) new JSONParser().parse(new FileReader(LOGIN_MOVEMENT_FILE));
                }
            }

            long maxSequence = loginMovementArray.size();

            return maxSequence + 1;
        } catch (Exception e) {
            System.out.println("Error getting next login sequence: " + e.getMessage());
            return 1;
        }
    }
}
