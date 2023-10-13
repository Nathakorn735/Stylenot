package main.java.org;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Login {
    private static final String USERS_FILE = "resources/json/users.json";

    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void authenticate() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader(USERS_FILE));

            boolean isAuthenticated = false;

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                String storedUsername = (String) jsonObject.get("Username");
                String storedPassword = (String) jsonObject.get("Password");

                if (username.equals(storedUsername) && password.equals(storedPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            if (isAuthenticated) {
                System.out.println("Authentication successful. Welcome, " + username + "!");
            } else {
                System.out.println("Invalid credentials. Authentication failed.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Example usage
        Login login = new Login("Peter", "123456789");
        login.authenticate();
    }
}
