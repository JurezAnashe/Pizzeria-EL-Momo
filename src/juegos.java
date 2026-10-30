package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class juegos extends JFrame implements ActionListener {
    private int x = 100;
    private int y = 200; 
    private int velocidadX = 0;
    private int velocidadY = 0;
    private Timer timer;
    
    private Image personajeImg;
    private Image fondoCocinaImg;
    private Image fondoNuevaEscenaImg;

    private int escenaActual = 1; 
    
    // NUEVO: Bandera para controlar que la VentanaPrincipal no se abra infinitas veces consecutivas
    private boolean ventanaAbierta = false; 

    public juegos() {
        setTitle("Mover Personaje con Imagen - Java Swing");
        setSize(1024, 600); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Carga de imágenes
        java.net.URL urlPersonaje = getClass().getResource("/src/img/momo.jpg");
        if (urlPersonaje != null) personajeImg = new ImageIcon(urlPersonaje).getImage();

        java.net.URL urlFondo = getClass().getResource("/src/img/pizzeria.jpg");
        if (urlFondo != null) fondoCocinaImg = new ImageIcon(urlFondo).getImage();

        java.net.URL urlNuevoFondo = getClass().getResource("/src/img/cocina.jpg");
        if (urlNuevoFondo != null) fondoNuevaEscenaImg = new ImageIcon(urlNuevoFondo).getImage();

        JPanel panelJuego = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (escenaActual == 1) {
                    if (fondoCocinaImg != null) g.drawImage(fondoCocinaImg, 0, 0, getWidth(), getHeight(), this);
                } else if (escenaActual == 2) {
                    if (fondoNuevaEscenaImg != null) g.drawImage(fondoNuevaEscenaImg, 0, 0, getWidth(), getHeight(), this);
                }
                
                if (personajeImg != null && personajeImg.getWidth(this) > 0) {
                    g.drawImage(personajeImg, x, y, 60, 60, this);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, 60, 60);
                }
            }
        };
        
        panelJuego.setBackground(Color.WHITE);
        panelJuego.setFocusable(true);

        panelJuego.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)   velocidadX = -5;
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)  velocidadX = 5;
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)     velocidadY = -5;
                if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)   velocidadY = 5;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_A || key == KeyEvent.VK_D) velocidadX = 0;
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_W || key == KeyEvent.VK_S) velocidadY = 0;
            }
        });

        add(panelJuego);

        timer = new Timer(16, this);
        timer.start();
        
        panelJuego.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        x += velocidadX;
        y += velocidadY;

        if (x < 0) x = 0;
        if (x > getWidth() - 76) x = getWidth() - 76;
        if (y < 0) y = 0;
        if (y > getHeight() - 99) y = getHeight() - 99;

        Rectangle hitBoxPersonaje = new Rectangle(x, y, 60, 60);

        // ESCENA 1: Ir a la cocina
        if (escenaActual == 1) {
            Rectangle hitBoxPuertaIda = new Rectangle(285, 60, 90, 140);

            if (hitBoxPersonaje.intersects(hitBoxPuertaIda)) {
                escenaActual = 2; 
                x = 80;
                y = 480; 
                velocidadX = 0;
                velocidadY = 0;
            }
        } 
        // ESCENA 2: Cocina interna
        else if (escenaActual == 2) {
            // 1. Zona de salida (abajo a la izquierda)
            Rectangle hitBoxSalidaRegreso = new Rectangle(0, 450, 70, 150);

            if (hitBoxPersonaje.intersects(hitBoxSalidaRegreso)) {
                escenaActual = 1; 
                x = 295;
                y = 210; 
                velocidadX = 0;
                velocidadY = 0;
            }

            // 2. NUEVO: Zona de las hornallas/cocina para abrir el sistema
            // Coordenadas estimadas según tu captura: X=580, Y=220, Ancho=160, Alto=100
            Rectangle hitBoxHornallas = new Rectangle(580, 220, 160, 100);

            if (hitBoxPersonaje.intersects(hitBoxHornallas)) {
                // Solo la abrimos si no hay una ventana interactiva activa en este momento
                if (!ventanaAbierta) {
                    ventanaAbierta = true;
                    System.out.println("¡Abriendo menú de gestión en la cocina!");
                    
                    // Detenemos el movimiento para que no siga caminando solo de fondo
                    velocidadX = 0;
                    velocidadY = 0;

                    // Instanciamos tu ventana anterior de gestión y la hacemos visible
                    VentanaPrincipal ventanaGestion = new VentanaPrincipal();
                    ventanaGestion.setVisible(true);

                    // Al cerrarse la ventana principal de gestión, liberamos el "candado"
                    // para que el jugador pueda volver a interactuar si lo desea
                    ventanaGestion.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            ventanaAbierta = false;
                        }
                    });
                }
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new juegos().setVisible(true));
    }
}
