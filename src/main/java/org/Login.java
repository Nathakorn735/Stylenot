package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Scanner;

public class Login {
    private static final String USERS_FILE = "src/resources/json/users.json";
    private static final int MAX_ATTEMPTS = 5;

    public static void main(String[] args) {
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

            System.out.print("Enter Password: ");
            // แสดงรหัสผ่านปกติ
            // String inputPassword = scanner.next();
            // แสดงรหัสผ่านปกติ

            // ไม่แสดงรหัสผ่าน
            System.out.print("Enter Password: ");
            char[] passwordChars = System.console().readPassword();
            String inputPassword = new String(passwordChars);
            // ไม่แสดงรหัสผ่าน

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

                        System.out.println("Access Granted! Welcome, " + loggedInUser + "!");
                        System.out.println("Employee ID: " + loggedInEmpID);

                        String role = loggedInEmpID.substring(0, 1);
                        if (role.equals("M")) {
                            System.out.println("Employee Name: " + loggedInEmpName);
                            System.out.println("Role: Manager");
                            // เพิ่มโค้ดที่คุณต้องการทำเมื่อเป็น Manager
                        } else if (role.equals("C")) {
                            System.out.println("Employee Name: " + loggedInEmpName);
                            System.out.println("Role: Cashier");
                            // เพิ่มโค้ดที่คุณต้องการทำเมื่อเป็น Cashier
                        }

                        break; // หลุดจากลูปหลักเมื่อตรวจสอบผู้ใช้สำเร็จ
                    }
                }
            } else {
                attempts++;
                System.out.println("Authentication failed! Remaining attempts: " + (MAX_ATTEMPTS - attempts));
            }
        }

        if (isAuthenticated) {
            System.out.println("Welcome!");
        } else {
            System.out.println("Authentication failed! Maximum attempts reached.");
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
                return true; // พบข้อมูลที่ตรงกัน
            }
        }
        return false; // ไม่พบข้อมูลที่ตรงกัน
    }
}
