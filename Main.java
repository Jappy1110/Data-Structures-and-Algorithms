import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

// Handle the user interface and application logic.
public class Main {
    private static Map<String, User> userDatabase = new HashMap<>();
    private static final String USER_DATA_FILE = "userData.txt";
    private static User currentUser = null;
    private static JTextArea textArea;
    private static CoinCountdownMachine countdownMachine;
    private static JFrame mainFrame;

    public static void main(String[] args) {
        loadUserData();
        showLoginDialog();
    }

    private static void showLoginDialog() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        int option = JOptionPane.showOptionDialog(null, loginPanel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new String[]{"Login", "Create Account"}, "Login");

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username or password cannot be empty.");
                showLoginDialog();
            } else {
                if (userDatabase.containsKey(username)) {
                    User user = userDatabase.get(username);
                    if (user.getPassword().equals(password)) {
                        currentUser = user;
                        setupGUI();
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect password.");
                        showLoginDialog();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "User not found. Please create an account.");
                    showLoginDialog();
                }
            }
        } else if (option == 1) { // Create Account
            createAccount();
        } else {
            System.exit(0);
        }
    }

    private static void createAccount() {
        JPanel registerPanel = new JPanel(new GridLayout(2, 2));
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(usernameField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, registerPanel, "Create Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username or password cannot be empty.");
                createAccount();
            } else if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                createAccount();
            } else {
                User newUser = new User(username, password, 0);
                userDatabase.put(username, newUser);
                saveUserData();
                JOptionPane.showMessageDialog(null, "Account created successfully! Please log in.");
                showLoginDialog();
            }
        } else {
            showLoginDialog();
        }
    }

    private static void setupGUI() {
        mainFrame = new JFrame("Coin Countdown Machine");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 600);

        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        countdownMachine = new CoinCountdownMachine(textArea);
        countdownMachine.setTotalTimeInSeconds(currentUser.getRemainingTime());
        textArea.append("Welcome, " + currentUser.getUsername() + "! Remaining time: " + currentUser.getRemainingTime() + " seconds.\n");

        JButton addTenPeso = new JButton("Insert 10 Peso");
        JButton addFivePeso = new JButton("Insert 5 Peso");
        JButton addOnePeso = new JButton("Insert 1 Peso");
        JButton continueButton = new JButton("Resume Time");
        JButton resetButton = new JButton("Reset Timer");
        JButton logoutButton = new JButton("Log Out");

        addTenPeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                countdownMachine.addCoin(10);
            }
        });

        addFivePeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                countdownMachine.addCoin(5);
            }
        });

        addOnePeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                countdownMachine.addCoin(1);
            }
        });

        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!countdownMachine.isRunning()) {  // Check if the countdown is not running
                    countdownMachine.startCountdown();  // Resume the countdown
                } else {
                    JOptionPane.showMessageDialog(null, "Countdown is already running.");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                countdownMachine.resetTimer();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 5, 5));
        buttonPanel.add(addTenPeso);
        buttonPanel.add(addFivePeso);
        buttonPanel.add(addOnePeso);
        buttonPanel.add(continueButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(logoutButton);

        mainFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        mainFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
        JOptionPane.showMessageDialog(null, "Welcome to the Coin Countdown Machine!");
    }

    private static void logout() {
        currentUser.setRemainingTime(countdownMachine.getTotalTimeInSeconds());
        saveUserData();
        countdownMachine.stopCountdown();
        mainFrame.dispose();
        JOptionPane.showMessageDialog(null, "Logged out successfully!");
        showLoginDialog();
    }

    private static void loadUserData() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    int remainingTime = Integer.parseInt(parts[2]);
                    userDatabase.put(username, new User(username, password, remainingTime));
                }
            }
        } catch (IOException e) {
            System.out.println("No user data found. Starting fresh.");
        }
    }

    private static void saveUserData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_DATA_FILE))) {
            for (User user : userDatabase.values()) {
                writer.println(user.getUsername() + "," + user.getPassword() + "," + user.getRemainingTime());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save user data.");
        }
    }
}
