
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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

    FileSystemView fsv = FileSystemView.getFileSystemView();
    Vector<File> escritorioArchivos = new Vector();
    String nombreUsuario;
    File archivoListaSeleccionado;
    File archivoCopiado = null;
    boolean esArchivoCortado = false;

    JPopupMenu clickDerechoInMenu = new JPopupMenu();
    JMenuItem abrirItem = new JMenuItem("Abrir");
    JMenuItem cortarItem = new JMenuItem("Cortar");
    JMenuItem copiarItem = new JMenuItem("Copiar");
    JMenuItem eliminarItem = new JMenuItem("Eliminar");
    JPopupMenu clickDerechoOutMenu = new JPopupMenu();
    JMenu nuevoItem = new JMenu("Nuevo");
    JMenuItem nuevaCarpetaItem = new JMenuItem("Carpeta");
    JMenuItem pegarItem = new JMenuItem("Pegar");

    /**
     * Creates new form Escritorio
     */
    public Escritorio() {
        clickDerechoInMenu.add(abrirItem);
        clickDerechoInMenu.addSeparator();
        clickDerechoInMenu.add(cortarItem);
        clickDerechoInMenu.add(copiarItem);
        clickDerechoInMenu.add(eliminarItem);

        clickDerechoOutMenu.add(nuevoItem);
        nuevoItem.add(nuevaCarpetaItem);
        clickDerechoOutMenu.addSeparator();
        clickDerechoOutMenu.add(pegarItem);
        pegarItem.setEnabled(false);

        initComponents();

        this.nombreUsuario = System.getProperty("user.home").substring(9);

        colocarArchivosEscritorio();
        ActionListener colocarArchivosEscritorioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colocarArchivosEscritorio();
            }
        };
        Timer t1 = new Timer(5000, colocarArchivosEscritorioListener);
        t1.start();

        abrirItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (archivoListaSeleccionado.isDirectory()) {
                    Explorador directorio = new Explorador(archivoListaSeleccionado);
                    escritorio.add(directorio);
                    directorio.show();
                    return;
                } else {
                    Desktop d = Desktop.getDesktop();
                    try {
                        d.open(archivoListaSeleccionado);
                    } catch (IOException ex) {
                        Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        nuevaCarpetaItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = JOptionPane.showInputDialog("Crear nueva carpeta");
                if (nombre != null) {
                    String path = fsv.getHomeDirectory().getPath();
                    File nuevaCarpeta = new File(path + "/" + nombre);
                    if (nuevaCarpeta.exists()) {
                        System.out.println("ERROR: El nombre de la carpeta ya existe.");
                    } else {
                        nuevaCarpeta.mkdir();
                    }
                }
            }
        });

        copiarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                archivoCopiado = archivoListaSeleccionado;
                esArchivoCortado = false;
                pegarItem.setEnabled(true);
            }
        });

        cortarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                archivoCopiado = archivoListaSeleccionado;
                esArchivoCortado = true;
                pegarItem.setEnabled(true);
            }
        });

        pegarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String carpetaActual = fsv.getHomeDirectory().getPath();
                    String nuevoArchivoUbicacion = carpetaActual + File.separator + archivoCopiado.getName();
                    Files.copy(archivoCopiado.toPath(), new File(nuevoArchivoUbicacion).toPath());

                    if (esArchivoCortado) {
                        try {
                            Files.delete(archivoCopiado.toPath());
                            esArchivoCortado = false;
                            pegarItem.setEnabled(false);
                        } catch (IOException ex) {
                            Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        eliminarItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Files.delete(archivoListaSeleccionado.toPath());
                } catch (IOException ex) {
                    Logger.getLogger(Explorador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        listaArchivos.setCellRenderer(new EscritorioListCellRenderer());
        listaArchivos.setVisibleRowCount(0);
        listaArchivos.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        //Image fondo = new ImageIcon(this.getClass().getResource("/img/win7.png")).getImage();
        listaArchivos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                java.awt.Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());

                if (r.contains(evt.getPoint())) {
                    int index = list.getSelectedIndex();

                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        if (evt.getClickCount() == 2) {
                            File archivoSeleccionado = (File) list.getModel().getElementAt(index);
                            String nombreDeArchivo = fsv.getSystemDisplayName(archivoSeleccionado).toLowerCase();
                            
                            if (nombreDeArchivo.contains("equipo")) {
                                Explorador miEquipo = new Explorador(archivoSeleccionado, "Equipo");
                                escritorio.add(miEquipo);
                                miEquipo.show();
                            } else if (nombreDeArchivo.contains("bibliotecas")) {
                                Explorador miEquipo = new Explorador(archivoSeleccionado, "Bibliotecas");
                                escritorio.add(miEquipo);
                                miEquipo.show();
                            } else if (nombreDeArchivo.contains("red")) {
                                Explorador miEquipo = new Explorador(archivoSeleccionado, "Red");
                                escritorio.add(miEquipo);
                                miEquipo.show();
                            } else if (nombreDeArchivo.contains("reciclaje")) {
                                PapeleraReciclaje papeleraReciclaje = new PapeleraReciclaje();
                                escritorio.add(papeleraReciclaje);
                                papeleraReciclaje.show();
                            } else if (archivoSeleccionado.isDirectory()) {
                                Explorador directorio = new Explorador(archivoSeleccionado);
                                escritorio.add(directorio);
                                directorio.show();
                            } else {
                                Desktop d = Desktop.getDesktop();
                                try {
                                    d.open(archivoSeleccionado);
                                } catch (IOException ex) {
                                    Logger.getLogger(Escritorio.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                    } else { //click derecho
                        if (evt.getClickCount() == 1) {
                            int i = list.locationToIndex(evt.getPoint());
                            list.setSelectedIndex(i);

                            archivoListaSeleccionado = (File) list.getModel().getElementAt(index);

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
                etiquetaFechaHora.setText("<html>" + sdf1.format(d) + " <br>" + sdf2.format(d) + "</html>");
            }
        };
        Timer t2 = new Timer(1000, updateClock);
        t2.start();

    }

    private void colocarArchivosEscritorio() {
        escritorioArchivos.removeAllElements();

        //Papelera
        File papelera = (new File("C:/$Recycle.Bin")).listFiles()[0];
        escritorioArchivos.add(papelera);

        File[] files = fsv.getHomeDirectory().listFiles();

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isHidden()) {
                escritorioArchivos.add(files[i]);
            }
        }

        listaArchivos.setListData(escritorioArchivos);
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
        panelLista = new javax.swing.JScrollPane();
        listaArchivos = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        botonInicio = new javax.swing.JButton();
        etiquetaFechaHora = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Escritorio");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage((new ImageIcon(this.getClass().getResource("/img/desktop-icon-3.png"))).getImage());
        setPreferredSize(new java.awt.Dimension(1218, 646));

        escritorio.setBackground(new java.awt.Color(240, 240, 240));
        escritorio.setPreferredSize(new java.awt.Dimension(1200, 550));

        panelLista.setBorder(null);
        panelLista.setHorizontalScrollBar(null);
        panelLista.setPreferredSize(new java.awt.Dimension(1200, 550));

        listaArchivos.setPreferredSize(new java.awt.Dimension(1200, 550));
        panelLista.setViewportView(listaArchivos);

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLista, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLista, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );
        escritorio.setLayer(panelLista, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1200, 50));

        botonInicio.setBackground(new java.awt.Color(153, 153, 255));
        botonInicio.setForeground(new java.awt.Color(153, 153, 255));
        botonInicio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/win-start-icon-50x50.png"))); // NOI18N
        botonInicio.setBorder(null);
        botonInicio.setBorderPainted(false);
        botonInicio.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        botonInicio.setFocusable(false);
        botonInicio.setPreferredSize(new java.awt.Dimension(50, 50));
        botonInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonInicioActionPerformed(evt);
            }
        });

        etiquetaFechaHora.setForeground(new java.awt.Color(255, 255, 255));
        etiquetaFechaHora.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(botonInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaFechaHora, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botonInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(etiquetaFechaHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void botonInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonInicioActionPerformed
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

        jPopupMenu1.show(botonInicio, 0, -jPopupMenu1.getPreferredSize().height);
    }//GEN-LAST:event_botonInicioActionPerformed

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
    private javax.swing.JButton botonInicio;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JLabel etiquetaFechaHora;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JList listaArchivos;
    private javax.swing.JScrollPane panelLista;
    // End of variables declaration//GEN-END:variables
}
