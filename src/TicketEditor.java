import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class TicketEditor extends JFrame {

    // Member variables
    private JLabel lblTicketName, lblRoom, lblPlayers, lblStart, lblDuration, lblSchedule;
    private JTextField txtTicketName;
    private JComboBox<String> cmbRoom, cmbPlayers, cmbDuration, cmbSchedule, cmbStart;
    private JButton createButton;
    long ticketId;

    public TicketEditor(long ticketId) throws SQLException {
        this.ticketId = ticketId;
        setTitle("Edit Ticket");
        setPreferredSize(new Dimension(500, 450));
        setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new FlowLayout());
        var label = new JLabel(" Edit Ticket");
        label.setIcon(Utils.sizedIcon("icons/tickets.png", 48));
        label.setFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 30));
        textPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        textPanel.add(label);
        add(textPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Initialize member variables
        lblTicketName = new JLabel("Ticket Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblTicketName, gbc);

        lblRoom = new JLabel("Room");
        gbc.gridy = 2;
        panel.add(lblRoom, gbc);

        lblPlayers = new JLabel("Players");
        gbc.gridy = 3;
        panel.add(lblPlayers, gbc);

        lblStart = new JLabel("Start");
        gbc.gridy = 4;
        panel.add(lblStart, gbc);

        lblDuration = new JLabel("Duration");
        gbc.gridy = 5;
        panel.add(lblDuration, gbc);

        lblSchedule = new JLabel("Schedule");
        gbc.gridy = 6;
        panel.add(lblSchedule, gbc);

        txtTicketName = new JTextField(20);
        txtTicketName.setText("Ticket_" + Utils.randomAlphaNumeric(5));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtTicketName, gbc);

        cmbRoom = new JComboBox<>(new String[]{"G1", "G2", "G3", "G4", "VIP"});
        gbc.gridy = 2;
        panel.add(cmbRoom, gbc);

        String[] numbers = new String[65];
        for (int i = 0; i < 64; i++) {
            numbers[i] = Integer.toString(i + 1);
        }
        cmbPlayers = new JComboBox<>(numbers);
        gbc.gridy = 3;
        panel.add(cmbPlayers, gbc);

//        ArrayList<String> startingTime = new ArrayList<>();
//        var now = LocalDateTime.now();
//        var startTime = now.minusHours(2);
//        for (int i = 0; i < 1000; i += 10) {
//            startingTime.add(Utils.convertLocalDateTimeToPostgresTimeWithTimezone(startTime.plusMinutes(i).toString()));
//        }
        cmbStart = new JComboBox<>();
//        for (String item : startingTime) {
////            cmbDuration.addItem(" "+item);
//            cmbStart.addItem(item);
//        }
        cmbStart.setEditable(true);
//        cmbStart.setSelectedItem(Utils.convertLocalDateTimeToPostgresTimeWithTimezone(now.toString()));
        gbc.gridy = 4;
        panel.add(cmbStart, gbc);

        ArrayList<String> durations = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 50; j += 10) {
                if (i == 0 && j < 50) {continue;}
                String minutes = j < 10 ? "0" + j : "" + j;
                String hours = i < 10 ? "0" + i : "" + i;
                durations.add(hours + ":" + minutes);
            }
        }
        cmbDuration = new JComboBox<>();
//        cmbDuration.setEditable(true);
        for (String item : durations) {
//            cmbDuration.addItem(" "+item);
            cmbDuration.addItem(item);
        }
        gbc.gridy = 5;
        panel.add(cmbDuration, gbc);

        cmbSchedule = new JComboBox<>(new String[]{"No", "Yes"});
        gbc.gridy = 6;
        panel.add(cmbSchedule, gbc);

        createButton = new JButton("Update");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateTicket();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        initFields(ticketId);
        panel.add(createButton, gbc);

        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 30, 8));
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    String name;
    LocalDateTime startsAt;
    LocalDateTime creationTime;
    int duration;
    String room;
    boolean schedule = false;

    public void initFields(long ticketId) throws SQLException {
        ResultSet resultSet = Utils.executeQuery(String.format("SELECT * FROM public.tickets WHERE id = %d", ticketId));
        if (resultSet.next()) {
            name = resultSet.getString("name");
            startsAt = Utils.convertStringToLocalDateTime(String.valueOf(resultSet.getString("starts_at")));
            creationTime = Utils.convertStringToLocalDateTime(String.valueOf(resultSet.getString("creation_time")));
            duration = resultSet.getInt("duration");
            room = resultSet.getString("room");
            schedule = resultSet.getBoolean("schedule");
            if (schedule) cmbSchedule.setSelectedItem("Yes");
            txtTicketName.setText(name);
            cmbRoom.setSelectedItem(room);
            cmbDuration.setSelectedItem(Utils.convertMinutesToHoursMinutesString(duration));
            cmbPlayers.setSelectedItem(Integer.toString(resultSet.getInt("players")));
            ArrayList<String> startingTime = new ArrayList<>();
            var now = startsAt;
            var startTime = now.minusHours(2);
            for (int i = 0; i < 1000; i += 10) {
                startingTime.add(Utils.convertLocalDateTimeToPostgresTimeWithTimezone(startTime.plusMinutes(i).toString()));
            }
            for (String item : startingTime) {
                cmbStart.addItem(item);
            }
            cmbStart.setSelectedItem(Utils.convertLocalDateTimeToPostgresTimeWithTimezone(now.toString()));
        }
    }

    void updateTicket() throws SQLException {
        schedule = false;
        schedule = cmbSchedule.getSelectedItem() == "Yes";
        Utils.executeQuery(String.format("UPDATE public.tickets SET name = '%s', room = '%s', starts_at = '%s', duration = %d, schedule = %b, players = %d WHERE id = %d",
                txtTicketName.getText(),
                cmbRoom.getSelectedItem(),
                cmbStart.getSelectedItem(),
                Utils.convertTimeToMinutes(cmbDuration.getSelectedItem().toString()),
                schedule, Integer.parseInt(Objects.requireNonNull(cmbPlayers.getSelectedItem()).toString()),
                ticketId
        ));
        Global.mainPageFrame.searchForTicket(Global.mainPageFrame.textField1.getText());

        Global.schedule.getDataFromDB();
        dispose();
    }

}
