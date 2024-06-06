import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 Opens an image window and adds a panel below the image
 */
public class GRDM_U3 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Rot-Kanal", "Negativ", "Graustufen", "Binärbild", "5 Graustufen", "27 Graustufen", "Fehlerdiffusion", "Sepia", "9 Farben"};


    public static void main(String args[]) {

        IJ.open("./src/bear.jpg");

        GRDM_U3 pw = new GRDM_U3();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp==null)
            imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int []) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[]) ip.getPixels();

            if (method.equals("Original")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Rot-Kanal")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        //int g = (argb >>  8) & 0xff;
                        //int b =  argb        & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Graustufen")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        double Y = 0.299 * r + 0.587 * g + 0.114 * b;


                        int rn = (int) Y;
                        int gn = (int) Y;
                        int bn = (int) Y;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Negativ")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = 255 - r;
                        int gn = 255 - g;
                        int bn = 255 - b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("Binärbild")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        double Y = 0.299 * r + 0.587 * g + 0.114 * b;

                        if (Y >= 128) {
                            Y = 255;
                        } else {
                            Y = 0;
                        }

                        int rn = (int) Y;
                        int gn = (int) Y;
                        int bn = (int) Y;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("5 Graustufen")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        double Y = 0.299 * r + 0.587 * g + 0.114 * b;
                        double U = (b - Y) * 0.493;
                        double V = (r - Y) * 0.877;

                        int delta = 255 / 5;

                        if (0 < Y && Y < delta) {
                            Y = delta / 2;
                        }
                        if (delta < Y && Y < 2 * delta) {
                            Y = 2 * delta - delta / 2;
                        }
                        if (2 * delta < Y && Y < 3 * delta) {
                            Y = 3 * delta - delta / 2;
                        }
                        if (3 * delta < Y && Y < 4 * delta) {
                            Y = 4 * delta - delta / 2;
                        }
                        if (4 * delta < Y && Y < 255) {
                            Y = 255 - delta / 2;
                        }


                        int rn = (int) Y;
                        int gn = (int) Y;
                        int bn = (int) Y;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("27 Graustufen")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        double Y = 0.299 * r + 0.587 * g + 0.114 * b;

                        int delta = 255 / 27;
                        int index = (int) (Y / delta);

                        Y = Math.min(255, Math.max(0, index * delta + delta / 2));

                        int rn = (int) Y;
                        int gn = (int) Y;
                        int bn = (int) Y;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("Fehlerdiffusion")) {
                int range = 255;
                int err = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn, gn, bn;

                        int colorSum = r + g + b + err;

                        if (colorSum < range) {
                            rn = 0;
                            gn = 0;
                            bn = 0;

                        } else {
                            rn = 255;
                            gn = 255;
                            bn = 255;
                        }
                        err = (colorSum - (rn + gn + bn));

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("Sepia")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;


                        int rn = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                        int gn = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                        int bn = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                        rn = Math.min(255, Math.max(0, rn));
                        gn = Math.min(255, Math.max(0, gn));
                        bn = Math.min(255, Math.max(0, bn));
                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
            if (method.equals("9 Farben")) {


                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        float [] hsv = new float[3];
                        Color.RGBtoHSB(r,g,b,hsv);
                        String hueStr = String.format("%.1f",hsv[0]);
                        String satStr = String.format("%.1f",hsv[1]);
                        String briStr = String.format("%.1f",hsv[2]);


                        float hue = Float.valueOf(hueStr);
                        float sat = Float.valueOf(satStr);
                        float bri = Float.valueOf(briStr);

                        int color = Color.HSBtoRGB(hue,sat,bri);


                        pixels[pos] = color;

                    }
                }
            }


        } // CustomWindow inner class
    }


}