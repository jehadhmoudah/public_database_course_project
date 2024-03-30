import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class Global {

    Global() {

    }

    public static ReservationsSchedule schedule;

    static {
        try {
            schedule = new ReservationsSchedule(new ArrayList<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Color[] turnedOffColor = new Color[]{Utils.alphaBlend(UIManager.getColor("Button.background"), UIManager.getColor("Component.accentColor"), 0.25f), UIManager.getColor("Component.foreground")};
    public static Color[] changingPowerStateColor = new Color[]{Utils.alphaBlend(new Color(0, 47, 12), UIManager.getColor("Component.accentColor"), 0.25f), Utils.alphaBlend(new Color(0, 175, 40), UIManager.getColor("Component.accentColor"), 0.40f)};
    public static Color[] usingTicketColor = new Color[]{new Color(255, 128, 0), new Color(91, 30, 0)};
    public static Color[] usingUserAccountColor = new Color[]{new Color(253, 0, 0), new Color(91, 0, 0)};
    public static Color[] turnedOnColor = new Color[]{new Color(0, 255, 55), new Color(0, 66, 16)};

    public static PCWidget[] pcs = new PCWidget[100];
    public static boolean pcsInitialized = false;

    public static void initializePCs() {

            for (int i = 0; i < pcs.length; i++) {
                if (!pcsInitialized) {
                    pcs[i] = new PCWidget("PC" + i);
                }
                pcs[i].turnedOffColor = Global.turnedOffColor;
                pcs[i].changingPowerStateColor = Global.changingPowerStateColor;
                if (pcs[i].getState() == PCState.TURNED_OFF) {
                    pcs[i].setBackground(Global.turnedOffColor[0]);
                    pcs[i].setForeground(Global.turnedOffColor[1]);
                    pcs[i].setIcon(Utils.sizedIcon("icons/computer.png", 24, turnedOffColor[1]));
                }

                pcs[i].initMenuItems();
            }
            if (!pcsInitialized) {
                pcsInitialized = true;
            }
    }

    public static void calculateStaticColor() {
        turnedOffColor = new Color[]{Utils.alphaBlend(UIManager.getColor("Button.background"), UIManager.getColor("Component.accentColor"), 0.25f), UIManager.getColor("Component.foreground")};
        changingPowerStateColor = new Color[]{Utils.alphaBlend(new Color(0, 47, 12), UIManager.getColor("Component.accentColor"), 0.25f), Utils.alphaBlend(new Color(0, 175, 40), UIManager.getColor("Component.accentColor"), 0.40f)};
        usingTicketColor = new Color[]{new Color(255, 128, 0), new Color(91, 30, 0)};
        usingUserAccountColor = new Color[]{new Color(253, 0, 0), new Color(91, 0, 0)};
        turnedOnColor = new Color[]{new Color(0, 255, 55), new Color(0, 66, 16)};
    }

    public static MainPage mainPageFrame;
    public static int selectedTicketIndex = -1;
    public static int selectedUserAccountIndex = -1;
    public static int selectedGroupIndex = -1;
    public static JTable ticketsTable;
    public static JTable groupsTable;
    public static JTable userAccountsTable;
}
