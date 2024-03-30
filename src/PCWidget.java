import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PCWidget extends JButton implements ActionListener, MouseListener {
    Map<String, JMenuItem> menuItems = new HashMap<>();
    private long startedAt;
    private String name;
    private int state;
    private String ip;
    private int occupant = Occupant.NONE;
    private int occupantId = -1;
    public Color[] turnedOffColor = new Color[]{Utils.alphaBlend(UIManager.getColor("Button.background"), UIManager.getColor("Component.accentColor"), 0.25f), UIManager.getColor("Component.foreground")};
    public Color[] changingPowerStateColor = new Color[]{Utils.alphaBlend(new Color(0, 47, 12), UIManager.getColor("Component.accentColor"), 0.25f), Utils.alphaBlend(new Color(0, 175, 40), UIManager.getColor("Component.accentColor"), 0.40f)};
    private final Color[] usingTicketColor = new Color[]{new Color(255, 128, 0), new Color(91, 30, 0)};
    private final Color[] usingUserAccountColor = new Color[]{new Color(253, 0, 0), new Color(91, 0, 0)};
    private final Color[] turnedOnColor = new Color[]{new Color(0, 255, 55), new Color(0, 66, 16)};
    public boolean rightClickEnabled = true;

    public PCWidget(String name) {
        super(name);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        putClientProperty("JButton.buttonType", "round");
        initMenuItems();
        addMouseListener(new MouseAdapter() {
            final JPopupMenu popupMenu = new JPopupMenu();

            @Override
            public void mouseEntered(MouseEvent e) {
                if (state == PCState.USING_TICKET) {
                    ResultSet resultSet = Utils.executeQuery("SELECT * FROM public.tickets WHERE id ='" + occupantId + "'"
                    );
                    String name = "";
                    long startsAt;
                    int duration;
                    long endsAt = 0;
                    long remainingMillis = 0;
                    while (true) {
                        try {
                            if (!resultSet.next()) break;
                            name = resultSet.getString("name");
                            startsAt = Utils.convertDateStringToMillis(String.valueOf(resultSet.getTimestamp("starts_at")));
                            duration = resultSet.getInt("duration");
                            endsAt = startsAt + (duration * 60L * 1000);
                            remainingMillis = endsAt - System.currentTimeMillis();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    var menuItem = new JMenuItem("Ticket:" + "     " + name);
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Started at:" + "       " + Utils.convertMillisToTime(startedAt));
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Ends at:" + "            " + Utils.convertMillisToTime(endsAt));
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Remaining:" + "      " + (remainingMillis < 0 ? "FINISHED" : Utils.convertMillisToHoursMinutesString(remainingMillis)));
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    popupMenu.show(e.getComponent(), e.getX() - 20, e.getY() + 60);
                }
                else if (state == PCState.USING_USER_ACCOUNT) {
                    ResultSet resultSet = Utils.executeQuery("SELECT * FROM public.accounts WHERE id ='" + occupantId + "'"
                    );
                    String name = "";
                    float balance = 0;
                    while (true) {
                        try {
                            if (!resultSet.next()) break;
                            name = resultSet.getString("username");
                            balance = resultSet.getFloat("balance");
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    var menuItem = new JMenuItem("Username:" + "     " + name);
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Started at:" + "       " + Utils.convertMillisToTime(startedAt));
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Duration:" + "          " + Utils.calculateTime(balance, 8));
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    menuItem = new JMenuItem("Balance:" + "          " + "₪" + balance);
                    menuItem.setEnabled(false);
                    popupMenu.add(menuItem);
                    popupMenu.show(e.getComponent(), e.getX() - 20, e.getY() + 60);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                popupMenu.removeAll();
                popupMenu.setVisible(false);
            }
        });
        addMouseListener(this);
        addActionListener(this);
        setName(name);
        setMargin(new Insets(0, 0, 0, 0));
        setFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 12));
        setBackground(turnedOffColor[0]);
        setForeground(turnedOffColor[1]);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, turnedOffColor[1]));
        setPreferredSize(new Dimension(60, 60));
    }

    public void initMenuItems() {
        var menuItem = new JMenuItem("Wake on LAN");
        menuItem.addActionListener(event -> turnOn());
        menuItems.put("wakeOnLan", menuItem);
        menuItem = new JMenuItem("Turn Off");
        menuItem.addActionListener(event -> turnOff());
        menuItems.put("turnOff", menuItem);
        menuItem = new JMenuItem("Use Selected Ticket");
        menuItem.addActionListener(event -> useSelectedTicket());
        menuItems.put("useSelectedTicket", menuItem);
        menuItem = new JMenuItem("Use Selected User Account");
        menuItem.addActionListener(event -> useSelectedUserAccount());
        menuItems.put("useSelectedUserAccount", menuItem);
        menuItem = new JMenuItem("Remove Occupant");
        menuItem.addActionListener(event -> removeOccupant());
        menuItems.put("removeOccupant", menuItem);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public int getOccupant() {
        return occupant;
    }

    public void setOccupant(int occupant) {
        this.occupant = occupant;
    }


    public int getOccupantId() {
        return occupantId;
    }

    public void setOccupantId(int occupantId) {
        this.occupantId = occupantId;
    }


    @Override
    public void actionPerformed(ActionEvent event) {

    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
            if (occupant == Occupant.TICKET) {
                try {
                    new TicketEditor(occupantId).setVisible(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if (occupant == Occupant.USER_ACCOUNT){
                new UserAccountEditor(occupantId).setVisible(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    public void turnOn() {
        setBackground(changingPowerStateColor[0]);
        setForeground(changingPowerStateColor[1]);
        Utils.fadeInComponent(this, 500);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, changingPowerStateColor[1]));
        setState(PCState.CHANGING_POWER_STATE);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
                setBackground(turnedOnColor[0]);
                setForeground(turnedOnColor[1]);
                Utils.fadeInComponent(this, 500);
                setIcon(Utils.sizedIcon("icons/computer.png", 24, turnedOnColor[1]));
                setState(PCState.TURNED_ON);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 0;
        }, executor);
        executor.shutdown();
    }

    public void useSelectedTicket() {
        startedAt = System.currentTimeMillis();
        int id = Integer.parseInt((String) Global.ticketsTable.getValueAt(Global.selectedTicketIndex, 0));
        setBackground(usingTicketColor[0]);
        setForeground(usingTicketColor[1]);
        Utils.fadeInComponent(this, 500);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, usingTicketColor[1]));
        occupant = Occupant.TICKET;
        occupantId = id;
        setState(PCState.USING_TICKET);
    }

    public void useSelectedUserAccount() {
        startedAt = System.currentTimeMillis();
        int id = Integer.parseInt((String) Global.userAccountsTable.getValueAt(Global.selectedUserAccountIndex, 0));
        setBackground(usingUserAccountColor[0]);
        setForeground(usingUserAccountColor[1]);
        Utils.fadeInComponent(this, 500);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, usingUserAccountColor[1]));
        occupant = Occupant.USER_ACCOUNT;
        occupantId = id;
        setState(PCState.USING_USER_ACCOUNT);
    }

    public void removeOccupant() {
        setBackground(turnedOnColor[0]);
        setForeground(turnedOnColor[1]);
        Utils.fadeInComponent(this, 500);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, turnedOnColor[1]));
        occupant = Occupant.NONE;
        occupantId = -1;
        setState(PCState.TURNED_ON);
    }

    public void turnOff() {
        occupantId = -1;
        occupant = Occupant.NONE;
        setBackground(changingPowerStateColor[0]);
        setForeground(changingPowerStateColor[1]);
        Utils.fadeInComponent(this, 500);
        setIcon(Utils.sizedIcon("icons/computer.png", 24, changingPowerStateColor[1]));
        setState(PCState.CHANGING_POWER_STATE);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
                setBackground(turnedOffColor[0]);
                setForeground(turnedOffColor[1]);
                Utils.fadeInComponent(this, 500);
                setIcon(Utils.sizedIcon("icons/computer.png", 24, turnedOffColor[1]));
                setState(PCState.TURNED_OFF);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 0;
        }, executor);
        executor.shutdown();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        if (Global.selectedTicketIndex >= 0 && state == PCState.TURNED_ON) {
            popupMenu.add(menuItems.get("useSelectedTicket"));
        }
        if (Global.selectedUserAccountIndex >= 0 && state == PCState.TURNED_ON) {
            popupMenu.add(menuItems.get("useSelectedUserAccount"));
        }
        switch (state) {
            case PCState.TURNED_OFF:
                popupMenu.add(menuItems.get("wakeOnLan"));
                break;
            case PCState.TURNED_ON:
                popupMenu.add(menuItems.get("turnOff"));
                break;
            case PCState.USING_TICKET, PCState.USING_USER_ACCOUNT:
                popupMenu.add(menuItems.get("removeOccupant"));
                popupMenu.add(menuItems.get("turnOff"));
                break;
            case PCState.CHANGING_POWER_STATE:
            default:

        }
        if (e.isPopupTrigger() && rightClickEnabled) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
