import com.formdev.flatlaf.*;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.sf.jasperreports.engine.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

public class MainPage extends JFrame {

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTable table1;
    public JTextField textField1;
    private JTable table2;
    public JTextField textField2;
    private JButton button2;
    private JButton button1;
    private JPanel pcWidgetsPanel;
    private JPanel g3Panel;
    private JPanel g2Panel;
    private JPanel g4Panel;
    private JPanel vipPanel;
    private JPanel g1Panel;
    private JPanel panel2;
    private JPanel schedulePanel;
    private JPanel digitalClockPanel;
    private JPanel groupsPanelTab;
    private JTable groupsTable;
    private JButton addGroupButton;
    public JTextField searchGroupsTextField;
    private JPanel settingsPanel;

    private void putTabbedPanesClientProperty(String key, Object value) {
        updateTabbedPanesRecur(this, tabbedPane -> tabbedPane.putClientProperty(key, value));
    }

    private void updateTabbedPanesRecur(Container container, Consumer<JTabbedPane> action) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTabbedPane tabPane) {
                action.accept(tabPane);
            }

            if (c instanceof Container)
                updateTabbedPanesRecur((Container) c, action);
        }
    }

    public void searchForTicket(String keyword) throws SQLException {
        ((DefaultTableModel) table1.getModel()).setRowCount(0);
        ResultSet resultSet = Utils.executeQuery(
                keyword != null ? "SELECT * FROM public.tickets WHERE LOWER(name) LIKE '%" + keyword.toLowerCase() + "%' ORDER BY creation_time DESC"
                        : "SELECT * FROM public.tickets ORDER BY creation_time DESC"
        );
        while (resultSet.next()) {

            var id = resultSet.getString("id");
            var name = resultSet.getString("name");
            var startsAt = Utils.convertDateStringToMillis(String.valueOf(resultSet.getTimestamp("starts_at")));
            var duration = resultSet.getInt("duration");
            var endsAt = startsAt + (duration * 60L * 1000);
            var remainingMillis = endsAt - System.currentTimeMillis();
            ((DefaultTableModel) table1.getModel()).addRow(new Object[]{id, name,
                    Utils.convertMillisToTime(startsAt),
                    Utils.convertMillisToTime(endsAt),
                    remainingMillis > 0 ? Utils.convertMillisToHoursMinutesString(remainingMillis) : "FINISHED"});
        }
        Global.selectedTicketIndex = -1;
    }

    public void searchForUserAccount(String keyword) throws SQLException {
        ((DefaultTableModel) table2.getModel()).setRowCount(0);
        ResultSet resultSet = Utils.executeQuery(
                keyword != null ? "SELECT * FROM public.accounts WHERE LOWER(username) LIKE '%" + keyword.toLowerCase() + "%' ORDER BY creation_time DESC"
                        : "SELECT * FROM public.accounts ORDER BY creation_time DESC"
        );
        while (resultSet.next()) {
            var id = resultSet.getString("id");
            var username = resultSet.getString("username");
            var balance = resultSet.getDouble("balance");
            ((DefaultTableModel) table2.getModel()).addRow(

                    new Object[]{id, username,
                            Utils.calculateTime(balance, 8),
                            "₪" + balance});
        }
    }

    public void deleteSelectedTicket() throws SQLException {
        long id = Integer.parseInt(Global.ticketsTable.getValueAt(Global.selectedTicketIndex, 0).toString());
        Utils.executeQuery(String.format("DELETE FROM public.tickets WHERE id = %d", id));
        searchForTicket(textField1.getText());
        Global.schedule.getDataFromDB();
    }

    public void deleteSelectedUserAccount() throws SQLException {
        long id = Integer.parseInt(Global.userAccountsTable.getValueAt(Global.selectedUserAccountIndex, 0).toString());
        Utils.executeQuery(String.format("DELETE FROM public.accounts WHERE id = %d", id));
        searchForUserAccount(textField2.getText());
        Global.schedule.getDataFromDB();
    }

    public void deleteSelectedGroup() throws SQLException {
        long id = Integer.parseInt(Global.groupsTable.getValueAt(Global.selectedGroupIndex, 0).toString());
        Utils.executeQuery(String.format("DELETE FROM public.groups WHERE id = %d", id));
        searchForTicket(searchGroupsTextField.getText());
        Global.schedule.getDataFromDB();
        searchForGroup(searchGroupsTextField.getText());
    }

    public void editSelectedTicket() throws SQLException {
        long id = Integer.parseInt(Global.ticketsTable.getValueAt(Global.selectedTicketIndex, 0).toString());
        new TicketEditor(id).setVisible(true);
    }

    public void editSelectedGroup() throws SQLException {
        long id = Integer.parseInt(Global.groupsTable.getValueAt(Global.selectedGroupIndex, 0).toString());
        new GroupEditor(id).setVisible(true);
    }

    public void editSelectedUserAccount() throws SQLException {
        long id = Integer.parseInt(Global.userAccountsTable.getValueAt(Global.selectedUserAccountIndex, 0).toString());
        new UserAccountEditor(id).setVisible(true);
    }

    public void initTicketsTable() throws SQLException {
        JPopupMenu popupMenu = getJPopupMenu(AppTables.TICKETS_TABLE);
        table1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                Global.selectedTicketIndex = table1.getSelectedRow();
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

                    try {
                        new TicketEditor(Integer
                                .parseInt(table1
                                        .getValueAt(Global.selectedTicketIndex,
                                                0)
                                        .toString()))
                                .setVisible(true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        table1.setRowHeight(30);
        table1.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 20));
        table1.setModel(new DefaultTableModel(null, new String[]{"ID", "Name", "Starts at", "Ends at", "Remaining"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(0).setMaxWidth(80);

        table1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        searchForTicket(null);
    }

    public void initUserAccountsTable() throws SQLException {
        JPopupMenu popupMenu = getJPopupMenu(AppTables.USER_ACCOUNTS_TABLE);
        table2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                Global.selectedTicketIndex = table2.getSelectedRow();
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

                    new UserAccountEditor(Integer
                            .parseInt(table2
                                    .getValueAt(Global.selectedUserAccountIndex,
                                            0)
                                    .toString()))
                            .setVisible(true);

                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        table2.setRowHeight(30);
        table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table2.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 20));
        table2.setModel(new DefaultTableModel(null, new String[]{"ID", "Username", "Time (₪8)", "Balance"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table2.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table2.getColumnModel().getColumn(0).setMaxWidth(80);
        table2.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table2.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Global.selectedUserAccountIndex = table2.getSelectedRow();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        searchForUserAccount(null);
    }

    int delay = 100;
    int differenceFactor = 100;

    public void initPCWidgets() {
        Global.initializePCs();
        GridBagConstraints gbc = new GridBagConstraints();
        g1Panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        for (int i = 0; i < 5; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            var pc = Global.pcs[21 + i];

            g1Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        for (int i = 0; i < 5; i++) {
            gbc.gridx = 1;
            gbc.gridy = i;
            var pc = Global.pcs[20 + 10 - i];
            g1Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        gbc = new GridBagConstraints();
        for (int i = 0; i < 7; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            var pc = Global.pcs[37 - i];
            g2Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        for (int i = 0; i < 7; i++) {
            gbc.gridx = i;
            gbc.gridy = 1;
            var pc = Global.pcs[i + 31 + 7];
            g2Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }

        gbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            gbc.gridx = 1;
            gbc.gridy = i;
            var pc = Global.pcs[49 - i];
            g3Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));
        }
        for (int i = 0; i < 5; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            var pc = Global.pcs[i + 45 + 5];
            g3Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));


        }
        gbc = new GridBagConstraints();
        for (int i = 0; i < 7; i++) {
            gbc.gridx = i;
            gbc.gridy = 1;
            var pc = Global.pcs[54 + 1 + i];
            g4Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        for (int i = 0; i < 7; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            var pc = Global.pcs[54 + 7 + 7 - i];
            g4Panel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        gbc = new GridBagConstraints();
        for (int i = 0; i < 6; i++) {
            gbc.gridx = i;
            gbc.gridy = 1;
            var pc = Global.pcs[68 + 1 + i];
            vipPanel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
        for (int i = 0; i < 6; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            var pc = Global.pcs[68 + 6 + 6 - i];
            vipPanel.add(pc, gbc);
            Utils.fadeInComponent(pc, delay + (i * differenceFactor));

        }
    }

    public MainPage() throws SQLException {
        Global.calculateStaticColor();
        Global.mainPageFrame = this;
        ToolTipManager.sharedInstance().setInitialDelay(0);
        Global.groupsTable = groupsTable;
        Global.ticketsTable = table1;
        Global.userAccountsTable = table2;
        button1.putClientProperty("JButton.buttonType", "square");
        button1.setIcon(Utils.sizedIcon("icons/add.png", 14, UIManager.getColor("Component.accentColor")));
        button1.addActionListener(e -> new TicketCreator().setVisible(true));
        button1.setPreferredSize(new Dimension(32, 24));
        button1.setBackground(Utils.changeAlpha(UIManager.getColor("Component.accentColor"), 50));
        button2.putClientProperty("JButton.buttonType", "square");
        button2.setIcon(Utils.sizedIcon("icons/add.png", 14, UIManager.getColor("Component.accentColor")));
        button2.addActionListener(e -> new UserAccountCreator().setVisible(true));
        button2.setPreferredSize(new Dimension(32, 24));
        button2.setBackground(Utils.changeAlpha(UIManager.getColor("Component.accentColor"), 50));
        textField2.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 15));
        textField2.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    searchForUserAccount(textField2.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        textField1.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 15));


        textField1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    searchForTicket(textField1.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        textField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for Tickets");
        textField2.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for User Accounts");
        textField1.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSearchIcon());
        textField2.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSearchIcon());
        initTicketsTable();
        initUserAccountsTable();
        initGroupsPanelTab();
        initSettingsPanelTab();
        tabbedPane1.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 22));
        tabbedPane1.putClientProperty(TABBED_PANE_TAB_ALIGNMENT, SwingConstants.LEADING);
        tabbedPane1.putClientProperty(TABBED_PANE_TAB_AREA_ALIGNMENT, TABBED_PANE_ALIGN_CENTER);
        tabbedPane1.putClientProperty(TABBED_PANE_TAB_ICON_PLACEMENT, SwingConstants.LEADING);
        tabbedPane1.setIconAt(0, Utils.sizedIcon("icons/dashboard.png", 34));
        tabbedPane1.setIconAt(1, Utils.sizedIcon("icons/calendar.png", 34));
        tabbedPane1.setIconAt(2, Utils.sizedIcon("icons/audience.png", 34));
        tabbedPane1.setIconAt(3, Utils.sizedIcon("icons/gears.png", 34));
        schedulePanel.add(Global.schedule, BorderLayout.CENTER);
        Border lineBorder = BorderFactory.createLineBorder(Color.gray, 2);
        Border marginBorder = new EmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(marginBorder, lineBorder);

        schedulePanel.setBorder(marginBorder);
        var clockWidget = new Clock();

        digitalClockPanel.add(clockWidget);
        setContentPane(panel1);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 0);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 1);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 1);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 1);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 1);
        tabbedPane1.insertTab("", Utils.sizedImage("", 1), new JLabel(""), "", 1);
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.addTab("", new JLabel(""));
        tabbedPane1.setTitleAt(16, "Jehad Hmoudah");
        tabbedPane1.setTitleAt(17, "Database Project");
        for (int i = 0; i < 18; i++) {
            if (i != 6 && i != 7 && i != 8 && i != 9) {
                tabbedPane1.setEnabledAt(i, false);
            }
        }
        tabbedPane1.setDisabledIconAt(0, Utils.sizedImage("icons/SlashLogo.png", 180, 100));
        putTabbedPanesClientProperty(TABBED_PANE_TAB_TYPE, TABBED_PANE_TAB_TYPE_CARD);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Java Project - Jehad Hmoudah");
        setSize(new Dimension(1600, 950));
        initPCWidgets();

        setLocationRelativeTo(null);

        setVisible(true);


    }

    public void searchForGroup(String keyword) throws SQLException {
        ((DefaultTableModel) groupsTable.getModel()).setRowCount(0);
        ResultSet resultSet = Utils.executeQuery(
                keyword != null && !(keyword.isEmpty()) ?
                        "SELECT * \n" +
                                "FROM public.groups \n" +
                                "WHERE LOWER(name) LIKE '%" + keyword.toLowerCase() + "%' \n" +
                                "   OR EXISTS (\n" +
                                "       SELECT 1 \n" +
                                "       FROM UNNEST(mobile_numbers) AS mn \n" +
                                "       WHERE mn LIKE '%" + keyword.toLowerCase() + "%'\n" +
                                "   )\n" +
                                "ORDER BY creation_time DESC;"
                        : "SELECT * FROM public.groups ORDER BY creation_time DESC"
        );
        while (resultSet != null && resultSet.next()) {
            var id = resultSet.getString("id");
            var name = resultSet.getString("name");
            var notes = resultSet.getString("notes");
            var creationTime = Utils.convertMillisToDateTimeString(Utils.convertDateStringToMillis(String.valueOf(resultSet.getTimestamp("creation_time"))));
            var lastVisitTime = Utils.convertMillisToDateTimeString(Utils.convertDateStringToMillis(String.valueOf(resultSet.getTimestamp("last_visit_time"))));
            var mobileNumbers = resultSet.getString("mobile_numbers").replace(",", ", ").replace("{", "").replace("}", "");
            ((DefaultTableModel) groupsTable.getModel()).addRow(new Object[]{
                    id,
                    name,
                    notes.isEmpty() ? "-" : notes,
                    mobileNumbers,
                    creationTime,
                    lastVisitTime});
        }
        Global.selectedGroupIndex = -1;
    }

    public void initGroupsTable() throws SQLException {
        JPopupMenu popupMenu = getJPopupMenu(AppTables.GROUPS_TABLE);
        addGroupButton.putClientProperty("JButton.buttonType", "square");
        addGroupButton.setIcon(Utils.sizedIcon("icons/add.png", 14, UIManager.getColor("Component.accentColor")));
        addGroupButton.setPreferredSize(new Dimension(32, 24));
        addGroupButton.setBackground(Utils.changeAlpha(UIManager.getColor("Component.accentColor"), 50));
        addGroupButton.addActionListener(e -> new GroupCreator().setVisible(true));
        searchGroupsTextField.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 15));
        searchGroupsTextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for Groups");
        searchGroupsTextField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSearchIcon());
        groupsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                Global.selectedGroupIndex = groupsTable.getSelectedRow();
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

                    try {
                        new GroupEditor(Integer
                                .parseInt(groupsTable
                                        .getValueAt(Global.selectedGroupIndex,
                                                0)
                                        .toString()))
                                .setVisible(true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        groupsTable.setRowHeight(30);
        groupsTable.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 20));
        groupsTable.setModel(new DefaultTableModel(null, new String[]{"ID", "Name", "Notes", "Mobile Numbers", "Creation", "Last Visit"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        groupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        groupsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        groupsTable.getColumnModel().getColumn(0).setMaxWidth(80);
        groupsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        groupsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        groupsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        groupsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        searchForGroup(null);
    }

    private JPopupMenu getJPopupMenu(int appTable) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemEdit = new JMenuItem("Edit");
        menuItemEdit.addActionListener(e -> {
            try {
                switch (appTable) {
                    case AppTables.TICKETS_TABLE -> editSelectedTicket();
                    case AppTables.GROUPS_TABLE -> editSelectedGroup();
                    case AppTables.USER_ACCOUNTS_TABLE -> editSelectedUserAccount();
                    default -> {
                    }
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        JMenuItem menuItemDelete = new JMenuItem("Delete");
        menuItemDelete.addActionListener(e -> {
            try {
                switch (appTable) {
                    case AppTables.TICKETS_TABLE -> deleteSelectedTicket();
                    case AppTables.GROUPS_TABLE -> deleteSelectedGroup();
                    case AppTables.USER_ACCOUNTS_TABLE -> deleteSelectedUserAccount();
                    default -> {
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        popupMenu.add(menuItemEdit);
        popupMenu.add(menuItemDelete);
        return popupMenu;
    }

    public void initGroupsPanelTab() throws SQLException {
        initGroupsTable();
        searchGroupsTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    searchForGroup(searchGroupsTextField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void initSettingsPanelTab() {
        new Themes();
        String[] themesArray = Themes.map.keySet().toArray(new String[0]);
        Arrays.sort(themesArray);
        JList<String> themeList = getThemeList(themesArray);
        var themesScrollPane = new JScrollPane(themeList);
        var btnProduceReport = new JButton("Produce a Revenue Report");
        themesScrollPane.add(btnProduceReport);
        btnProduceReport.addActionListener(e -> {
            try {
                String sourceFileName = "jasper_report/Simple_Blue.jrxml";
                JasperReport jasperReport = JasperCompileManager.compileReport(sourceFileName);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, Utils.dbConnection);
                String destinationFileName = "jasper_report/produced_reports/" + "RevenueStatsReport_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
                        + "_T_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm")) + ".pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, destinationFileName);
                System.out.println("PDF Report created successfully.");
                try {
                    if (!Desktop.isDesktopSupported()) {
                        System.out.println("Desktop is not supported");
                        return;
                    }
                    File pdfFile = new File(destinationFileName);
                    if (pdfFile.exists()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        System.out.println("The file does not exist.");
                    }
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
            } catch (Exception exception) {
//                exception.printStackTrace();
            }
        });
        var themesListPanel = new JPanel(new FlowLayout());
        themesListPanel.add(themesScrollPane);
        var gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        var lblThemes = new JLabel(" Themes");
        lblThemes.setFont(new Font("", Font.BOLD, 28));
        settingsPanel.add(lblThemes, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        var lblPCStates = new JLabel("        PC States");
        lblPCStates.setFont(new Font("", Font.BOLD, 28));
        settingsPanel.add(lblPCStates, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsPanel.add(pcsPanel(), gbc);


        gbc.gridx = 1;
        gbc.gridy = 2;
        settingsPanel.add(new JLabel(" "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        var lblShowDonut = new JLabel("        Donut Project");
        lblShowDonut.setFont(new Font("", Font.BOLD, 28));
        settingsPanel.add(lblShowDonut, gbc);




        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        var btnShowDonut = new JButton("Show Donut");
        btnShowDonut.addActionListener(e->{
            new Donut().setVisible(true);
        });
        var btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        btnPanel.add(btnShowDonut);
        settingsPanel.add(btnPanel, gbc);



        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsPanel.add(themesListPanel, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsPanel.add(new JLabel(" "), gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        var lblReporting = new JLabel(" Reporting");
        lblReporting.setFont(new Font("", Font.BOLD, 28));
        settingsPanel.add(lblReporting, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 4;
        var buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonPanel.add(btnProduceReport);
        settingsPanel.add(buttonPanel, gbc);


    }
    private JPanel pcLabelPanel(Color[] colors, String text) {
        var p = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        var pc = new PCWidget("PC");
        pc.rightClickEnabled = false;
        pc.setBackground(colors[0]);
        pc.setForeground(colors[1]);
        pc.setIcon(Utils.sizedIcon("icons/computer.png", 24, colors[1]));
        p.add(pc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        var lbl = new JLabel(text);
        lbl.setFont(new Font("", Font.BOLD, 18));
        p.add(lbl, gbc);
        return p;
    }
    private JPanel pcsPanel(){
        Global.calculateStaticColor();
        var p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 60, 15, 15));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(pcLabelPanel(Global.turnedOffColor, "Turned Off"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        p.add(pcLabelPanel(Global.changingPowerStateColor, "Changing State"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        p.add(pcLabelPanel(Global.turnedOnColor, "Turned On"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(pcLabelPanel(Global.usingTicketColor, "Using Ticket"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        p.add(pcLabelPanel(Global.usingUserAccountColor, "Using User Account"), gbc);

        return p;
    }
    private JList<String> getThemeList(String[] themesArray) {
        JList<String> themeList = new JList<>(themesArray);


        themeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    String themeName = themeList.getSelectedValue();
                    LookAndFeel newLaf = Themes.map.get(themeName);
                    if (newLaf != null) {
                        UIManager.setLookAndFeel(newLaf);
                        SwingUtilities.updateComponentTreeUI(this);
                    }
                    dispose();
                    new MainPage().setVisible(true);

                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
            }
        });
        return themeList;
    }


    public static void setGlobalFont(Font font) {
        var keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Utils.initDBConnection();
        setGlobalFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 17));
        FlatMacLightLaf.setup();

        new MainPage();

    }

}
