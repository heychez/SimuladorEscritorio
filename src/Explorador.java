
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

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
    String ventanaTitulo;
    ImageIcon ventanaIcono;
    Vector<File> archivosActuales = new Vector();
    Vector<File> directoriosVisitados = new Vector();
    int indiceDirectorioActual = 0;

    /**
     * Creates new form MiEquipo
     */
    public Explorador() {
        this(null, null, "");
    }

    public Explorador(File directorioInicial) {
        this(directorioInicial, (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(directorioInicial));
    }

    public Explorador(File directorioInicial, ImageIcon ventanaIcono) {
        this(directorioInicial, ventanaIcono, directorioInicial.getPath());
    }

    public Explorador(File directorioInicial, String ruta) {
        this(directorioInicial, (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(directorioInicial), ruta);
    }

    public Explorador(File directorioInicial, ImageIcon ventanaIcono, String ruta) {
        this.ventanaIcono = ventanaIcono;
        this.ventanaTitulo = fsv.getSystemDisplayName(directorioInicial);

        JPopupMenu clickDerechoMenu = new JPopupMenu();
        JMenuItem abrirItem = new JMenuItem("Abrir");
        JMenuItem cortarItem = new JMenuItem("Cortar");
        JMenuItem copiarItem = new JMenuItem("Copiar");
        JMenuItem pegarItem = new JMenuItem("Pegar");
        JMenuItem eliminarItem = new JMenuItem("Eliminar");
        clickDerechoMenu.add(abrirItem);
        clickDerechoMenu.addSeparator();
        clickDerechoMenu.add(cortarItem);
        clickDerechoMenu.add(copiarItem);
        clickDerechoMenu.add(eliminarItem);
        pegarItem.setEnabled(false);

        initComponents();

        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new ExploradorCellRenderer());
        directoriosVisitados.add(directorioInicial);

        colocarArchivosTabla(directorioInicial);
        campoRuta.setText(ruta);

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
                            directoriosVisitados.setSize(indiceDirectorioActual + 1);
                            directoriosVisitados.add(archivoSeleccionado);
                            indiceDirectorioActual++;

                            colocarArchivosTabla(archivoSeleccionado);
                            return;
                        } else {
                            Desktop d = Desktop.getDesktop();
                            try {
                                d.open(archivoSeleccionado);
                            } catch (IOException ex) {
                                Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } else { //click derecho
                    tabla.setRowSelectionInterval(nroFila, nroFila);
                    clickDerechoMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
                //}
            }
        });

        arbolArchivos.setCellRenderer(new MyTreeCellRenderer());

        arbolArchivos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = arbolArchivos.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = arbolArchivos.getPathForLocation(e.getX(), e.getY());

                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                File f = new File((String) nodo.getUserObject());
                System.out.println(nodo.getUserObject());

                if (selRow != -1) {
                    if (e.getClickCount() == 2) {
                        if (!(f.isDirectory() && fsv.isDrive(f))) {
                            Desktop d = Desktop.getDesktop();
                            try {
                                d.open(f);
                            } catch (IOException ex) {
                                Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else if (e.getClickCount() == 1) {
                        if (f.isDirectory() || fsv.isDrive(f)) {
                            colocarArchivosArbol(f);

                            directoriosVisitados.setSize(indiceDirectorioActual + 1);
                            directoriosVisitados.add(f);
                            indiceDirectorioActual++;

                            colocarArchivosTabla(f);
                        }
                    }
                }
            }
        });
    }

    private void colocarArchivosTabla(File directorio) {
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

        // poer la ruta actual
        campoRuta.setText(directorio.getPath());

        // llenar el arbol de archivos
        colocarArchivosArbol(directorio);
    }

    public void colocarArchivosArbol(File directorio) {
        DefaultMutableTreeNode padre = new DefaultMutableTreeNode(directorio.getPath());
        File[] archivos = directorio.listFiles();
        for (int i = 0; i < archivos.length; i++) {
            DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(archivos[i].getPath());
            if (!archivos[i].isHidden() || fsv.isDrive(archivos[i])) {
                if (archivos[i].isDirectory() || fsv.isDrive(archivos[i])) {
                    File[] hijos = archivos[i].listFiles();
                    for (int j = 0; j < hijos.length; j++) {
                        DefaultMutableTreeNode nieto = new DefaultMutableTreeNode(hijos[j].getPath());
                        hijo.add(nieto);
                    }
                }
                padre.add(hijo);
            }
        }

        DefaultTreeModel dtm = (DefaultTreeModel) arbolArchivos.getModel();
        dtm.setRoot(padre);
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
        botonAtras = new javax.swing.JButton();
        botonAdelante = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(ventanaTitulo);
        setFrameIcon(ventanaIcono);
        setPreferredSize(new java.awt.Dimension(700, 450));

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
            .addComponent(panelArbol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
        );
        jDesktopPane3.setLayer(panelArbol, javax.swing.JLayeredPane.DEFAULT_LAYER);

        campoRuta.setText("Equipo");
        campoRuta.setMargin(new Insets(0, 8, 0, 0));
        campoRuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoRutaActionPerformed(evt);
            }
        });

        botonAtras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/atras-icon.png"))); // NOI18N
        botonAtras.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botonAtras.setFocusPainted(false);
        botonAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAtrasActionPerformed(evt);
            }
        });

        botonAdelante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/adelante-icon.png"))); // NOI18N
        botonAdelante.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botonAdelante.setFocusPainted(false);
        botonAdelante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAdelanteActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonAtras, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonAdelante, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(campoRuta)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(botonAdelante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(botonAtras, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campoRuta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDesktopPane2)
                    .addComponent(jDesktopPane3)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void campoRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoRutaActionPerformed
        File directorioEntrante = new File(campoRuta.getText());

        if (directorioEntrante == null) {
            System.out.println("ERROR: ruta ingresada invalida.");
        } else {
            if (directorioEntrante.isDirectory()) {
                directoriosVisitados.setSize(indiceDirectorioActual + 1);
                directoriosVisitados.add(directorioEntrante);
                indiceDirectorioActual++;

                colocarArchivosTabla(directorioEntrante);
            } else {
                Desktop d = Desktop.getDesktop();
                try {
                    d.open(directorioEntrante);
                } catch (IOException ex) {
                    Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_campoRutaActionPerformed

    private void botonAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAtrasActionPerformed
        if (indiceDirectorioActual > 0) {
            indiceDirectorioActual--;
            colocarArchivosTabla(directoriosVisitados.elementAt(indiceDirectorioActual));
        }
    }//GEN-LAST:event_botonAtrasActionPerformed

    private void botonAdelanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAdelanteActionPerformed
        if (indiceDirectorioActual < directoriosVisitados.size() - 1) {
            indiceDirectorioActual++;
            colocarArchivosTabla(directoriosVisitados.elementAt(indiceDirectorioActual));
        }
    }//GEN-LAST:event_botonAdelanteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbolArchivos;
    private javax.swing.JButton botonAdelante;
    private javax.swing.JButton botonAtras;
    private javax.swing.JTextField campoRuta;
    private javax.swing.JDesktopPane jDesktopPane2;
    private javax.swing.JDesktopPane jDesktopPane3;
    private javax.swing.JScrollPane panelArbol;
    private javax.swing.JScrollPane panelTabla;
    private javax.swing.JTable tablaArchivos;
    // End of variables declaration//GEN-END:variables
}
