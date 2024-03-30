import javax.swing.*;
import java.awt.*;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class GroupEditor extends JFrame {

    private JLabel lblGroupName, lblNotes, lblMobileNumbers;
    private JTextField txtGroupName;
    private JTextArea txtNotes, txtMobileNumbers;
    private JButton createButton;
    long ticketId;
    public GroupEditor(long ticketId) throws SQLException {
        this.ticketId = ticketId;
        setTitle("Edit Group");
        setPreferredSize(new Dimension(500, 450));
        setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new FlowLayout());
        var label = new JLabel(" Edit Group");
        label.setIcon(Utils.sizedIcon("icons/audience.png", 48));
        label.setFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 30));
        textPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        textPanel.add(label);
        add(textPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Initialize member variables
        lblGroupName = new JLabel("Group Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblGroupName, gbc);


        lblNotes = new JLabel("Notes");
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridy = 2;
        panel.add(lblNotes, gbc);


        lblMobileNumbers = new JLabel("Mobile Numbers");
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridy = 3;
        panel.add(lblMobileNumbers, gbc);


        txtGroupName = new JTextField(20);
        txtGroupName.setText("Group_" + Utils.randomAlphaNumeric(5));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtGroupName, gbc);


        txtNotes = new JTextArea(3, 20);
        gbc.gridy = 2;
        panel.add(new JScrollPane(txtNotes), gbc);

        txtMobileNumbers = new PlaceholderTextArea("Mobile numbers separated by commas", 3, 20);
        gbc.gridy = 3;
        panel.add(new JScrollPane(txtMobileNumbers), gbc);

        createButton = new JButton("Update");
        createButton.addActionListener(e -> {
            try {
                editGroup();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createButton, gbc);

        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 30, 8));
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        initFields(ticketId);

    }
    String name;
    String notes;
    Array mobile_numbers;
    public void initFields(long ticketId) throws SQLException {
        ResultSet resultSet = Utils.executeQuery(String.format("SELECT * FROM public.groups WHERE id = %d", ticketId));
        if (resultSet.next()) {
            name = resultSet.getString("name");
            notes = resultSet.getString("notes");
            mobile_numbers = resultSet.getArray("mobile_numbers");
            txtGroupName.setText(name);
            txtNotes.setText(notes);
            txtMobileNumbers.setText(mobile_numbers.toString().replace("{", "").replace("}", "").replace(",",", "));
        }

    }

    void editGroup() throws SQLException {

        Utils.executeQuery(String.format("UPDATE public.groups SET name = '%s', notes = '%s', mobile_numbers = '%s' WHERE id = %d",
                txtGroupName.getText(),
                txtNotes.getText(),
                "{"+ txtMobileNumbers.getText() + "}",
                ticketId
        ));
        Global.mainPageFrame.searchForGroup(Global.mainPageFrame.searchGroupsTextField.getText());
        Global.schedule.getDataFromDB();
        dispose();


    }


}
