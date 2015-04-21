
import java.awt.Component;
import java.io.File;
import java.nio.file.FileSystem;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Roberto
 */
public class ExploradorTreeCellRenderer implements TreeCellRenderer {

    private JLabel label;

    ExploradorTreeCellRenderer() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        File f = new File((String) o);
        label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
        label.setText(FileSystemView.getFileSystemView().getSystemDisplayName(f));
        return label;
    }
}
