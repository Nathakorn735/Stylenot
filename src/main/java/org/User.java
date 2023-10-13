package main.java.org;

public class User {
    private String userId;
    private String username;
    private String password;
    private String empId;
    private String empName;

    public User(String userId, String username, String password, String empId, String empName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.empId = empId;
        this.empName = empName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmpId() {
        return empId;
    }

    public String getEmpName() {
        return empName;
    }
}
