package me.nathena.infra.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * @author nathena
 * 
 */
public final class HttpUtil {

	public static String doGet( String url,Map<String, String> headers )
	{
		 StringBuffer responseBoby = new StringBuffer();
		 BufferedReader in = null;
		 try 
		 {
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 URLConnection connection = realUrl.openConnection();
			 /**设置通用的请求属性**/
			 connection.setRequestProperty("accept", "*/*");
			 connection.setRequestProperty("connection", "Keep-Alive");
			 connection.setRequestProperty("Charset", "utf-8");
			 connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			 if( null!=headers && headers.size()>0 )
			 {
				 for(String key : headers.keySet() )
				 {
					 connection.setRequestProperty(key, headers.get(key));
				 }
			 }
			 /**建立实际的连接**/
			 connection.connect();
			 /**定义 BufferedReader输入流来读取URL的响应**/
			 in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			 String line;
			 while ((line = in.readLine()) != null) 
			 {
				 responseBoby.append(line);
			 }
		} 
	 	catch (Exception e) 
	 	{
	 		LogHelper.error(e.getMessage(), e);
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return responseBoby.toString();
	}
	
	public static String doPost(String url,Map<String, String> params,Map<String, String> headers)
	{
		StringBuilder responseBoby = new StringBuilder();
		BufferedReader in = null;
		PrintWriter out = null;
		try 
		{
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 URLConnection connection = realUrl.openConnection();
			 /**设置通用的请求属性**/
			
			 connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			 connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			 connection.setRequestProperty("Connection", "Keep-Alive");
			 connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 connection.setRequestProperty("Charset", "utf-8");
			 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:27.0) Gecko/20100101 Firefox/27.0");
			 if( null!=headers && headers.size()>0 )
			 {
				 for(String key : headers.keySet() )
				 {
					 connection.setRequestProperty(key, headers.get(key));
				 }
			 }
			 /**发送POST请求必须设置如下两行**/
			 connection.setDoOutput(true);
			 connection.setDoInput(true);
			 /**获取URLConnection对象对应的输出流**/
			 out = new PrintWriter(connection.getOutputStream());
			 /**发送请求参数**/
			 String param = parseParams(params);
			 out.print(param);
			 /**flush输出流的缓冲**/
			 out.flush();
			 /**定义 BufferedReader输入流来读取URL的响应**/
			 in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			 String line;
			 while ((line = in.readLine()) != null) 
			 {
				 responseBoby.append(line);
			 }
		} 
	 	catch (Exception e) 
	 	{
	 		LogHelper.error(e.getMessage(), e);
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(out != null) { out.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return responseBoby.toString();
	}
	
	public static byte[] doGetBytes( String url ,Map<String, String> headers )
	{
		 ByteArrayOutputStream  baos = new ByteArrayOutputStream();
		 InputStream in = null;
		 try 
		 {
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 URLConnection connection = realUrl.openConnection();
			 /**设置通用的请求属性**/
			 connection.setRequestProperty("accept", "*/*");
			 connection.setRequestProperty("Charset", "utf-8");
			 connection.setRequestProperty("connection", "Keep-Alive");
			 if( null!=headers && headers.size()>0 )
			 {
				 for(String key : headers.keySet() )
				 {
					 connection.setRequestProperty(key, headers.get(key));
				 }
			 }
			 
			 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:27.0) Gecko/20100101 Firefox/27.0");
			 /**建立实际的连接**/
			 connection.connect();
			 /**定义 BufferedReader输入流来读取URL的响应**/
			 in = connection.getInputStream();
			 byte[] b = new byte[1024*100];
			 int num = -1;
			 while((num = in.read(b, 0, b.length)) != -1 )
			 {
				 baos.write(b, 0, num);
			 }
		} 
		catch (Exception e) 
	 	{
			LogHelper.error(e.getMessage(), e);
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(baos != null) { baos.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return baos.toByteArray();
	}
	
	public static byte[] doPostBytes(String url,Map<String, String> params,Map<String, String> headers)
	{
		ByteArrayOutputStream  baos = new ByteArrayOutputStream();
		InputStream in = null;
		PrintWriter out = null;
		try 
		{
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 URLConnection connection = realUrl.openConnection();
			 /**设置通用的请求属性**/
			 connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			 connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			 connection.setRequestProperty("Connection", "Keep-Alive");
			 connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 connection.setRequestProperty("Charset", "utf-8");
			 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:27.0) Gecko/20100101 Firefox/27.0");
			 if( null!=headers && headers.size()>0 )
			 {
				 for(String key : headers.keySet() )
				 {
					 connection.setRequestProperty(key, headers.get(key));
				 }
			 }
			 /**发送POST请求必须设置如下两行**/
			 connection.setDoOutput(true);
			 connection.setDoInput(true);
			 /**获取URLConnection对象对应的输出流**/
			 out = new PrintWriter(connection.getOutputStream());
			 /**发送请求参数**/
			 String param = parseParams(params);
			 out.print(param);
			 /**flush输出流的缓冲**/
			 out.flush();
			 /**定义 BufferedReader输入流来读取URL的响应**/
			 in = connection.getInputStream();
			 byte[] b = new byte[1024*100];
			 int num = -1;
			 while((num = in.read(b, 0, b.length)) != -1 )
			 {
				 baos.write(b, 0, num);
			 }
		} 
	 	catch (Exception e) 
	 	{
	 		LogHelper.error(e.getMessage(), e);
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(out != null) { out.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(baos != null) { baos.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return baos.toByteArray();
	}
	
	public static byte[] doRestPost(String url,String params,Map<String, String> headers)
	{
		ByteArrayOutputStream  baos = new ByteArrayOutputStream();
		InputStream in = null;
		OutputStream out = null;
		try 
		{
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
			 if( null!=headers && headers.size()>0 )
			 {
				 for(String key : headers.keySet() )
				 {
					 connection.setRequestProperty(key, headers.get(key));
				 }
			 }
			 connection.setRequestMethod("POST");
			 
			 /**发送POST请求必须设置如下两行**/
			 connection.setDoOutput(true);
			 connection.setDoInput(true);
			 /**获取URLConnection对象对应的输出流**/
			 out = connection.getOutputStream();
			 /**发送请求参数**/
			 out.write(params.getBytes("UTF-8"));
			 /**flush输出流的缓冲**/
			 out.flush();
			 
			 in = connection.getInputStream();
			 byte[] b = new byte[1024*100];
			 int num = -1;
			 while((num = in.read(b, 0, b.length)) != -1 )
			 {
				 baos.write(b, 0, num);
			 }
		} 
	 	catch (Exception e) 
	 	{
	 		LogHelper.error(e.getMessage(), e);
	 		e.printStackTrace();
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(out != null) { out.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(baos != null) { baos.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return baos.toByteArray();
	}
	
	public static byte[] doRestPost(String url,String params)
	{
		ByteArrayOutputStream  baos = new ByteArrayOutputStream();
		InputStream in = null;
		OutputStream out = null;
		try 
		{
			 URL realUrl = new URL(url);
			 /**打开和URL之间的连接**/
			 HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
			 connection.setRequestMethod("POST");
			 /**发送POST请求必须设置如下两行**/
			 connection.setDoOutput(true);
			 connection.setDoInput(true);
			 /**获取URLConnection对象对应的输出流**/
			 out = connection.getOutputStream();
			 /**发送请求参数**/
			 out.write(params.getBytes("UTF-8"));
			 /**flush输出流的缓冲**/
			 out.flush();
			 
			 in = connection.getInputStream();
			 byte[] b = new byte[1024*100];
			 int num = -1;
			 while((num = in.read(b, 0, b.length)) != -1 )
			 {
				 baos.write(b, 0, num);
			 }
		} 
	 	catch (Exception e) 
	 	{
	 		LogHelper.error(e.getMessage(), e);
	 		e.printStackTrace();
		} 
	 	finally 
	 	{
	 		/**使用finally块来关闭输入流**/
			 try 
			 {
				 if(in != null) { in.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(out != null) { out.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
			 
			 try 
			 {
				 if(baos != null) { baos.close(); }
			 } 
			 catch (Exception e) 
			 {
			 }
	 	}
		return baos.toByteArray();
	}
	
	
	private static String parseParams(Map<String,String> map)
	{
		StringBuffer sb = new StringBuffer();
		if(map != null)
		{
			for (Entry<String, String> e : map.entrySet()) 
			{
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();	
	}
}
