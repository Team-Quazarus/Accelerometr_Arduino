package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CofigU  {
	private String path="config/conf.cfg";

	private Map<String,Object> barahlo;
	public CofigU()  {
		File f = new File(path);
		if(f.isFile()){
		deSerializCfg();
		}else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			barahlo = new HashMap<>();
		}
	}
	
	public void serializCfg(){
		 FileOutputStream fos;
		 ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(path);
			oos = new ObjectOutputStream(fos);
			  oos.writeObject(barahlo);
			  oos.flush();
			  oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
		 
	}
	public void deSerializCfg(){
		FileInputStream fis;
		 ObjectInputStream oin;
		try {
			fis = new FileInputStream("temp.out");
			oin = new ObjectInputStream(fis);
			barahlo= (Map<String,Object>)oin.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public boolean contain(String key){
		return barahlo.containsKey(key);
	}
	public Object getObj(String key){
		return barahlo.get(key);
	}
	
	public void addObj(String key,Object obj){
		barahlo.put(key, obj);
	}

	

}
