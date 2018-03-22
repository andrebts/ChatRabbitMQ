package br.ufs.dcomp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;

public class FileUtils {
	public static final String TYPE_NONE = "";
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_IMAGE = "image/png";
	public static final String TYPE_PDF = "application/pdf";

	public static String obterArquivosBase64(String caminho) throws IOException{
	    /*File file = new File(caminho);
		String encoded = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));*/

		return null;
	}
	
	/*public byte[] loadFile(String fileName) {
	    File file = new File(fileName);
	 
	    byte[] bytes = new byte[(int) file.length()];
	    try {
	        FileInputStream fileInputStream = new FileInputStream(file);
	        fileInputStream.read(bytes);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	 
	    return bytes;
	}*/
	
	public static String obterTipoArquivo(String caminho){
		String ext = "";

		int i = caminho.lastIndexOf('.');
		if (i >= 0) {
		    ext = caminho.substring(i + 1);
		}
		
		if (ext.toUpperCase().equals("PDF")) 
			return "application/pdf";
   
		if (ext.toUpperCase().equals("PNG")) 
			return "image/png";
   
   		return "text/plain";
	}
	
	public static byte[] toByteArray(String path) throws IOException {
		byte[] array = Files.readAllBytes(new File(path).toPath());
		return array;
	}
	
	public static byte[] read(String path) throws IOException {
		File file = new File(path);
	    if (file.length() > 50000) {
	        System.err.println("Arquivo muito grande");
	    }

	    byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); //read file into bytes[]
		fis.close();
		
		return bytesArray;
	}
}
