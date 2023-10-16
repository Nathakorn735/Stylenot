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
    private static final String LOGOUT_MOVEMENT_FILE = "src/resources/json/Logout_movement.json";
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
                        // กำหนดค่าตัวแปรหลังจากตรวจสอบสำเร็จ
                        loggedInUser = storedUsername;
                        loggedInEmpID = (String) user.get("EmpID");
                        loggedInEmpName = (String) user.get("EmpName");

                        // เรียกใช้งานฟังก์ชัน updateLoginMovement หลังจากกำหนดค่า
                        updateLoginMovement(loggedInUser, loggedInEmpID, loggedInEmpName);

                        // แสดงข้อความต้อนรับ
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
                                while (true) {
                                    System.out.println("กรุณาเลือกรายการ:");
                                    System.out.println("1. View Menu");
                                    System.out.println("2. Search Product");
                                    System.out.println("3. Add Menu");
                                    System.out.println("4. Delete Menu");
                                    System.out.println("5. Edit Menu");
                                    System.out.println("6. Receipt");
                                    System.out.println("7. เรียงราคาสินค้าจากน้อยไปมาก");
                                    System.out.println("8. SalesReport");

                                    int choice = 0;
                                    try {
                                        choice = scanner.nextInt();
                                    } catch (Exception e) {
                                        System.out.println(" ");
                                        System.out.println("กรุณากรอกตัวเลขเท่านั้นครับ");
                                        System.out.println(" ");
                                        scanner.nextLine(); // เพื่อล้างคิวอาร์เรย์ของ Scanner
                                                            // จากตัวหลังค่าที่ถูกใส่เข้ามา
                                        continue; // เริ่มลูปใหม่
                                    }

                                    switch (choice) {
                                        case 1:
                                            p1.viewMenu();
                                            e1.viewMenuEarrings();
                                            r1.viewMenuRings();
                                            break;
                                        case 2:
                                            do {
                                                System.out.println(
                                                        "Enter productID to search (must start with 'E' or 'R' followed by digits):");
                                                String productIDToSearch = scanner.next();

                                                if (productIDToSearch.matches("[ER]\\d+")) {
                                                    ProductSearch productSearch = new ProductSearch();
                                                    boolean foundInEarrings = productSearch
                                                            .searchProductByID(productIDToSearch, EARRINGS_FILE);
                                                    boolean foundInRings = productSearch
                                                            .searchProductByID(productIDToSearch, RINGS_FILE);

                                                    if (!foundInEarrings && !foundInRings) {
                                                        System.out.println(
                                                                "No matching product found for the entered productID.");
                                                    } else {
                                                        break; // ถ้าข้อมูลถูกต้อง ออกจากลูป
                                                    }
                                                } else {
                                                    System.out.println(
                                                            "Invalid productID format. Please enter a productID starting with 'E' or 'R' followed by digits.");
                                                }
                                            } while (true);
                                            break;

                                        case 3:
                                            int productTypeChoice;

                                            do {
                                                System.out.println("Choose product type:");
                                                System.out.println("1. Earrings");
                                                System.out.println("2. Rings");

                                                if (scanner.hasNextInt()) {
                                                    productTypeChoice = scanner.nextInt();

                                                    switch (productTypeChoice) {
                                                        case 1:
                                                            m1.addEarring();
                                                            break;
                                                        case 2:
                                                            m1.addRing();
                                                            break;
                                                        default:
                                                            System.out.println(
                                                                    "Invalid choice. Please select a menu item from 1 to 2.");
                                                            break;
                                                    }
                                                } else {
                                                    System.out.println("Invalid input. Please enter a number.");
                                                    scanner.next(); // Clear the invalid input from the scanner
                                                    productTypeChoice = 0; // Set to 0 to continue the loop
                                                }
                                            } while (productTypeChoice < 1 || productTypeChoice > 2);
                                            

                                        case 4:

                                        int productTypeChoice2;

                                            do {
                                                System.out.println("Choose product type:");
                                                System.out.println("1. Earrings");
                                                System.out.println("2. Rings");

                                                if (scanner.hasNextInt()) {
                                                    productTypeChoice2 = scanner.nextInt();

                                                    switch (productTypeChoice2) {
                                                        case 1:
                                                            m1.deleteEarring();
                                                            break;
                                                        case 2:
                                                            m1.deleteRing();
                                                            break;
                                                        default:
                                                            System.out.println(
                                                                    "Invalid choice. Please select a menu item from 1 to 2.");
                                                            break;
                                                    }
                                                } else {
                                                    System.out.println("Invalid input. Please enter a number.");
                                                    scanner.next(); // Clear the invalid input from the scanner
                                                    productTypeChoice2 = 0; // Set to 0 to continue the loop
                                                }
                                            } while (productTypeChoice2 < 1 || productTypeChoice2 > 2);
                                            
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
                                                    System.out.println(
                                                            "Invalid choice, Please select a menu item from 1 to 2.");
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
                                            System.out
                                                    .println("Invalid choice, Please select a menu item from 1 to 8.");
                                    }

                                    System.out.println("Do you want to continue? (Y/N)");
                                    String continueChoice = scanner.next();
                                    if (!continueChoice.equalsIgnoreCase("Y")) {
                                        break;
                                    }
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
                                            System.out
                                                    .println("Invalid choice, Please select a menu item from 1 to 2.");
                                        }
                                        break;
                                    case 3:
                                        System.out.println("Not implemented yet");
                                        break;
                                    default:
                                        System.out.println("Invalid choice, Please select a menu item from 1 to 3.");
                                }
                            }

                            System.out.println("Do you want to continue? (Y/N)");
                        } while (scanner.next().equalsIgnoreCase("Y"));

                        updateLogoutMovement(loggedInUser, loggedInEmpID, loggedInEmpName);

                        if (option == 1) {
                            System.out.println("Manager Logout...");
                        } else if (option == 2) {
                            System.out.println("Cashier Logout...");
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

    private static void updateLogoutMovement(String username, String empID, String empName) {
        try {
            JSONArray logoutMovementArray;
            try (FileReader fileReader = new FileReader(LOGOUT_MOVEMENT_FILE)) {
                if (fileReader.read() == -1) {
                    logoutMovementArray = new JSONArray();
                } else {
                    logoutMovementArray = (JSONArray) new JSONParser().parse(new FileReader(LOGOUT_MOVEMENT_FILE));
                }
            }

            JSONObject logoutMovementData = new JSONObject();
            logoutMovementData.put("EmpID", empID);
            logoutMovementData.put("Username", username);
            logoutMovementData.put("EmpName", empName);
            logoutMovementData.put("LogoutSequence", getNextLogoutSequence());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedTime = sdf.format(new Date());

            logoutMovementData.put("LogoutTime", formattedTime);

            logoutMovementArray.add(0, logoutMovementData);

            try (FileWriter fileWriter = new FileWriter(LOGOUT_MOVEMENT_FILE)) {
                fileWriter.write(logoutMovementArray.toJSONString() + "\n");
                System.out.println("Logout movement data updated.");
            }
        } catch (Exception e) {
            System.out.println("Error updating logout movement data: " + e.getMessage());
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

    private static long getNextLogoutSequence() {
        try {
            JSONArray logoutMovementArray;
            try (FileReader fileReader = new FileReader(LOGOUT_MOVEMENT_FILE)) {
                if (fileReader.read() == -1) {
                    logoutMovementArray = new JSONArray();
                } else {
                    logoutMovementArray = (JSONArray) new JSONParser().parse(new FileReader(LOGOUT_MOVEMENT_FILE));
                }
            }

            long maxSequence = logoutMovementArray.size();

            return maxSequence + 1;
        } catch (Exception e) {
            System.out.println("Error getting next logout sequence: " + e.getMessage());
            return 1;
        }
    }
}
