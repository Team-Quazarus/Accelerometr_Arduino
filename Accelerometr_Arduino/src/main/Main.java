package main;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import static jssc.SerialPort.*;

import java.awt.Font;

import javax.swing.JFrame;

import frame.ConnectFrame;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {

	static SerialPort serial;

	public static Font progFont;//dfvbmfdkbfdk
	public int afysdyh;
	public static void main(String[] args) {
		String[] portList = SerialPortList.getPortNames();
		for (String string : portList) {
			System.out.println(string);
		}
		progFont = new Font("Arial", Font.TYPE1_FONT,20);
		boolean b=false;
		if(b){
			Main2.main(args);
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
	public static boolean createPort(String name){
		serial=new SerialPort(name);
		try {
			serial.openPort();
			serial.setParams(BAUDRATE_9600,DATABITS_8,STOPBITS_1 ,PARITY_NONE);
			
			serial.setEventsMask(SerialPort.MASK_RXCHAR);
			serial.addEventListener(new EventListener());
			serial.writeByte((byte)56);
			return true;
			//serial.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
			System.out.println(e.getExceptionType());
			return false;
		}
	}
	
	
 public static class EventListener implements SerialPortEventListener{

	@Override
	public void serialEvent(SerialPortEvent event) {
		 byte[] buffer=null;
		if(event.isRXCHAR()){
            try {
                 buffer = serial.readBytes(8);
                 for (byte b : buffer) {
					System.out.print(b);
				}
                 System.out.println();
            }
            catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
		
	}
		
	}
}

