import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public abstract class ListWithListener<E> extends JList<E> implements ListSelectionListener {
    public ListWithListener(DefaultListModel<E> listModel) {
        super(listModel);
        this.addListSelectionListener(this);
    }

    @Override
    public abstract void valueChanged(ListSelectionEvent e);
}
