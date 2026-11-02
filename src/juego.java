package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class juego extends JFrame implements ActionListener {
    private int x = 100;
    private int y = 200;
    private int velocidadX = 0;
    private int velocidadY = 0;
    private Timer timer;

    private Image personajeImg;
    private Image fondoCocinaImg;
    private Image fondoNuevaEscenaImg;

    private int escenaActual = 1;
    private boolean ventanaAbierta = false;

    private final int PERSONAJE_ANCHO = 75;
    private final int PERSONAJE_ALTO = 75;
    private final int VELOCIDAD_PASO = 8;

    private List<NPC> listaNPCs = new ArrayList<>();

    // Panel del juego (campo para poder devolverle el foco al cerrar la ventana)
    private JPanel panelJuego;

    // Única instancia de la ventana de gestión: se crea una vez y se reutiliza,
    // así los pedidos registrados no se pierden al cerrarla con la X
    private VentanaPrincipal ventanaGestion;

    private class NPC {
        int indiceSilla;
        Image img;

        public NPC(int indiceSilla, String rutaImg) {
            this.indiceSilla = indiceSilla;
            java.net.URL url = getClass().getResource(rutaImg);
            if (url != null) {
                this.img = new ImageIcon(url).getImage();
            }
        }
    }

    public juego() {
        setTitle("Mover Personaje con Imagen - Java Swing");
        setSize(1024, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Carga de imágenes principales
        java.net.URL urlPersonaje = getClass().getResource("/src/img/momo.jpg");
        if (urlPersonaje != null)
            personajeImg = new ImageIcon(urlPersonaje).getImage();

        java.net.URL urlFondo = getClass().getResource("/src/img/pizzeria.jpg");
        if (urlFondo != null)
            fondoCocinaImg = new ImageIcon(urlFondo).getImage();

        java.net.URL urlNuevoFondo = getClass().getResource("/src/img/cocina.jpg");
        if (urlNuevoFondo != null)
            fondoNuevaEscenaImg = new ImageIcon(urlNuevoFondo).getImage();

        // Generar posiciones iniciales de los NPCs
        generarPosicionesAleatoriasNPCs();

        panelJuego = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int panelW = getWidth();
                int panelH = getHeight();

                if (escenaActual == 1) {
                    if (fondoCocinaImg != null)
                        g.drawImage(fondoCocinaImg, 0, 0, panelW, panelH, this);

                    // Dibujar NPCs de forma dinámica
                    for (NPC npc : listaNPCs) {
                        Point posDinamica = obtenerCoordenadaSilla(npc.indiceSilla, panelW, panelH);

                        if (npc.img != null) {
                            g.drawImage(npc.img, posDinamica.x, posDinamica.y, PERSONAJE_ANCHO, PERSONAJE_ALTO, this);
                        } else {
                            g.setColor(Color.BLUE);
                            g.fillRect(posDinamica.x, posDinamica.y, PERSONAJE_ANCHO, PERSONAJE_ALTO);
                        }
                    }

                } else if (escenaActual == 2) {
                    if (fondoNuevaEscenaImg != null)
                        g.drawImage(fondoNuevaEscenaImg, 0, 0, panelW, panelH, this);
                }

                // Dibujar personaje principal
                if (personajeImg != null && personajeImg.getWidth(this) > 0) {
                    g.drawImage(personajeImg, x, y, PERSONAJE_ANCHO, PERSONAJE_ALTO, this);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, PERSONAJE_ANCHO, PERSONAJE_ALTO);
                }
            }
        };

        panelJuego.setBackground(Color.WHITE);
        panelJuego.setFocusable(true);

        panelJuego.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Bloquear entradas si la ventana de gestión está abierta
                if (ventanaAbierta)
                    return;

                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
                    velocidadX = -VELOCIDAD_PASO;
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
                    velocidadX = VELOCIDAD_PASO;
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
                    velocidadY = -VELOCIDAD_PASO;
                if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
                    velocidadY = VELOCIDAD_PASO;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_A || key == KeyEvent.VK_D)
                    velocidadX = 0;
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_W || key == KeyEvent.VK_S)
                    velocidadY = 0;
            }
        });

        add(panelJuego);

        timer = new Timer(16, this);
        timer.start();

        panelJuego.requestFocusInWindow();
    }

    private Point obtenerCoordenadaSilla(int indice, int panelW, int panelH) {
        switch (indice) {
            case 0:
                return new Point((int) (panelW * 0.232), (int) (panelH * 0.42)); // Banqueta barra izquierda
            case 1:
                return new Point((int) (panelW * 0.368), (int) (panelH * 0.42)); // Banqueta barra centro
            case 2:
                return new Point((int) (panelW * 0.588), (int) (panelH * 0.42)); // Banqueta barra derecha
            case 3:
                return new Point((int) (panelW * 0.672), (int) (panelH * 0.30)); // Banqueta aislada derecha
            case 4:
                return new Point((int) (panelW * 0.055), (int) (panelH * 0.63)); // Silla blanca izquierda 1
            case 5:
                return new Point((int) (panelW * 0.145), (int) (panelH * 0.63)); // Silla blanca izquierda 2
            case 6:
                return new Point((int) (panelW * 0.628), (int) (panelH * 0.63)); // Silla gris mesa amarilla 1
            case 7:
                return new Point((int) (panelW * 0.718), (int) (panelH * 0.63)); // Silla gris mesa amarilla 2
            default:
                return new Point(100, 100);
        }
    }

    private void generarPosicionesAleatoriasNPCs() {
        listaNPCs.clear();
        List<Integer> todasLasSillas = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            todasLasSillas.add(i);
        }
        Collections.shuffle(todasLasSillas);

        if (todasLasSillas.size() >= 2) {
            int silla1 = todasLasSillas.get(0);
            int silla2 = todasLasSillas.get(1);

            listaNPCs.add(new NPC(silla1, "/src/img/milica.jpg"));
            listaNPCs.add(new NPC(silla2, "/src/img/epstein.jpg"));
        }
    }

    public void pedidoEntregado() {
        generarPosicionesAleatoriasNPCs();
        repaint();
    }

    /**
     * Abre la ventana de gestión. La crea solo la primera vez; después reutiliza
     * la misma instancia, por lo que los pedidos registrados se conservan.
     * La X de la ventana solo la oculta (HIDE_ON_CLOSE), no la destruye.
     */
    private void abrirVentanaGestion() {
        ventanaAbierta = true;
        velocidadX = 0;
        velocidadY = 0;

        SwingUtilities.invokeLater(() -> {
            if (ventanaGestion == null) {
                ventanaGestion = new VentanaPrincipal();

                // La X solo oculta la ventana: no se destruye ni se pierden los datos
                ventanaGestion.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

                // Al ocultarse, devolvemos el control al juego
                ventanaGestion.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentHidden(ComponentEvent e) {
                        ventanaAbierta = false;
                        panelJuego.requestFocusInWindow();
                    }
                });
            }
            ventanaGestion.setVisible(true);
            ventanaGestion.toFront();
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Pausar completamente el procesamiento del ciclo físico si el menú está
        // abierto
        if (ventanaAbierta) {
            velocidadX = 0;
            velocidadY = 0;
            return;
        }

        // Guardamos la posición previa segura
        int anteriorX = x;
        int anteriorY = y;

        x += velocidadX;
        y += velocidadY;

        int panelW = getWidth();
        int panelH = getHeight();

        if (x < 0)
            x = 0;
        if (x > panelW - PERSONAJE_ANCHO - 10)
            x = panelW - PERSONAJE_ANCHO - 10;
        if (y < 0)
            y = 0;
        if (y > panelH - PERSONAJE_ALTO - 35)
            y = panelH - PERSONAJE_ALTO - 35;

        Rectangle hitBoxPersonaje = new Rectangle(x, y, PERSONAJE_ANCHO, PERSONAJE_ALTO);

        // ESCENA 1: Salón de las mesas y barra
        if (escenaActual == 1) {
            for (NPC npc : listaNPCs) {
                Point posNpc = obtenerCoordenadaSilla(npc.indiceSilla, panelW, panelH);

                // Área de proximidad del NPC
                Rectangle areaInteraccionNpc = new Rectangle(
                        posNpc.x - 12,
                        posNpc.y - 12,
                        PERSONAJE_ANCHO + 24,
                        PERSONAJE_ALTO + 24);

                if (hitBoxPersonaje.intersects(areaInteraccionNpc)) {
                    if (!ventanaAbierta) {
                        // Separamos al personaje regresándolo un paso atrás de forma inmediata
                        x = anteriorX;
                        y = anteriorY;

                        abrirVentanaGestion();
                        break;
                    }
                }
            }

            // Entrada a la cocina (Puerta Blanca)
            Rectangle hitBoxPuertaBlanca = new Rectangle(
                    (int) (panelW * 0.28),
                    (int) (panelH * 0.10),
                    (int) (panelW * 0.09),
                    (int) (panelH * 0.23));

            if (hitBoxPersonaje.intersects(hitBoxPuertaBlanca)) {
                escenaActual = 2;
                x = (int) (panelW * 0.10);
                y = (int) (panelH * 0.80);
                velocidadX = 0;
                velocidadY = 0;
            }
        }
        // ESCENA 2: Cocina interna
        else if (escenaActual == 2) {
            Rectangle hitBoxSalidaRegreso = new Rectangle(0, (int) (panelH * 0.75), (int) (panelW * 0.08),
                    (int) (panelH * 0.25));
            if (hitBoxPersonaje.intersects(hitBoxSalidaRegreso)) {
                escenaActual = 1;
                x = (int) (panelW * 0.29);
                y = (int) (panelH * 0.35);
                velocidadX = 0;
                velocidadY = 0;
            }
            Rectangle hitBoxHornallas = new Rectangle((int) (panelW * 0.56), (int) (panelH * 0.36),
                    (int) (panelW * 0.16), (int) (panelH * 0.16));
            if (hitBoxPersonaje.intersects(hitBoxHornallas)) {
                if (!ventanaAbierta) {
                    y = hitBoxHornallas.y + hitBoxHornallas.height + 15;
                    abrirVentanaGestion();
                }
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new juego().setVisible(true));
    }
}