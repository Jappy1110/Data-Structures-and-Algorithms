// Represent user information.
public class User {
    private String username;
    private String password;
    private int remainingTime;

    public User(String username, String password, int remainingTime) {
        this.username = username;
        this.password = password;
        this.remainingTime = remainingTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
