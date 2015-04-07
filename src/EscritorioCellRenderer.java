
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Roberto
 */
public class EscritorioCellRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
            File file = (File) value;
            setText(FileSystemView.getFileSystemView().getSystemDisplayName(file));
            setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setPreferredSize(new Dimension(150, 40));
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);

        return this;

    }
}
