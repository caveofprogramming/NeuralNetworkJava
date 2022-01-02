package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private Random random = new Random();
	
	class Label extends JLabel {
		
		private static final long serialVersionUID = 1L;
		
		private BufferedImage image;
		private Graphics gi;
		private int width;
		private int height;
		
		public Label(int width, int height) {
			
			this.width = width;
			this.height = height;
			
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			gi = image.getGraphics();
			
			gi.setColor(Color.black);
			gi.fillRect(0, 0, width, height);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			
			int centerX = width/2;
			int centerY = height/2;
			
			// Draw random dots restricted to a square in the center.
			int x = (int)((2 * random.nextDouble() - 1) * width/4); 
			int y = (int)((2 * random.nextDouble() - 1) * height/4);
			
			// Draw random dots with a standard deviation that's small
			// compared to the width and height.
			//int x = (int)(random.nextGaussian() * width/16); 
			//int y = (int)(random.nextGaussian() * height/16);
			
			gi.setColor(new Color(255, 0, 0, 100));
			gi.fillOval(centerX + x, centerY + y, 4, 4);
			g.drawImage(image, 0, 0, null);
		}
	}

	public MainWindow() {
		
		int width = 800;
		int height = 800;
		
		Label label = new Label(width, height);
		
		Timer timer = new Timer(50, e->label.repaint());
		timer.start();
		
		setContentPane(label);
		
		setSize(width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

}

public class App {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainWindow::new);
	}
}
