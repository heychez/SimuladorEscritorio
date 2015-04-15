

import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
public class Escritorio extends javax.swing.JFrame {

    /**
     * Creates new form Escritorio
     */
    public Escritorio() {
        initComponents();

        JPopupMenu clickDerechoInMenu = new JPopupMenu();
        JMenuItem abrirItem = new JMenuItem("Abrir");
        JMenuItem cortarItem = new JMenuItem("Cortar");
        JMenuItem copiarItem = new JMenuItem("Copiar");
        JMenuItem eliminarItem = new JMenuItem("Eliminar");
        clickDerechoInMenu.add(abrirItem);
        clickDerechoInMenu.addSeparator();
        clickDerechoInMenu.add(cortarItem);
        clickDerechoInMenu.add(copiarItem);
        clickDerechoInMenu.add(eliminarItem);

        JPopupMenu clickDerechoOutMenu = new JPopupMenu();
        JMenu nuevoItem = new JMenu("Nuevo");
        JMenuItem nuevaCarpetaItem = new JMenuItem("Carpeta");
        JMenuItem nuevoAccesoDirectoItem = new JMenuItem("Acceso directo");
        JMenuItem nuevoWordItem = new JMenuItem("Documento Word");
        JMenuItem nuevoExcelItem = new JMenuItem("Documento Excel");
        JMenuItem pegarItem = new JMenuItem("Pegar");
        clickDerechoOutMenu.add(nuevoItem);
        nuevoItem.add(nuevaCarpetaItem);
        nuevoItem.add(nuevoAccesoDirectoItem);
        nuevoItem.addSeparator();
        nuevoItem.add(nuevoWordItem);
        nuevoItem.add(nuevoExcelItem);
        clickDerechoOutMenu.addSeparator();
        clickDerechoOutMenu.add(pegarItem);

        FileSystemView filesys = FileSystemView.getFileSystemView();
        //File roots[] = filesys.getRoots();
        File escritorioDirectorio = filesys.getHomeDirectory();

        File files[] = escritorioDirectorio.listFiles();
        Vector<File> escritorioArchivos = new Vector();

        for (int i = 0; i < files.length; i++) {
            // buscando la papelera
            /*
             if (filesys.getSystemDisplayName(files[i]).equals("Equipo")) {
             File[] fs = files[i].listFiles();
             File[] fss = fs[0].listFiles();
             for (int j = 0; j < fss.length; j++) {
             if (filesys.getSystemDisplayName(fss[j]).equals("$Recycle.Bin")) {
             File[] fsss = fss[j].listFiles();
             escritorioArchivos.add(fsss[0]);
             System.out.println(fsss[0].getName());
             }
             }
             }
             */
            escritorioArchivos.add(files[i]);
        }

        //Papelera 
        File papelera = (new File("C:/$Recycle.Bin")).listFiles()[0];
        escritorioArchivos.add(papelera);

        // JList de archivos del escritorio
        archivosJList.setListData(escritorioArchivos);
        archivosJList.setCellRenderer(new EscritorioCellRenderer());
        archivosJList.setVisibleRowCount(0);
        archivosJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        //Image fondo = new ImageIcon(this.getClass().getResource("/img/win7.png")).getImage();
        // listener del evento doble click en un elemento del JList
        archivosJList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                java.awt.Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());

                if (r.contains(evt.getPoint())) {
                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        //r != null
                        if (evt.getClickCount() == 2) {
                            //int index = list.locationToIndex(evt.getPoint());
                            int index = list.getSelectedIndex();
                            ListModel model = list.getModel();
                            File f = (File) model.getElementAt(index);
                            String nombreDeArchivo = filesys.getSystemDisplayName(f).toLowerCase();
                            System.out.println(nombreDeArchivo);

                            if (nombreDeArchivo.contains("equipo")) {
                                MiEquipo miEquipo = new MiEquipo();
                                escritorio.add(miEquipo);
                                miEquipo.show();
                            } else if (nombreDeArchivo.contains("reciclaje")) {
                                PapeleraReciclaje papeleraReciclaje = new PapeleraReciclaje();
                                escritorio.add(papeleraReciclaje);
                                papeleraReciclaje.show();
                            }else{
                                Desktop d = Desktop.getDesktop();
                                try {
                                    d.browse(f.toURI());
                                } catch (IOException ex) {
                                    Logger.getLogger(Escritorio.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                    } else { //click derecho
                        if (evt.getClickCount() == 1) {
                            int index = list.locationToIndex(evt.getPoint());
                            list.setSelectedIndex(index);

                            clickDerechoInMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                        }
                    }
                } else {
                    list.clearSelection();
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        clickDerechoOutMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
            }
        });

        ///Reloj de Barra
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm a");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        ActionListener updateClock = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date d = new Date();
                fechaHoraLabel.setText("<html>" + sdf1.format(d) + " <br>" + sdf2.format(d) + "</html>");
            }
        };

        Timer t = new Timer(1000, updateClock);
        t.start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        escritorio = new javax.swing.JDesktopPane();
        archivosJScrollPane = new javax.swing.JScrollPane();
        archivosJList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        fechaHoraLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Escritorio");
        setIconImage((new ImageIcon(this.getClass().getResource("/img/desktop-icon-3.png"))).getImage());
        setPreferredSize(new java.awt.Dimension(1218, 646));

        escritorio.setBackground(new java.awt.Color(240, 240, 240));
        escritorio.setPreferredSize(new java.awt.Dimension(1200, 550));

        archivosJScrollPane.setBorder(null);
        archivosJScrollPane.setHorizontalScrollBar(null);
        archivosJScrollPane.setPreferredSize(new java.awt.Dimension(1200, 550));

        archivosJList.setPreferredSize(new java.awt.Dimension(1200, 550));
        archivosJScrollPane.setViewportView(archivosJList);

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(archivosJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(archivosJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );
        escritorio.setLayer(archivosJScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1200, 50));

        jButton1.setBackground(new java.awt.Color(153, 153, 255));
        jButton1.setForeground(new java.awt.Color(153, 153, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/win-start-icon-50x50.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setPreferredSize(new java.awt.Dimension(50, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        fechaHoraLabel.setForeground(new java.awt.Color(255, 255, 255));
        fechaHoraLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fechaHoraLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(fechaHoraLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jPopupMenu1.removeAll();
        this.repaint();

        String inicioPath = "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs";
        File files[] = (new File(inicioPath)).listFiles();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        for (int i = 0; i < files.length; i++) {
            JMenuItem ex = new JMenuItem(files[i].getName());
            ex.setIcon(fsv.getSystemIcon(files[i]));
            jPopupMenu1.add(ex);
        }

        jPopupMenu1.show(jButton1, 0, -jPopupMenu1.getPreferredSize().height);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Escritorio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList archivosJList;
    private javax.swing.JScrollPane archivosJScrollPane;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JLabel fechaHoraLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    // End of variables declaration//GEN-END:variables
}
