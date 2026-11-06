package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class juego extends JFrame implements ActionListener {

    // ===================== CONSTANTES =====================

    // Todo el juego (posiciones, tamaños, colisiones) se calcula en este espacio
    // "virtual" de 1024x600. Al dibujar se escala al tamaño real del panel, así que
    // si se achica o agranda la ventana todo queda en el mismo lugar del fondo.
    private static final int BASE_ANCHO = 1024;
    private static final int BASE_ALTO = 600;

    private static final int PERSONAJE_ANCHO = 75;
    private static final int PERSONAJE_ALTO = 75;
    private static final int VELOCIDAD_PASO = 8;

    // Sprite sheet: 3 columnas x 2 filas (frame 0 = quieto, 1 a 5 = caminata)
    private static final int SPRITE_COLUMNAS = 3;
    private static final int SPRITE_FILAS = 2;
    private static final int FRAME_QUIETO = 0;
    private static final int FRAME_CAMINATA_MAX = 5;
    private static final int TICKS_POR_FRAME = 6;

    private static final int ESCENA_SALON = 1;
    private static final int ESCENA_COCINA = 2;

    // Distancia extra alrededor de cada NPC donde se activa la interacción.
    // 0 = hay que tocar al NPC. Aumentar el valor para activarla desde más lejos.
    private static final int MARGEN_INTERACCION = 0;

    // Posición de cada silla como proporción de la escena {x, y}
    private static final double[][] SILLAS = {
            { 0.232, 0.42 },
            { 0.368, 0.42 },
            { 0.588, 0.42 },
            { 0.672, 0.30 },
            { 0.055, 0.63 },
            { 0.145, 0.63 },
            { 0.628, 0.63 },
            { 0.718, 0.63 }
    };

    // ===================== ESTADO DEL PERSONAJE (coordenadas base)
    // =====================

    private int x = 100;
    private int y = 200;
    private int velocidadX = 0;
    private int velocidadY = 0;

    private int frameActual = FRAME_QUIETO;
    private int animacionContador = 0;

    // ===================== ESTADO DEL JUEGO =====================

    private int escenaActual = ESCENA_SALON;
    private boolean ventanaAbierta = false;

    // Evita que la ventana se vuelva a abrir mientras el personaje siga dentro del
    // área de un NPC (por ejemplo al volver de la cocina o al cerrar la ventana).
    // Se libera apenas el personaje sale de esa área.
    private boolean interaccionBloqueada = false;

    private final List<NPC> listaNPCs = new ArrayList<>();

    // ===================== RECURSOS Y COMPONENTES =====================

    private final Image spriteSheet;
    private final Image fondoSalonImg;
    private final Image fondoCocinaImg;

    private final JPanel panelJuego;
    private final Timer timer;
    private VentanaPrincipal ventanaGestion; // se crea una sola vez y se reutiliza

    // ===================== CLASE INTERNA: NPC =====================

    private static class NPC {
        final int indiceSilla;
        final Image img;

        NPC(int indiceSilla, String rutaImg) {
            this.indiceSilla = indiceSilla;
            this.img = cargarImagen(rutaImg);
        }
    }

    // ===================== CONSTRUCTOR =====================

    public juego() {
        setTitle("Pizzería - Java Swing");
        setSize(BASE_ANCHO, BASE_ALTO);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Los archivos deben estar en /src/img/
        spriteSheet = cargarImagen("/src/img/momo.png");
        fondoSalonImg = cargarImagen("/src/img/pizzeria.jpg");
        fondoCocinaImg = cargarImagen("/src/img/cocina.jpg");

        generarPosicionesAleatoriasNPCs();

        panelJuego = crearPanelJuego();
        add(panelJuego);

        timer = new Timer(16, this);
        timer.start();

        panelJuego.requestFocusInWindow();
    }

    private JPanel crearPanelJuego() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarEscena(g, getWidth(), getHeight());
                dibujarPersonaje(g, getWidth(), getHeight());
            }
        };

        panel.setBackground(Color.WHITE);
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ventanaAbierta) {
                    return;
                }
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                    velocidadX = -VELOCIDAD_PASO;
                }
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                    velocidadX = VELOCIDAD_PASO;
                }
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                    velocidadY = -VELOCIDAD_PASO;
                }
                if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                    velocidadY = VELOCIDAD_PASO;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT
                        || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
                    velocidadX = 0;
                }
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN
                        || key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
                    velocidadY = 0;
                }
            }
        });
        return panel;
    }

    // ===================== CARGA DE RECURSOS =====================

    private static Image cargarImagen(String ruta) {
        URL url = juego.class.getResource(ruta);
        return (url != null) ? new ImageIcon(url).getImage() : null;
    }

    // ===================== ESCALADO (base -> pantalla) =====================

    private static int escalarX(int valorBase, int panelW) {
        return valorBase * panelW / BASE_ANCHO;
    }

    private static int escalarY(int valorBase, int panelH) {
        return valorBase * panelH / BASE_ALTO;
    }

    // ===================== DIBUJADO =====================

    private void dibujarEscena(Graphics g, int panelW, int panelH) {
        if (escenaActual == ESCENA_SALON) {
            if (fondoSalonImg != null) {
                g.drawImage(fondoSalonImg, 0, 0, panelW, panelH, panelJuego);
            }
            dibujarNPCs(g, panelW, panelH);
        } else if (escenaActual == ESCENA_COCINA) {
            if (fondoCocinaImg != null) {
                g.drawImage(fondoCocinaImg, 0, 0, panelW, panelH, panelJuego);
            }
        }
    }

    private void dibujarNPCs(Graphics g, int panelW, int panelH) {
        int w = escalarX(PERSONAJE_ANCHO, panelW);
        int h = escalarY(PERSONAJE_ALTO, panelH);

        for (NPC npc : listaNPCs) {
            Point pos = obtenerCoordenadaSilla(npc.indiceSilla);
            int px = escalarX(pos.x, panelW);
            int py = escalarY(pos.y, panelH);

            if (npc.img != null) {
                g.drawImage(npc.img, px, py, w, h, panelJuego);
            } else {
                g.setColor(Color.BLUE); // respaldo si no cargó la imagen
                g.fillRect(px, py, w, h);
            }
        }
    }

    private void dibujarPersonaje(Graphics g, int panelW, int panelH) {
        int dx1 = escalarX(x, panelW);
        int dy1 = escalarY(y, panelH);
        int dx2 = escalarX(x + PERSONAJE_ANCHO, panelW);
        int dy2 = escalarY(y + PERSONAJE_ALTO, panelH);

        if (spriteSheet != null && spriteSheet.getWidth(panelJuego) > 0) {
            int frameW = spriteSheet.getWidth(panelJuego) / SPRITE_COLUMNAS;
            int frameH = spriteSheet.getHeight(panelJuego) / SPRITE_FILAS;

            int sx1 = (frameActual % SPRITE_COLUMNAS) * frameW;
            int sy1 = (frameActual / SPRITE_COLUMNAS) * frameH;

            g.drawImage(spriteSheet, dx1, dy1, dx2, dy2,
                    sx1, sy1, sx1 + frameW, sy1 + frameH, panelJuego);
        } else {
            g.setColor(Color.RED); // respaldo si no cargó la imagen
            g.fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
        }
    }

    // ===================== NPCs =====================

    /** Devuelve la posición de la silla en coordenadas base. */
    private Point obtenerCoordenadaSilla(int indice) {
        if (indice < 0 || indice >= SILLAS.length) {
            return new Point(100, 100);
        }
        return new Point(
                (int) (BASE_ANCHO * SILLAS[indice][0]),
                (int) (BASE_ALTO * SILLAS[indice][1]));
    }

    private void generarPosicionesAleatoriasNPCs() {
        listaNPCs.clear();

        List<Integer> sillas = new ArrayList<>();
        for (int i = 0; i < SILLAS.length; i++) {
            sillas.add(i);
        }
        Collections.shuffle(sillas);

        listaNPCs.add(new NPC(sillas.get(0), "/src/img/milica.jpg"));
        listaNPCs.add(new NPC(sillas.get(1), "/src/img/epstein.jpg"));
    }

    public void pedidoEntregado() {
        interaccionBloqueada = true; // los NPCs nuevos podrían aparecer pegados al personaje
        generarPosicionesAleatoriasNPCs();
        repaint();
    }

    // ===================== VENTANA DE GESTIÓN =====================

    private void abrirVentanaGestion() {
        ventanaAbierta = true;
        detenerMovimiento();

        SwingUtilities.invokeLater(() -> {
            if (ventanaGestion == null) {
                ventanaGestion = new VentanaPrincipal(this);
                ventanaGestion.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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

    // ===================== BUCLE DEL JUEGO =====================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ventanaAbierta) {
            velocidadX = 0;
            velocidadY = 0;
            return;
        }

        actualizarAnimacion();

        int anteriorX = x;
        int anteriorY = y;
        x += velocidadX;
        y += velocidadY;
        limitarAEscena();

        Rectangle hitBoxPersonaje = new Rectangle(x, y, PERSONAJE_ANCHO, PERSONAJE_ALTO);

        if (escenaActual == ESCENA_SALON) {
            actualizarSalon(hitBoxPersonaje, anteriorX, anteriorY);
        } else if (escenaActual == ESCENA_COCINA) {
            actualizarCocina(hitBoxPersonaje);
        }

        repaint();
    }

    private void actualizarAnimacion() {
        boolean enMovimiento = velocidadX != 0 || velocidadY != 0;

        if (enMovimiento) {
            animacionContador++;
            if (animacionContador >= TICKS_POR_FRAME) {
                frameActual++;
                if (frameActual > FRAME_CAMINATA_MAX) {
                    frameActual = 1; // cicla entre los frames de caminata (1 al 5)
                }
                animacionContador = 0;
            }
        } else {
            frameActual = FRAME_QUIETO;
            animacionContador = 0;
        }
    }

    private void limitarAEscena() {
        x = Math.max(0, Math.min(x, BASE_ANCHO - PERSONAJE_ANCHO));
        y = Math.max(0, Math.min(y, BASE_ALTO - PERSONAJE_ALTO));
    }

    private void detenerMovimiento() {
        velocidadX = 0;
        velocidadY = 0;
        frameActual = FRAME_QUIETO;
    }

    // ===================== LÓGICA POR ESCENA =====================

    /** Escena 1: salón con mesas y barra. */
    private void actualizarSalon(Rectangle hitBoxPersonaje, int anteriorX, int anteriorY) {

        // Interacción con los NPCs sentados (solo si el personaje los toca)
        boolean tocandoNPC = false;
        for (NPC npc : listaNPCs) {
            Point posNpc = obtenerCoordenadaSilla(npc.indiceSilla);
            Rectangle areaInteraccion = new Rectangle(
                    posNpc.x - MARGEN_INTERACCION,
                    posNpc.y - MARGEN_INTERACCION,
                    PERSONAJE_ANCHO + MARGEN_INTERACCION * 2,
                    PERSONAJE_ALTO + MARGEN_INTERACCION * 2);

            if (hitBoxPersonaje.intersects(areaInteraccion)) {
                tocandoNPC = true;
                break;
            }
        }

        if (!tocandoNPC) {
            interaccionBloqueada = false; // ya salió del área: se puede volver a interactuar
        } else if (!interaccionBloqueada) {
            interaccionBloqueada = true;
            x = anteriorX;
            y = anteriorY;
            abrirVentanaGestion();
        }

        // Puerta blanca: entrada a la cocina
        Rectangle puertaCocina = new Rectangle(
                (int) (BASE_ANCHO * 0.28),
                (int) (BASE_ALTO * 0.10),
                (int) (BASE_ANCHO * 0.09),
                (int) (BASE_ALTO * 0.23));

        if (hitBoxPersonaje.intersects(puertaCocina)) {
            escenaActual = ESCENA_COCINA;
            x = (int) (BASE_ANCHO * 0.10);
            y = (int) (BASE_ALTO * 0.80);
            velocidadX = 0;
            velocidadY = 0;
        }
    }

    /** Escena 2: cocina interna. */
    private void actualizarCocina(Rectangle hitBoxPersonaje) {

        // Salida de regreso al salón
        Rectangle salida = new Rectangle(
                0,
                (int) (BASE_ALTO * 0.75),
                (int) (BASE_ANCHO * 0.08),
                (int) (BASE_ALTO * 0.25));

        if (hitBoxPersonaje.intersects(salida)) {
            escenaActual = ESCENA_SALON;
            interaccionBloqueada = true; // aparece cerca de los NPCs: no abrir la ventana de inmediato
            x = (int) (BASE_ANCHO * 0.29);
            y = (int) (BASE_ALTO * 0.35);
            velocidadX = 0;
            velocidadY = 0;
        }

        // Hornallas: abre la ventana de gestión
        Rectangle hornallas = new Rectangle(
                (int) (BASE_ANCHO * 0.56),
                (int) (BASE_ALTO * 0.36),
                (int) (BASE_ANCHO * 0.16),
                (int) (BASE_ALTO * 0.16));

        if (hitBoxPersonaje.intersects(hornallas)) {
            y = hornallas.y + hornallas.height + 15;
            abrirVentanaGestion();
        }
    }

    // ===================== MAIN =====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new juego().setVisible(true));
    }
}