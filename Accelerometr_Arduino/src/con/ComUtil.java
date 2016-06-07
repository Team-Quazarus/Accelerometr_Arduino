package con;

import jssc.SerialPortList;

public class ComUtil {
	public static String[] getPortList(){
		return SerialPortList.getPortNames();
	}

}
