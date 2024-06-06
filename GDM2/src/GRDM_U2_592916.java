import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class GRDM_U2_592916 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	public static void main(String args[]) {
		//new ImageJ();
		//IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
		IJ.open("./src/orchid.jpg");

		GRDM_U2_592916 pw = new GRDM_U2_592916();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp == null)
			imp = WindowManager.getCurrentImage();
		if (imp == null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}

	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int[]) ip.getPixels()).clone();
	}

	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class

	class CustomWindow extends ImageWindow implements ChangeListener {

		private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue; // Hue slider
		private double brightness;
		private double contrast;
		private double saturation;
		private double hue; // Hue value

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			Panel panel = new Panel();

			panel.setLayout(new GridLayout(4, 1));

			double[] brightnessArr = new double[257];
			for (int i = 0; i <= 256; i++) {
				brightnessArr[i] = i-128;
			}
			double[] contrastArr = new double[] {0, 20, 40, 60, 80, 100, 200, 400, 600, 800, 1000};
			double[] saturationArr = new double[] {0, 25, 50, 75, 100, 200, 300, 400, 500};

			double[] hueArr = new double[361];
			for (int i = 0; i < 361; i++) {
				hueArr[i] = i;
			}
			jSliderBrightness = makeTitledSilder("Helligkeit", brightnessArr, 0);
			jSliderContrast = makeTitledSilder("Kontrast", contrastArr, 100);
			jSliderSaturation = makeTitledSilder("Sättigung",saturationArr , 100);
			jSliderHue = makeTitledSilder("Farbe", hueArr, 0); // Hue slider
			panel.add(jSliderBrightness);
			panel.add(jSliderContrast);
			panel.add(jSliderSaturation);
			panel.add(jSliderHue);
			add(panel);

			pack();
		}

		private JSlider makeTitledSilder(String string, double[] intervals, int val) {
			double minVal = intervals[0];
			double maxVal = intervals[intervals.length - 1];

			JSlider slider = new JSlider(JSlider.HORIZONTAL, (int) minVal, (int) maxVal, val);
			Dimension preferredSize = new Dimension(width, 80);
			slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 15));
			slider.setBorder(tb);
			slider.setMajorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setSnapToTicks(true);



			slider.setPaintLabels(true);

			slider.addChangeListener(this);

			return slider;
		}

		private Hashtable<Integer, JLabel> createLabelTable(double[] intervals) {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			for (int i = 0; i < intervals.length; i++) {
				double value = intervals[i]/100.0;
				labelTable.put((int)intervals[i], new JLabel(String.valueOf(value)));
			}
			return labelTable;
		}



		private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
					str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 15));
			slider.setBorder(tb);
		}

		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider) e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness;
				setSliderTitle(jSliderBrightness, str);

			}

			if (slider == jSliderContrast) {
				double contrastValue = (slider.getValue())/100.0;
				contrast = contrastValue;
				String str = "Kontrast " + contrast;
				setSliderTitle(jSliderContrast, str);

			}

			if (slider == jSliderSaturation) {
				double saturationValue = (slider.getValue())/100.0;
				saturation = saturationValue;
				String str = "Sättigung " + saturation;
				setSliderTitle(jSliderSaturation, str);

			}

			if (slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Farbe " + hue;
				setSliderTitle(jSliderHue, str);

			}

			changePixelValues(imp.getProcessor());

			imp.updateAndDraw();
		}

		private void changePixelValues(ImageProcessor ip) {


			int[] pixels = (int[]) ip.getPixels();

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					int argb = origPixels[pos];

					double r = (argb >> 16) & 0xff;
					double g = (argb >> 8) & 0xff;
					double b = argb & 0xff;


					double Y = 0.299 * r + 0.587 * g + 0.114 * b;
					double U = (b - Y) * 0.493;
					double V = (r - Y) * 0.877;

					Y = Math.max(0, Math.min(255, Y + brightness));

					Y = Math.max(0, Math.min(255, (Y-128) * contrast + 128));
                    U= U * saturation;
                    V = V * saturation;


					r = Y + V / 0.877;
					g =(1/0.587) * Y - ((0.299 / 0.587) * r) - ((0.114 / 0.587) * b);
					b = Y + U / 0.493;




					double[] rotatedRGB = rotateHue(r, g, b, hue);

					int rn = (int) Math.max(0, Math.min(255, rotatedRGB[0]));
					int gn = (int) Math.max(0, Math.min(255, rotatedRGB[1] ));
					int bn = (int) Math.max(0, Math.min(255, rotatedRGB[2]));


					pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
		}


		private double[] rotateHue(double Y, double U, double V, double angle) {

			double radianAngle = Math.toRadians(angle);
			double cosAngle = Math.cos(radianAngle);
			double sinAngle = Math.sin(radianAngle);

			double VHue = (cosAngle * V - sinAngle * U);
			double UHue = (sinAngle * V + cosAngle * U);

			return new double[]{Y, UHue, VHue};
		}
	} // CustomWindow inner class
}
