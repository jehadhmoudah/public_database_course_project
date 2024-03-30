import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockPanel extends JPanel {

    private final JLabel timeLabel;
    private final Timer timer;

    public ClockPanel() {
        // Initialize the label
        timeLabel = new JLabel();
        timeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        timeLabel.setFont(new Font("digital-7", Font.BOLD, 36));
        add(timeLabel);

        // Timer to update the time
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTime();
            }
        });
        startClock();
    }

    public void startClock() {
        timer.start();
    }

    private void updateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   H:mm a");
        String time = format.format(new Date());
        timeLabel.setText(time);
    }
}
