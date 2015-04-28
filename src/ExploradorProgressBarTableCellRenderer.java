
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Roberto
 */
public class ExploradorProgressBarTableCellRenderer extends DefaultTableCellRenderer {

    JLabel label = new JLabel();
    JProgressBar bar = new JProgressBar();

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        //label.setIcon((ImageIcon) obj);

        if (obj instanceof Integer) {
            Integer n = (Integer) obj;
            bar.setValue(n);
            return bar;
        } else {
            label.setText((String) obj);
            return label;
        }

    }
}
