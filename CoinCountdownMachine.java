import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

interface CountdownMachineADT {
    void addCoin(int coin);  
    void startCountdown();
    void stopCountdown();
    void resetTimer();
}
// Manage the countdown functionality.

public class CoinCountdownMachine implements CountdownMachineADT {
    private int totalTimeInSeconds;
    private Timer timer;
    private boolean isRunning;
    private JTextArea textArea;

    public CoinCountdownMachine(JTextArea textArea) {
        this.totalTimeInSeconds = 0;
        this.isRunning = false;
        this.textArea = textArea;
    }

    @Override
    public void addCoin(int coin) {
        switch (coin) {
            case 10:
                totalTimeInSeconds += 60; // 10 peso coin adds 1 minute
                break;
            case 5:
                totalTimeInSeconds += 30; // 5 peso coin adds 30 seconds
                break;
            case 1:
                totalTimeInSeconds += 6;  // 1 peso coin adds 6 seconds
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid coin. Please insert a 1, 5, or 10 peso coin.");
                return;
        }
        textArea.append("Added " + coin + " peso coin. Total time: " + totalTimeInSeconds + " seconds.\n");
        if (!isRunning) {
            startCountdown();
        }
    }

    @Override
    public void startCountdown() {
        if (isRunning) {
            return;
        }

        if (totalTimeInSeconds == 0) {
            JOptionPane.showMessageDialog(null, "No time available. Please add coins.");
            return;
        }

        isRunning = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (totalTimeInSeconds <= 0) {
                    textArea.append("Time is up!\n");
                    JOptionPane.showMessageDialog(null, "Time is up!");
                    stopCountdown();
                } else {
                    if (totalTimeInSeconds == 4) {
                        JOptionPane.showMessageDialog(null, "Warning: Only 4 seconds left!");
                    }
                    textArea.append("Time remaining: " + totalTimeInSeconds + " seconds.\n");
                    totalTimeInSeconds--;
                }
            }
        }, 0, 1000);
    }

    @Override
    public void stopCountdown() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        textArea.append("Countdown stopped.\n");
    }

    @Override
    public void resetTimer() {
        stopCountdown();
        totalTimeInSeconds = 0;
        textArea.setText("Timer reset.\n");
    }

    public int getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(int time) {
        this.totalTimeInSeconds = time;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
