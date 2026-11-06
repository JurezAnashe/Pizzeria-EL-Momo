package src;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class Musica {

    private Clip clip;

    public Musica(String ruta) {
        try (InputStream is = Musica.class.getResourceAsStream(ruta)) {
            if (is == null) {
                System.err.println("No se encontró la música: " + ruta);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(audio);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la música: " + e.getMessage());
        }
    }

    public void reproducirEnBucle() {
        if (clip != null && !clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void pausar() {
        if (clip != null) {
            clip.stop();
        }
    }

    public void detener() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public boolean estaSonando() {
        return clip != null && clip.isRunning();
    }

    /** Volumen de 0.0 (silencio) a 1.0 (máximo). */
    public void setVolumen(float volumen) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = (volumen <= 0.0001f) ? control.getMinimum() : (float) (20 * Math.log10(volumen));
        control.setValue(Math.max(control.getMinimum(), Math.min(control.getMaximum(), db)));
    }
}