package main;

import javax.swing.JFrame;

import frame.Testokno;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
public class Main2 {
	static SerialPort serialPort;
	static Testokno okno;
	public static void main(String ... args) {
		String[] list = SerialPortList.getPortNames();
		for (String string : list) {
			System.out.println(string);
		}
		 okno = new Testokno();
		okno.setSize(800, 600);
		okno.setVisible(true);
		
		 serialPort = new SerialPort(args[0]);
		
		try {
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			serialPort.writeByte((byte)'C');
			serialPort.addEventListener(new PortReader());
		} catch (SerialPortException e) {
			
			e.printStackTrace();
		}
		
	}
	 private static class PortReader implements SerialPortEventListener {
public static int sum, count,count1;
	        public void serialEvent(SerialPortEvent event) {
	            if(event.isRXCHAR() && event.getEventValue() > 0){
	                try {
	                    //�������� ����� �� ����������, ������������ ������ � �.�.
	                    int[] data = serialPort.readIntArray(event.getEventValue());
	                    //� ����� ���������� ������
	                   sum=data[0];
	                  
	                   count++;
	                 
	                  
	                	   okno.series.add(count1, sum);
	 
	                	   okno.updategraph();
  
	                   }
	                catch (SerialPortException ex) {
	                    System.out.println(ex);
	                }
	            }
	        }
	    }

}
