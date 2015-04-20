
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Roberto
 */
public class Explorador extends javax.swing.JInternalFrame {

    FileSystemView fsv = FileSystemView.getFileSystemView();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    File directorio;
    String ventanaTitulo;
    ImageIcon ventanaIcono;
    Vector<File> archivosActuales = new Vector();

    /**
     * Creates new form MiEquipo
     */
    public Explorador() {
        this(null, null, "");
    }

    public Explorador(File directorio) {
        this(directorio, (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(directorio));
    }

    public Explorador(File directorio, ImageIcon ventanaIcono) {
        this(directorio, ventanaIcono, directorio.getPath());
    }

    public Explorador(File directorio, ImageIcon ventanaIcono, String ruta) {
        this.directorio = directorio;
        this.ventanaIcono = ventanaIcono;
        this.ventanaTitulo = fsv.getSystemDisplayName(directorio);

        initComponents();

        this.campoRuta.setText(ruta);
        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new ExploradorCellRenderer());

        colocarArchivosEnLaTabla(this.directorio);

        tablaArchivos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                JTable tabla = (JTable) evt.getSource();
                //java.awt.Rectangle r = tabla.getCellBounds(0, tabla.getLastVisibleIndex());
                Point p = evt.getPoint();
                int nroFila = tabla.rowAtPoint(p);
                //if (r.contains(evt.getPoint())) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (evt.getClickCount() == 2) {
                        File archivoSeleccionado = archivosActuales.elementAt(nroFila);
                        if (archivoSeleccionado.isDirectory()) {
                            colocarArchivosEnLaTabla(archivoSeleccionado);
                            return;
                        } else {
                            Desktop d = Desktop.getDesktop();
                            try {
                                d.open(archivoSeleccionado);
                            } catch (IOException ex) {
                                Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    } else { //click derecho
                        if (evt.getClickCount() == 1) {
                                //int index = list.locationToIndex(evt.getPoint());
                            //list.setSelectedIndex(index);

                            //clickDerechoInMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                        }
                    }
                } else {
                    //list.clearSelection();
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        //clickDerechoOutMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
                //}
            }
        });
    }

    private void colocarArchivosEnLaTabla(File directorio) {
        archivosActuales.clear();

        File[] archivos = directorio.listFiles();
        DefaultTableModel dtm = (DefaultTableModel) tablaArchivos.getModel();
        dtm.setRowCount(0);

        // poner los archivos en la tabla
        for (int i = 0; i < archivos.length; i++) {
            if (!archivos[i].isHidden() || fsv.isDrive(archivos[i])) {
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

        jDesktopPane2 = new javax.swing.JDesktopPane();
        panelTabla = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();
        jDesktopPane3 = new javax.swing.JDesktopPane();
        panelArbol = new javax.swing.JScrollPane();
        arbolArchivos = new javax.swing.JTree();
        campoRuta = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(ventanaTitulo);
        setFrameIcon(ventanaIcono);

        jDesktopPane2.setBackground(new java.awt.Color(153, 255, 204));

        panelTabla.setBackground(new java.awt.Color(255, 255, 255));

        tablaArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Nombre", "Fecha de modificacion", "Tipo", "Tamaño"
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
        tablaArchivos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaArchivos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tablaArchivos.setIntercellSpacing(new java.awt.Dimension(2, 2));
        tablaArchivos.setRowHeight(20);
        tablaArchivos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaArchivos.setShowHorizontalLines(false);
        tablaArchivos.setShowVerticalLines(false);
        tablaArchivos.getTableHeader().setResizingAllowed(false);
        tablaArchivos.getTableHeader().setReorderingAllowed(false);
        panelTabla.setViewportView(tablaArchivos);

        javax.swing.GroupLayout jDesktopPane2Layout = new javax.swing.GroupLayout(jDesktopPane2);
        jDesktopPane2.setLayout(jDesktopPane2Layout);
        jDesktopPane2Layout.setHorizontalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
        );
        jDesktopPane2Layout.setVerticalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jDesktopPane2.setLayer(panelTabla, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jDesktopPane3.setBackground(new java.awt.Color(204, 255, 204));

        panelArbol.setViewportView(arbolArchivos);

        javax.swing.GroupLayout jDesktopPane3Layout = new javax.swing.GroupLayout(jDesktopPane3);
        jDesktopPane3.setLayout(jDesktopPane3Layout);
        jDesktopPane3Layout.setHorizontalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelArbol, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
        );
        jDesktopPane3Layout.setVerticalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelArbol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
        );
        jDesktopPane3.setLayer(panelArbol, javax.swing.JLayeredPane.DEFAULT_LAYER);

        campoRuta.setText("Equipo");
        campoRuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoRutaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDesktopPane2))
            .addComponent(campoRuta)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(campoRuta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDesktopPane2)
                    .addComponent(jDesktopPane3)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void campoRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoRutaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoRutaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbolArchivos;
    private javax.swing.JTextField campoRuta;
    private javax.swing.JDesktopPane jDesktopPane2;
    private javax.swing.JDesktopPane jDesktopPane3;
    private javax.swing.JScrollPane panelArbol;
    private javax.swing.JScrollPane panelTabla;
    private javax.swing.JTable tablaArchivos;
    // End of variables declaration//GEN-END:variables
}
