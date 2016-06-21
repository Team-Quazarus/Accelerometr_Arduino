package connect;


import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.PARITY_NONE;


import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class ComUtil {
	
	 static SerialPort serial;
	public static ComListener listeneric = new ComListener();
	public static String[] getPortList(){
		return SerialPortList.getPortNames();
	}
	public static void sendCom(char c){
		try {
			serial.writeByte((byte)c);
		} catch (SerialPortException e) {
			System.err.println("Воткни кабель обратно");
			e.printStackTrace();
		}
	}
	/**
	 * @param name
	 * @return
	 */
	public static boolean createPort(String name){
		serial=new SerialPort(name);
		try {
			serial.openPort();
			serial.setParams(SerialPort.BAUDRATE_9600,DATABITS_8,SerialPort.STOPBITS_1 ,PARITY_NONE);
			
			
			serial.setEventsMask(SerialPort.MASK_RXCHAR);
			serial.addEventListener(listeneric);
			sendCom('T');
			return true;
			//serial.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
			System.out.println(e.getExceptionType());
			return false;
		}
	}
	public static void closePort(){
		try {
			serial.closePort();
		} catch (SerialPortException e) {
			System.err.println("Какаято ошибка");
			e.printStackTrace();
		}
	}

}
