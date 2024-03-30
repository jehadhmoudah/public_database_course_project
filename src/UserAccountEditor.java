import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class UserAccountEditor extends JFrame {
    private long accountId;
    private JTextField txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JSpinner spnrBalance;
    private JButton updateButton;
    private String oldName;

    public UserAccountEditor(long accountId) {
        // Store the account ID
        this.accountId = accountId;

        setTitle("Edit User Account");
        setPreferredSize(new Dimension(500, 390));
        setLayout(new BorderLayout());

        // Rest of the setup code...
        JPanel panel = setupPanel();
        add(panel, BorderLayout.CENTER);

        // Load the user account data
        loadUserAccountData();

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel setupPanel() {
        setTitle("Edit User Account");
        setPreferredSize(new Dimension(500, 390));
        setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new FlowLayout());
        var label = new JLabel(" Edit User Account");
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
        spnrBalance = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.5));
        spnrBalance.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(spnrBalance, gbc);


        // Create button
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            try {
                updateUserAccount();
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
        panel.add(updateButton, gbc);
        var p = new JPanel();
        p.add(panel);
        p.setBorder(new EmptyBorder(10, 10, 100, 10));
        add(p, BorderLayout.CENTER);

        return p;
    }

    private void loadUserAccountData() {
        String fetchUserSql = String.format("SELECT username, balance FROM public.accounts WHERE id = %d", this.accountId);
        try {
            ResultSet resultSet = Utils.executeQuery(fetchUserSql);
            if (resultSet.next()) {
                oldName = resultSet.getString("username");
                txtUsername.setText(oldName);
                spnrBalance.setValue(resultSet.getDouble("balance"));
            }
            resultSet.close(); // Don't forget to close the ResultSet
        } catch (SQLException ex) {
//            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading account data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void updateUserAccount() throws SQLException {
        if (!validateInput()) {
            return; // Validation failed
        }

        // Start with the basic SQL statement for updating username and balance
        StringBuilder updateSql = new StringBuilder(
                "UPDATE public.accounts SET username = '" + txtUsername.getText() + "', balance = " + spnrBalance.getValue());

        // Check if the password field is not empty
        if (txtPassword.getPassword().length > 0) {
            // Both password fields are filled out and match
            if (Arrays.equals(txtPassword.getPassword(), txtConfirmPassword.getPassword())) {
                // Hash the password
                String hashedPassword = Utils.hashPassword(new String(txtPassword.getPassword()));
                // Add password to the SQL statement
                updateSql.append(", password = '").append(hashedPassword).append("'");
            } else {
                // If passwords do not match, show a message and do not update the password
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Finish the SQL statement
        updateSql.append(" WHERE id = ").append(this.accountId);

        // Execute the update
        Utils.executeQuery(updateSql.toString()); // Implement Utils.executeUpdate() for update operations
        Global.mainPageFrame.searchForUserAccount(Global.mainPageFrame.textField2.getText());
        dispose();
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
    private boolean validateInput() throws SQLException {
        String username = txtUsername.getText();
        if (username.trim().isEmpty()) return false;
        if (!Objects.equals(oldName, username)) {

            return !isUsernameTaken();
        }
        return true;
    }
}
