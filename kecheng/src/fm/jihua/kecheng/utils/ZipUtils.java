package fm.jihua.kecheng.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	
	public static void unZip(String unZipfileName, String mDestPath) {  
	       if (!mDestPath.endsWith("/")) {  
	           mDestPath = mDestPath + "/";  
	       }  
	       FileOutputStream fileOut = null;  
	       ZipInputStream zipIn = null;  
	       ZipEntry zipEntry = null;  
	       File file = null;  
	       int readedBytes = 0;  
	       byte buf[] = new byte[4096];  
	       try {  
	    	   zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unZipfileName)));
	           while ((zipEntry = zipIn.getNextEntry()) != null) {  
	               file = new File(mDestPath + zipEntry.getName());  
	               if (zipEntry.isDirectory()) {  
	                   file.mkdirs();  
	               } else {  
	                   // 如果指定文件的目录不存在,则创建之.  
	                   File parent = file.getParentFile();  
	                   if (!parent.exists()) {  
	                       parent.mkdirs();  
	                   }  
	                   fileOut = new FileOutputStream(file);  
	                   while ((readedBytes = zipIn.read(buf)) > 0) {  
	                       fileOut.write(buf, 0, readedBytes);  
	                   }  
	                   fileOut.close();  
	               }  
	               zipIn.closeEntry();  
	           }  
	       } catch (IOException ioe) {  
	           ioe.printStackTrace();  
	       }  
	   }  
	
	public static void unZip(InputStream inputStream, String mDestPath) {  
	       if (!mDestPath.endsWith("/")) {  
	           mDestPath = mDestPath + "/";  
	       }  
	       FileOutputStream fileOut = null;  
	       ZipInputStream zipIn = null;  
	       ZipEntry zipEntry = null;  
	       File file = null;  
	       int readedBytes = 0;  
	       byte buf[] = new byte[4096];  
	       try {  
	    	   zipIn = new ZipInputStream(new BufferedInputStream(inputStream));
	           while ((zipEntry = zipIn.getNextEntry()) != null) {  
	               file = new File(mDestPath + zipEntry.getName());  
	               if (zipEntry.isDirectory()) {  
	                   file.mkdirs();  
	               } else {  
	                   // 如果指定文件的目录不存在,则创建之.  
	                   File parent = file.getParentFile();  
	                   if (!parent.exists()) {  
	                       parent.mkdirs();  
	                   }  
	                   fileOut = new FileOutputStream(file);  
	                   while ((readedBytes = zipIn.read(buf)) > 0) {  
	                       fileOut.write(buf, 0, readedBytes);  
	                   }  
	                   fileOut.close();  
	               }  
	               zipIn.closeEntry();  
	           }  
	       } catch (IOException ioe) {  
	           ioe.printStackTrace();  
	       }  
	   }  
	
	 /** 
     * 解压一个压缩文档 到指定位置 
     * @param zipFileString 压缩包的名字 
     * @param outPathString 指定的路径 
     * @throws Exception 
     */  
//    public static void UnZipFolder(String zipFileString, String outPathString)throws Exception {  
//        android.util.Log.v("XZip", "UnZipFolder(String, String)");
//        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
//        java.util.zip.ZipEntry zipEntry;
//        String szName = "";
//        while ((zipEntry = inZip.getNextEntry()) != null) {  
//            szName = zipEntry.getName();
//            if (zipEntry.isDirectory()) {
//                // get the folder name of the widget  
//                szName = szName.substring(0, szName.length() - 1);  
//                java.io.File folder = new java.io.File(outPathString + java.io.File.separator + szName);  
//                folder.mkdirs();
//            } else {
//                java.io.File file = new java.io.File(outPathString + java.io.File.separator + szName);  
//                file.createNewFile();  
//                // get the output stream of the file  
//                java.io.FileOutputStream out = new java.io.FileOutputStream(file);  
//                int len;  
//                byte[] buffer = new byte[1024];  
//                // read (len) bytes into buffer  
//                while ((len = inZip.read(buffer)) != -1) {  
//                    // write (len) byte from buffer at the position 0  
//                    out.write(buffer, 0, len);  
//                    out.flush();  
//                }  
//                out.close();  
//            }  
//        }//end of while  
//        inZip.close();  
//    }//end of func 
}
