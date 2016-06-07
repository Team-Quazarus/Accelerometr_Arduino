package main;



import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args){
		JFrame f = new JFrame("Катаныч");
		ImageIcon i = new ImageIcon("/catan.png");
		((JFrame) f).setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		
	}
}
