package file;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class FileUtils {
	
	static String path;
	
	
	public static File createFile(String path){
		
		
		return null;
	}
	public static File openFile(){
		
		return null;
	}
	
	public static Scanner readFile(){
		
		
		return null;
	}
	
	public static void writeFile(String str){
		
	}
	public  URL getpravilnuiPath(String path){
		return getClass().getClassLoader().getResource(path);
	}

}
