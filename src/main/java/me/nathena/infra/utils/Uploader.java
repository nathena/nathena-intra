package me.nathena.infra.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import me.nathena.infra.context.AppsContext;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.StringUtil;

import org.apache.commons.fileupload.FileItem;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class Uploader {

	/**
	 * 文件头部信息，十六进制信息，取前4位 
	 * JPEG (jpg)，文件头：FFD8FFe1
	 * PNG (png)，文件头：89504E47 
	 * GIF (gif)，文件头：47494638 
	 * TIFF (tif)，文件头：49492A00 
	 * Windows Bitmap (bmp)，文件头：424D
	 * CAD (dwg)，文件头：41433130 
	 * Adobe Photoshop (psd)，文件头：38425053 
	 * Rich Text Format (rtf)，文件头：7B5C727466 
	 * XML (xml)，文件头：3C3F786D6C HTML
	 * (html)，文件头：68746D6C3E 
	 * Email [thorough only]  (eml)，文件头：44656C69766572792D646174653A 
	 * Outlook Express (dbx)，文件头：CFAD12FEC5FD746F 
	 * Outlook (pst)，文件头：2142444E 
	 * MS Word/Excel (xls.or.doc)，文件头：D0CF11E0 
	 * MS Access (mdb)，文件头：5374616E64617264204A
	 * WordPerfect (wpd)，文件头：FF575043 
	 * Postscript (eps.or.ps)，文件头：252150532D41646F6265 
	 * Adobe Acrobat (pdf)，文件头：255044462D312E 
	 * Quicken (qdf)，文件头：AC9EBD8F
	 * Windows Password (pwl)，文件头：E3828596 
	 * ZIP Archive (zip)，文件头：504B0304 
	 * RAR Archive (rar)，文件头：52617221 
	 * Wave (wav)，文件头：57415645 
	 * AVI (avi)，文件头：41564920 
	 * Real Audio (ram)，文件头：2E7261FD 
	 * Real Media (rm)，文件头：2E524D46 
	 * MPEG (mpg)，文件头：000001BA 
	 * MPEG (mpg)，文件头：000001B3 
	 * Quicktime (mov)，文件头：6D6F6F76
	 * Windows Media (asf)，文件头：3026B2758E66CF11 
	 * MIDI (mid)，文件头：4D546864
	 * MP4 (mp4)，文件头：文件头：00000020667479706d70
	 */
	private static final String allowUploadFileType = "89504e47 504e470d ffd8ffe1 40496e69 ffd8ffe0 47494638 d0cf11e0 49545346 25504446 504B0304 52617221 00000020667479706d70";
	
	private static final String[] allowUploadFileSuffix = new String[]{"gif","jpg","jpeg","png","bmp","mp4"};
	
	public static String saveAsFile(HttpServletRequest request,String uploadName, String path, Object name) throws IOException,RuntimeException 
	{
		if( null == request || StringUtil.isEmpty(uploadName) || StringUtil.isEmpty(path) || StringUtil.isEmpty(name) )
		{
			throw new RuntimeException("上传文件参数有错");
		}
		
		String savedName = "";
		try
		{
			MultipartHttpServletRequest picRequest = (MultipartHttpServletRequest) request;
			MultipartFile tmpfile = picRequest.getFile(uploadName);
	
			if (null != tmpfile && 0<tmpfile.getSize() ) 
			{
				byte[] data = tmpfile.getBytes();
				if (!validateType(data)) 
				{
					throw new RuntimeException("不支持的上传类型");
				}
				
				//检查扩展名
				String tmpfileName = tmpfile.getOriginalFilename();
				String fileExt = tmpfileName.substring(tmpfileName.lastIndexOf(".") + 1).toLowerCase();
				if( null != fileExt )
				{
					if(!Arrays.<String>asList(allowUploadFileSuffix).contains(fileExt))
					{
						throw new RuntimeException("上传文件扩展名是不允许的扩展名。\n只允许" + allowUploadFileSuffix + "格式。");
					}
					
					savedName = saveFile(data, path, name + "." + fileExt);
				}
			}
			else
			{
				LogHelper.info(" ==== Uploader saveAsFile 上传文件 "+uploadName+" 为空");
			}
		}
		catch(ClassCastException e)
		{
			LogHelper.error(e.getMessage(), e);
		}

		return savedName;
	}
	
	public static String saveAsFile(HttpServletRequest request,String uploadName, String path, Object name,long maxSize) throws IOException,RuntimeException 
	{
		if( null == request || StringUtil.isEmpty(uploadName) || StringUtil.isEmpty(path) || StringUtil.isEmpty(name) )
		{
			throw new RuntimeException("上传文件参数有错");
		}
		
		String savedName = "";
		try
		{
			MultipartHttpServletRequest picRequest = (MultipartHttpServletRequest) request;
			MultipartFile tmpfile = picRequest.getFile(uploadName);

			if (null != tmpfile && 0<tmpfile.getSize() ) 
			{
				byte[] data = tmpfile.getBytes();
				if (!validateType(data)) 
				{
					throw new RuntimeException("不支持的上传类型");
				}
				
				//检查文件大小
				if(tmpfile.getSize() > maxSize)
				{
					throw new RuntimeException("上传文件大小超过限制。");
				}
				
				//检查扩展名
				String tmpfileName = tmpfile.getOriginalFilename();
				String fileExt = tmpfileName.substring(tmpfileName.lastIndexOf(".") + 1).toLowerCase();
				if( null != fileExt )
				{
					if(!Arrays.<String>asList(allowUploadFileSuffix).contains(fileExt))
					{
						throw new RuntimeException("上传文件扩展名是不允许的扩展名。\n只允许" + allowUploadFileSuffix + "格式。");
					}
					
					savedName = saveFile(data, path, name + "." + fileExt);
				}
			}
			else
			{
				LogHelper.info(" ==== Uploader saveAsFile 上传文件 "+uploadName+" 为空");
			}
		}
		catch(ClassCastException e)
		{
			LogHelper.error(e.getMessage(), e);
		}

		return savedName;
	}
	
	public static String saveAsFile(MultipartFile file, String path, Object name,long maxSize) throws IOException,RuntimeException 
	{
		if( null == file || StringUtil.isEmpty(path) || StringUtil.isEmpty(name) )
		{
			throw new RuntimeException("上传文件参数有错");
		}
		
		String savedName = "";
		try
		{
			if (null != file && 0<file.getSize() ) 
			{
				byte[] data = file.getBytes();
				if (!validateType(data)) 
				{
					throw new RuntimeException("不支持的上传类型");
				}
				
				//检查文件大小
				if(file.getSize() > maxSize)
				{
					throw new RuntimeException("上传文件大小超过限制。");
				}
				
				//检查扩展名
				String tmpfileName = file.getOriginalFilename();
				String fileExt = tmpfileName.substring(tmpfileName.lastIndexOf(".") + 1).toLowerCase();
				if( null != fileExt )
				{
					if(!Arrays.<String>asList(allowUploadFileSuffix).contains(fileExt))
					{
						throw new RuntimeException("上传文件扩展名是不允许的扩展名。\n只允许" + allowUploadFileSuffix + "格式。");
					}
					
					savedName = saveFile(data, path, name + "." + fileExt);
				}
			}
			else
			{
				LogHelper.info(" ==== Uploader saveAsFile 上传文件 "+file.getOriginalFilename()+" 为空");
			}
		}
		catch(ClassCastException e)
		{
			LogHelper.error(e.getMessage(), e);
		}

		return savedName;
	}
	
	public static String saveAsFile(FileItem item,String path, Object name,long maxSize) throws IOException,RuntimeException 
	{
		String savedName = "";
		
		if (null != item && !item.isFormField() && 0 < item.getSize()) 
		{
			String fileName = item.getName();
			byte[] data = item.get();
			if (!validateType(data)) 
			{
				throw new RuntimeException("不支持的上传类型");
			}
			
			//检查文件大小
			if(item.getSize() > maxSize)
			{
				throw new RuntimeException("上传文件大小超过限制。");
			}
			
			//检查扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if( null != fileExt )
			{
				if(!Arrays.<String>asList(allowUploadFileSuffix).contains(fileExt))
				{
					throw new RuntimeException("上传文件扩展名是不允许的扩展名。\n只允许" + allowUploadFileSuffix + "格式。");
				}
				
				savedName = saveFile(data, path, name + "." + fileExt);
			}
		}

		return savedName;
	}

	private static boolean validateType(byte[] b) 
	{
		if (b != null) 
		{
			int size = b.length;
			String hex = null;
			StringBuilder contentType = new StringBuilder();
			for (int i = 0; i < size; i++) 
			{
				hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) 
				{
					hex = "0" + hex;
				}
				contentType.append(hex);
				if (i > 2) 
				{
					break;
				}
			}
			
			if (allowUploadFileType.toLowerCase().indexOf(contentType.toString()) > -1) 
			{
				return true;
			}
		}
		return false;
	}
	
	private static String saveFile(byte[] data,String path,String fileName) throws IOException
	{
		String savePath = AppsContext.uploadDir().concat("/").concat(path);
		File foder = new File(savePath);
		if (!foder.exists()) 
		{
			if( !foder.mkdirs() )
			{
				LogHelper.error("上传文件时，创建文件夹"+foder.getAbsolutePath()+"失败");
				throw new RuntimeException("上传文件时，创建文件夹失败");
			}
		}

		File file = new File(savePath,fileName);
		
		FileCopyUtils.copy(data, file);

		return path + "/" + fileName;
	}
	
	private static String byteToHexString(byte[] b)
	{
		StringBuilder contentType = new StringBuilder();
		if (b != null) 
		{
			int size = b.length;
			String hex = null;
			
			for (int i = 0; i < size; i++) 
			{
				hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) 
				{
					hex = "0" + hex;
				}
				contentType.append(hex);
				if (i > 2) 
				{
					break;
				}
			}
		}
		return contentType.toString();
	}
	
	private static byte[] getFileBytes(String path) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		FileChannel fc = null;
		FileInputStream is = null;
		try
		{
			is = new FileInputStream(path);
			fc = is.getChannel();
			
			ByteBuffer bb = ByteBuffer.allocate(1024);
			
			while(fc.read(bb)>0)
			{
				baos.write(bb.array());
				bb.clear();
			}
			bb.clear();
		}
		catch(Exception e)
		{
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				fc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		return baos.toByteArray();
	}
	
	public static void main(String[] arg)
	{
		byte[] b = getFileBytes("/Users/nathena/Downloads/jytnn/images/banner33.png");
		
		System.out.println(byteToHexString(b));
	}
}
