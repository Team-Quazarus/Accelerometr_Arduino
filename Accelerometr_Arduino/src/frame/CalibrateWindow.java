package frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.omg.Messaging.SyncScopeHelper;

import connect.ComUtil;
import main.Main;

import static connect.ComUtil.listeneric;




public class CalibrateWindow extends JFrame implements ActionListener{
	public Float Sz=Float.valueOf(1),Sy=Float.valueOf(1),Sx=Float.valueOf(1);
	public Float z0=Float.valueOf(0),y0=Float.valueOf(0),x0=Float.valueOf(0);
	private float[] iksu = new float[7]; 
	private float[] ygreki = new float[7];
	private float[] zetu = new float[7];
	public JLabel lblX,lblY,lblZ;
	public JButton button,button_1,button_2,button_3,button_4,button_5,button_6,button_7;
	
	
	public CalibrateWindow(MainWindow mainWindow) {
		setTitle("Калибровка");
		setSize(800, 329);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		JPanel panel = new JPanel();
		panel.setBounds(481, 27, 282, 257);
		panel.setBackground(Color.red);
		
		 button = new JButton("Назад");
		button.setBounds(10, 27, 87, 23);
		button.addActionListener(this);
		button.setActionCommand("nazad");
		
		 button_1 = new JButton("ОК");
		button_1.setBounds(115, 27, 89, 23);
		button_1.addActionListener(this);
		button_1.setActionCommand("okay");
		
		 button_2 = new JButton("ЗАД");
		button_2.setBounds(10, 68, 83, 23);
		button_2.addActionListener(this);
		button_2.setActionCommand("zad");
		
		 button_3 = new JButton("Перед");
		button_3.setBounds(115, 68, 89, 23);
		button_3.addActionListener(this);
		button_3.setActionCommand("pered");
		
		
		 button_4 = new JButton("НИЗ");
		button_4.setBounds(10, 109, 83, 23);
		button_4.addActionListener(this);
		button_4.setActionCommand("niz");
		
		 button_5 = new JButton("Верх");
		button_5.setBounds(115, 109, 89, 23);
		button_5.addActionListener(this);
		button_5.setActionCommand("vverx");
		
		 button_6 = new JButton("ЛЕВО");
		button_6.setBounds(10, 143, 83, 23);
		button_6.addActionListener(this);
		button_6.setActionCommand("levo");
		
		 button_7 = new JButton("ПРАВО");
		button_7.setBounds(115, 143, 89, 23);
		button_7.addActionListener(this);
		button_7.setActionCommand("pravo");
		
		
		
		
		JLabel x = new JLabel("X");
		x.setBounds(249, 31, 20, 14);
		
		JLabel y = new JLabel("Y");
		y.setBounds(308, 31, 18, 14);
		
		JLabel z = new JLabel("Z");
		z.setBounds(375, 31, 24, 14);
		
		lblX = new JLabel("0.00");
		lblX.setBounds(239, 56, 53, 29);		
		lblY = new JLabel("0.00");
		lblY.setBounds(302, 59, 53, 23);
		
		lblZ = new JLabel("0.00");
		lblZ.setBounds(365, 59, 63, 22);
		
		getContentPane().setLayout(null);
		getContentPane().add(lblX);
		getContentPane().add(lblY);
		getContentPane().add(lblZ);
		getContentPane().add(button);
		getContentPane().add(button_1);
		getContentPane().add(button_2);
		getContentPane().add(button_6);
		getContentPane().add(button_4);
		getContentPane().add(button_3);
		getContentPane().add(button_7);
		getContentPane().add(button_5);
		getContentPane().add(x);
		getContentPane().add(y);
		getContentPane().add(z);
		getContentPane().add(panel);
		
		JButton button_8 = new JButton("Сброс");
		button_8.setBounds(34, 200, 89, 23);
		getContentPane().add(button_8);
		button_8.addActionListener(this);
		button_8.setActionCommand("vnoll");
		
		JButton button_9 = new JButton("Калибровка");
		button_9.setBounds(150, 200, 119, 23);
		getContentPane().add(button_9);
		button_9.addActionListener(this);
		button_9.setActionCommand("cali");//не показывать
	}

	
	private void calibration(){
		float xss=0,yss=0,zss=0;
		for(int i=1;i<5;i++){
			xss+=iksu[i];
			yss+=ygreki[i];
			zss+=zetu[i];
		}
		xss/=4;
		yss/=4;
		zss/=4;
		float az=0;
		Sz=zetu[5]-zetu[6];//пределение чувствительности
		Sz/=2;
		
		Sy=ygreki[5]-ygreki[6];
		Sy/=2;
		
		Sx=iksu[5]-iksu[6];
		Sx/=2;
		
		Sx=Math.abs(Sx);
		Sy=Math.abs(Sy);
		Sz=Math.abs(Sz);
		//этап установки нуля
		
		z0=zetu[0]-1*Sz;
		
		x0=iksu[0]-1*Sx;
		
		y0=ygreki[0]-1*Sy;
		
		System.out.println(x0);
		System.out.println(y0);
		System.out.println(z0);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		switch (e.getActionCommand()){
		case "nazad":
			ComUtil.sendCom('D');
			ComUtil.sendCom('D');
			this.dispose();
			Main.mainWindow.setVisible(true);
			break;
			
		case "okay":
			break;
			
		case "zad":
			iksu[1]=listeneric.x;
			ygreki[6]=listeneric.y;
			zetu[1]=listeneric.z;
			button_2.setEnabled(false);
			break;
			
		case "pered":
			ygreki[0]=listeneric.y;
			iksu[2]=listeneric.x;
			ygreki[5]=listeneric.y;
			zetu[2]=listeneric.z;
			button_3.setEnabled(false);
			break;
		
		case "niz":
			zetu[0]=listeneric.z;
			iksu[3]=listeneric.x;
			ygreki[1]=listeneric.y;
			zetu[5]=listeneric.z;
			button_4.setEnabled(false);
			break;
		
		case "vverx":
			iksu[4]=listeneric.x;
			ygreki[2]=listeneric.y;
			zetu[6]=listeneric.z;
			button_5.setEnabled(false);
			break;
		
		case "levo":
			iksu[0]=listeneric.x;
			iksu[6]=listeneric.x;
			ygreki[3]=listeneric.y;
			zetu[3]=listeneric.z;
			button_6.setEnabled(false);
			break;
		
		case "pravo":
			iksu[5]=listeneric.x;
			ygreki[4]=listeneric.y;
			zetu[4]=listeneric.z;
			button_7.setEnabled(false);
			break;
		
		case "vnoll":
			button.setEnabled(true);
			button_1.setEnabled(true);
			button_2.setEnabled(true);
			button_3.setEnabled(true);
			button_4.setEnabled(true);
			button_5.setEnabled(true);
			button_6.setEnabled(true);
			button_7.setEnabled(true);
			break;
		case "cali":
			calibration();
			break;
		}
		
	}
}
