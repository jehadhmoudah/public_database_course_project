import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

abstract public class ElevatedButton extends JButton implements ActionListener{
    public ElevatedButton(String text) {
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
