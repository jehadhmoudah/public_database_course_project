import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserAccountCreator extends JFrame {
    private JTextField txtUsername, txtPassword, txtConfirmPassword;
    private JSpinner spnrBalance;
    private JButton createButton;

    public UserAccountCreator() {
        setTitle("New User Account");
        setPreferredSize(new Dimension(500, 390));
        setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new FlowLayout());
        var label = new JLabel(" New User Account");
        label.setIcon(Utils.sizedIcon("icons/account.png", 48));
        label.setFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 30));
        textPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        textPanel.add(label);
        add(textPanel, BorderLayout.NORTH);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password"), gbc);
        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Confirm Password"), gbc);
        txtConfirmPassword = new JPasswordField(15);
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtConfirmPassword, gbc);

        // Balance
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Balance"), gbc);
        spnrBalance = new JSpinner(new SpinnerNumberModel(8, 8, 1000, 0.5));
        spnrBalance.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(spnrBalance, gbc);


        // Create button
        createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            try {
                createUserAccount();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(new JLabel(" "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createButton, gbc);
        var p = new JPanel();
        p.add(panel);
        p.setBorder(new EmptyBorder(10, 10, 100, 10));
        add(p, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }
    public boolean isUsernameTaken() throws SQLException {
        String checkUserSql = String.format(
                "SELECT COUNT(*) AS user_count FROM public.accounts WHERE username = '%s'",
                txtUsername.getText().trim());

        ResultSet resultSet = Utils.executeQuery(checkUserSql);
        if (resultSet.next()) {
            return resultSet.getInt("user_count") > 0;
        }
        return false;
    }

    void createUserAccount() throws SQLException {
        if (txtUsername.getText().trim().isEmpty()) return;
        if (isUsernameTaken()) return;
        if (txtPassword.getText().trim().isEmpty()) return;
        if (txtConfirmPassword.getText().trim().isEmpty()) return;
        // Prepare the SQL query
        String sql = "INSERT INTO public.accounts (username, password, creation_time, last_sign_in_time, balance) VALUES ('%s', '%s', '%s', '%s', %f)";

        // Hash the password - implement this method according to your security requirements
        String hashedPassword = Utils.hashPassword(txtPassword.getText());

        // Execute the SQL query
        Utils.executeQuery(String.format(sql,
                txtUsername.getText(),
                hashedPassword,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Float.parseFloat(spnrBalance.getValue().toString()))); // Assuming starting balance is 0
        Global.mainPageFrame.searchForUserAccount(Global.mainPageFrame.textField2.getText());
        dispose();
    }
}
