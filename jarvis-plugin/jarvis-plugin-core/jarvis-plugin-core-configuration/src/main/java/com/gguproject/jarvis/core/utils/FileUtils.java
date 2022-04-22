package com.gguproject.jarvis.core.utils;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FileUtils.class);
	
	public static boolean isDirectory(String directoryName) {
		File directory = new File(directoryName);
		return directory.exists() && directory.isDirectory();
	}
	
	public static boolean isFile(String file) {
		return new File(file).exists();
	}
	
	public static File getDirectory(String directoryName) {
		File directory = new File(directoryName);
		if(directory.exists() && directory.isDirectory()) {
			return directory;
		}
		return null;
	}
	
	public static List<String> readFile(String filename){
		return readFile(new File(filename));
	}
	
	/**
	 * Read a property file
	 * @param filename File name to read
	 * @return Return an array 1 entry = 1 line of the file
	 */
	public static List<String> readFile(File file){
		List<String> values = new ArrayList<String>();
		if(file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))){
				String line = "";
	            while ((line = br.readLine()) != null) {
	            		values.add(line);
	            }
			} catch (IOException e) {
				LOGGER.error("Not able to read file", e);
			}
		} else {
			LOGGER.warn("File: {} does not exists", file.getAbsolutePath());
		}
		
		return values;
	}
	
	/**
	 * Write a list of values in a file
	 * @param file File to write
	 * @param values values to write
	 */
	public static void writeFile(String filename, List<String> values) {
		writeFile(new File(filename), values);
	}
	
	/**
	 * Write a list of values in a file
	 * @param file File to write
	 * @param values values to write
	 */
	public static void writeFile(File file, List<String> values) {
		try (PrintWriter writer = new PrintWriter(file)){
			values.forEach(v -> {
				writer.print(v);
				writer.println();
			});
		} catch (FileNotFoundException e) {
			LOGGER.error("Not able to write values in file: {}", file, e);
		}
	}
	
	/**
	 * Copy a file to another
	 * @param source
	 * @param target
	 * @throws IOException 
	 */
	public static void copyFile(File source, File target) throws IOException {
		org.apache.commons.io.FileUtils.copyFile(source, target);
	}
	
	/**
	 * Delete a log a file
	 * @param file
	 * @return
	 */
	public static boolean deleteAndLogFile(File file) {
		if(file.delete()) {
			LOGGER.info("File deleted: {}", file);
			return true;
		} else {
			LOGGER.warn("Can't delete file: {}", file);
			return false;
		}
	}

	/**
	 * Delete a directory
	 * @param source
	 * @throws IOException
	 */
	public static void deleteDirectory(File source) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(source);
	}
	
	/**
	 * Unzip file
	 * @param zipFile
	 * @param destDir
	 * @throws IOException
	 */
	public static void unzipFile(File zipFile, File destDir) throws IOException {
        // create output directory if it doesn't exist
        if(!destDir.exists()) { 
        	destDir.mkdirs();
        }
        
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        
        try (FileInputStream fis = new FileInputStream(zipFile); ZipInputStream zis = new ZipInputStream(fis)){
System.out.println("--------- UNZIP");
        	ZipEntry ze;
            while((ze = zis.getNextEntry()) != null){
            	String fileName = ze.getName();
            	System.out.println(fileName);
            	if(ze.isDirectory()) {
            		new File(destDir, fileName).mkdirs();
            		//first one should be /data directory -> rename to /data-tmp
            	} else {
	                File newFile = new File(destDir, fileName);
	
	                //create directories for sub directories in zip
	                new File(newFile.getParent()).mkdirs();
	                
	                try (FileOutputStream fos = new FileOutputStream(newFile)){
		                int len;
		                while ((len = zis.read(buffer)) > 0) {
		                	fos.write(buffer, 0, len);
		                }
	                }
            	}

                //close this ZipEntry
                zis.closeEntry();
            }
            
            //close last ZipEntry
            zis.closeEntry();
        }
    }
}
