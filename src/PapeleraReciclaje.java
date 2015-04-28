
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Roberto
 */
public class PapeleraReciclaje extends javax.swing.JInternalFrame {
    File papelera = (new File("C:/$Recycle.Bin").listFiles()[0]);
    
    FileSystemView fsv = FileSystemView.getFileSystemView();
    Vector<File> archivosActuales = new Vector();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    File archivoSeleccionado;
    /**
     * Creates new form PapeleraReciclaje
     */
    public PapeleraReciclaje() {
        
        final JPopupMenu clickDerechoMenu = new JPopupMenu();
        JMenuItem recuperarItem = new JMenuItem("Recuperar");
        JMenuItem eliminarItem = new JMenuItem("Eliminar");
        clickDerechoMenu.add(recuperarItem);
        clickDerechoMenu.addSeparator();
        clickDerechoMenu.add(eliminarItem);
        
        initComponents();
        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new ExploradorIconTableCellRenderer());
        colocarArchivosTabla(papelera);
        
        tablaArchivos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                JTable tabla = (JTable) evt.getSource();
                Point p = evt.getPoint();
                int nroFila = tabla.rowAtPoint(p);
                if (SwingUtilities.isRightMouseButton(evt)) {
                    if (evt.getClickCount() == 1) {
                        archivoSeleccionado = archivosActuales.elementAt(nroFila);
                        tabla.setRowSelectionInterval(nroFila, nroFila);
                        clickDerechoMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                } 
            }
        });
        
        recuperarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String pathOld = fsv.getSystemDisplayName(archivoSeleccionado);
                System.out.println("Falta implementar :D");
                /*
                    String[] nameArray = pathOld.split(".");
                String ext = nameArray[nameArray.length-1];
                
                pathOld = fsv.getSystemDisplayName(archivoSeleccionado)+ext;
                String pathAct = archivoSeleccionado.getAbsolutePath();
                for (int i = 0; i < nameArray.length; i++) {
                    System.out.println(nameArray[i]);
                }
                    System.out.println(pathOld);
                    System.out.println(pathAct);
                */
            }
        });
        eliminarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                    Files.delete(archivoSeleccionado.toPath());
                    System.out.println(archivoSeleccionado.toPath());
                } catch (IOException ex) {
                    Logger.getLogger(PapeleraReciclaje.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       
        ActionListener updatePapelera = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colocarArchivosTabla(papelera);
            }
            
        };
        
        Timer t = new Timer(1000, updatePapelera);
        t.start();
        
    }
    private void colocarArchivosTabla(File directorio) {
        archivosActuales.clear();

        File[] archivos = directorio.listFiles();
        DefaultTableModel dtm = (DefaultTableModel) tablaArchivos.getModel();
        dtm.setRowCount(0);

        // poner los archivos en la tabla
        for (int i = 0; i < archivos.length; i++) {
                if(!fsv.isHiddenFile(archivos[i])){
                Vector v = new Vector();
                v.add("");
                v.add(fsv.getSystemDisplayName(archivos[i]));
                v.add(sdf.format(archivos[i].lastModified()));
                v.add(fsv.getSystemTypeDescription(archivos[i]));

                if (fsv.isDrive(archivos[i])) {
                    v.add(bytesAKBs(archivos[i].getTotalSpace() / (1024 * 1024 * 1024), " GB"));
                } else {
                    v.add(bytesAKBs(archivos[i].length() / (1024), " KB"));
                }

                dtm.addRow(v);
                dtm.setValueAt(fsv.getSystemIcon(archivos[i]), dtm.getRowCount() - 1, 0);

                archivosActuales.add(archivos[i]);
                }
        }

        // ajustar el ancho de las columnas
        for (int i = 0; i < tablaArchivos.getColumnCount(); i++) {
            DefaultTableColumnModel colModel = (DefaultTableColumnModel) tablaArchivos.getColumnModel();
            TableColumn col = colModel.getColumn(i);
            int width = 0;

            TableCellRenderer renderer = col.getHeaderRenderer();
            for (int r = 0; r < tablaArchivos.getRowCount(); r++) {
                renderer = tablaArchivos.getCellRenderer(r, i);
                Component comp = renderer.getTableCellRendererComponent(tablaArchivos, tablaArchivos.getValueAt(r, i),
                        false, false, r, i);
                width = Math.max(width, comp.getPreferredSize().width);
            }
            col.setPreferredWidth(width + 2);
        }
    }
    
    private String bytesAKBs(long bytes, String sufix) {
        StringBuilder kbs = new StringBuilder();
        String cadena = Long.toString(bytes);
        int j = 0;
        for (int i = cadena.length() - 1; i >= 0; i--) {
            if (j != 0 && j % 3 == 0) {
                kbs.append(",");
            }
            kbs.append(cadena.charAt(i));
            j++;
        }
        return kbs.reverse().append(sufix).toString();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Papelera de reciclaje");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/img/papelera-vacia-icon.png"))); // NOI18N

        tablaArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Nombre", "Fecha de Modificacion", "Tipo", "Tamaño"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaArchivos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaArchivos;
    // End of variables declaration//GEN-END:variables
}
