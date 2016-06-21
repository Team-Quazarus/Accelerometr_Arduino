package connect;



import java.util.Locale;

import org.jfree.data.time.Millisecond;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import static main.Main.*;


public class ComListener implements SerialPortEventListener {
	int t=0;
	@Deprecated
	private final int BYTES_COUNT=16;//колличество байт в сообщении (2 перенос, 4 float/integer, 1 символ)
	byte[] m = new byte[BYTES_COUNT];
	public  float x,y,z;
	private Float x0,y0,z0,Sz,Sy,Sx;
	public ComListener() {
		
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		byte[] buffer=null;
		
		 
			if(event.isRXCHAR()){
	            try {
	                 buffer = ComUtil.serial.readBytes(16);
	                // m[c]=buffer[0];	
					
						//System.out.println(new String(m)+" "+c);
						String sy = new String(buffer, 0,4);
						String sz = new String(buffer, 5,4);
						String sx = new String(buffer, 10,4);
						
						this.x=Float.parseFloat(sx);
						this.y=Float.parseFloat(sy);
						this.z=Float.parseFloat(sz);
						
						mainWindow.ygrek.setText(sy);
						mainWindow.zetik.setText(sz);
						mainWindow.ikasa.setText(sx);
						if(calibrwind!=null){
							calibrwind.lblX.setText(format.format(((this.x)-calibrwind.x0)/calibrwind.Sx));
							calibrwind.lblY.setText(format.format(((this.y-calibrwind.y0)/calibrwind.Sy)));
							calibrwind.lblZ.setText(format.format(((this.z-calibrwind.z0)/calibrwind.Sz)));
						}
					
						t++;
						if((t%10)==0)
						mainWindow.updateVal(this.y,this.x,this.z);
						if(t==Integer.MAX_VALUE) t=0;//вдруг переполнится будет страшно
					
			 
	            }
	            catch (SerialPortException ex) {
	                System.out.println(ex);
	            }
	        }
			
		}
	{ System.out.println("Где Лёша?");}
	}


