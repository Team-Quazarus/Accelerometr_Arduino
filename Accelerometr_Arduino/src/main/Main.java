package main;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import static jssc.SerialPort.*;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import connect.ComUtil;
import frame.CalibrateWindow;
import frame.ConnectFrame;
import frame.MainWindow;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {

	
	public static MainWindow mainWindow;
	public static CalibrateWindow calibrwind;
	public static Font progFont;//dfvbmfdkbfdk
	public static DecimalFormat format = new  DecimalFormat("##0.00");
	public static CofigU conf;
	
	public static void main(String[] args) {
		String[] portList = SerialPortList.getPortNames();
		for (String string : portList) {
			System.out.println(string);
		}
		progFont = new Font("Arial", Font.TYPE1_FONT,20);
		conf = new CofigU();
		boolean b=false;
		if(b){
		
			
		}else{
			JFrame f = new ConnectFrame();
			f.setVisible(true);
		}
	/*	 serial = new SerialPort("/dev/ttyUSB0");
		try {
			serial.openPort();
			serial.setParams(BAUDRATE_9600,DATABITS_8,STOPBITS_1 ,PARITY_NONE);
			
			serial.setEventsMask(SerialPort.MASK_RXCHAR);
			//serial.addEventListener(new EventListener());
			//serial.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
			System.out.println(e.getExceptionType());
		}*/
		
	

	}
	
}

