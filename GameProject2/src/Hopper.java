
import javax.swing.*;

public class Hopper {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Hopper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(new HopperPanel());
		
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		HopperPanel backgroundmusic = new HopperPanel();
		backgroundmusic.music();
	}

}
