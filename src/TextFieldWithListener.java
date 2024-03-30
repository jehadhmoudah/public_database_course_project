import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class TextFieldWithListener extends JTextField implements KeyListener {

    public TextFieldWithListener () {
        this.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    abstract public void keyReleased(KeyEvent e);
}
