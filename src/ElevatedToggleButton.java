import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ElevatedToggleButton extends JToggleButton implements ActionListener {

    public ElevatedToggleButton(String text) {
        super(text);
        this.setFocusPainted(false);
        this.addActionListener(this);
        this.setFont(new Font("Ubuntu", Font.PLAIN, 15));
    }

    abstract public void onPressed();
    @Override
    public void actionPerformed(ActionEvent e) {
        this.onPressed();
    }
}
