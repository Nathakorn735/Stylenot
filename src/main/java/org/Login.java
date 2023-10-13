package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Scanner;

public class Login {
    private static final String USERS_FILE = "src/resources/json/users.json";

    public static void main(String[] args) {
        JSONArray jsonArray = loadUsersData();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Username: ");
        String inputUsername = scanner.next();

        System.out.print("Enter Password: ");
        String inputPassword = scanner.next();

        boolean isAuthenticated = authenticate(jsonArray, inputUsername, inputPassword);

        if (isAuthenticated) {
            System.out.println("Welcome!");
        } else {
            System.out.println("Authentication failed!");
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
