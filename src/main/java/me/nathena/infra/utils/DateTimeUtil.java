package me.nathena.infra.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 说明：时间工具类
 */
public final class DateTimeUtil 
{
	public static final String ymd = "yyyy-MM-dd";
	public static final String ymdhm = "yyyy-MM-dd HH:mm";

	private static final long ONE_MINUTE = 60000L;  
    private static final long ONE_HOUR = 3600000L;  
    private static final long ONE_DAY = 86400000L;  
    private static final long ONE_WEEK = 604800000L;  
  
    private static final String ONE_SECOND_AGO = "秒前";  
    private static final String ONE_MINUTE_AGO = "分钟前";  
    private static final String ONE_HOUR_AGO = "小时前";  
    private static final String ONE_DAY_AGO = "天前";  
    private static final String ONE_MONTH_AGO = "月前";  
    private static final String ONE_YEAR_AGO = "年前";
	
	private static TimeZone timeZone  = TimeZone.getTimeZone("Asia/Shanghai");
	private static Map<Integer, String> weekDays = new HashMap<Integer, String>();
	private static Map<Integer, String> weekDaysEn = new HashMap<Integer, String>();
	
	static{
		
		weekDays.put(1, "周日");
		weekDays.put(2, "周一");
		weekDays.put(3, "周二");
		weekDays.put(4, "周三");
		weekDays.put(5, "周四");
		weekDays.put(6, "周五");
		weekDays.put(7, "周六");
		
		weekDaysEn.put(1, "周日");
		weekDaysEn.put(2, "周一");
		weekDaysEn.put(3, "周二");
		weekDaysEn.put(4, "周三");
		weekDaysEn.put(5, "周四");
		weekDaysEn.put(6, "周五");
		weekDaysEn.put(7, "周六");
	}
	
	private static SimpleDateFormat getSimpleDateFormat(String format)
	{
		SimpleDateFormat sf = new SimpleDateFormat(format);
		sf.setTimeZone(timeZone);
		
		return sf;
	}
	
	private static Calendar getCalendar()
	{
		return GregorianCalendar.getInstance(timeZone);
	}
	
	/**
	 * @说明 得到格式为 format 的日期Str
	 * @param date 日期
	 * @param format 转化格式
	 * @return String 
	 */
	public static String getDateToStr(Date date,String format)
	{
		try
		{
			if( null == date )
			{
				return "";
			}
			
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			
			return simpleDateFormat.format(date);
			
		}catch(Exception ex){
			//LogHelper.error(ex.getMessage(),ex);
		}
		return "";
	}
	/**
	 * @说明 得到格式为 format 的日期Str
	 * @param date 日期
	 * @param format 转化格式
	 * @return String 
	 */
	public static String getDateToStr(String dateStr,String format1,String format2)
	{
		String strDate = "";
		try
		{
			SimpleDateFormat simpleDateFormat1 = getSimpleDateFormat(format1);
			Date date = simpleDateFormat1.parse(dateStr);
			
			SimpleDateFormat simpleDateFormat2 = getSimpleDateFormat(format2);
			
			strDate = simpleDateFormat2.format(date);
		}
		catch(Exception ex)
		{
		}
		return strDate;
	}
	/**
	 * @说明 得到格式为 format 的日期
	 * @param str
	 * @param format 转化格式
	 * @return Date
	 */
	public static Date getStrToDate(String str,String format)
	{
		Date date = null;
		
		try
		{
			if( null != str)
			{
				SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
				
				date = simpleDateFormat.parse(str);
			}
		}
		catch(Exception pe)
		{
			//LogHelper.error(pe.getMessage(),pe);
		}
		return date;
		
	}
	
	public static long getStringToLong(String str,String format)
	{
		long time = 0;
		try
		{
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			
			Date date = simpleDateFormat.parse(str);
			
			time = date.getTime();
		}
		catch(Exception pe)
		{
			//LogHelper.error(pe.getMessage(),pe);
		}
		return time;
	}
	
	public static int getStringToInt(String str,String format)
	{
		int time = 0;
		try
		{
			time = (int)Math.ceil(getStringToLong(str,format)/1000);
		}
		catch(Exception pe)
		{
			//LogHelper.error(pe.getMessage(),pe);
		}
		return time;
	}
	
	public static int getDateToInt(Date date)
	{
		return (int)Math.ceil(getDateToLong(date)/1000);
	}
	
	public static long getDateToLong(Date date)
	{
		return date.getTime();
	}
	
	public static Date getTimeStampToDate(int timestamp)
	{
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis((long)timestamp*1000);
		return calendar.getTime();
	}
	public static Date getTimeStampToDate(String timestamp)
	{
		try
		{
			Long l = NumberUtil.parseLong(timestamp);
			Calendar calendar = getCalendar();
			calendar.setTimeInMillis(l);
			return calendar.getTime();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public static String getTimeStampToString(int timestamp,String format)
	{
		return getDateToStr(getTimeStampToDate(timestamp),format);
	}
	public static String getTimeStampToString(String timestamp,String format)
	{
		return getDateToStr(getTimeStampToDate(timestamp),format);
	}
	
	/**
	 * @说明 获取系统时间年份
	 * @return int
	 */
	public static int getYear() 
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * @说明 根据指定的时间返回年份
	 * @param date
	 * @return int
	 */
	public static int getYear(Date date) 
	{
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * @说明 返回系统时间月份
	 * @return int
	 */
	public static int getMonth() 
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * @说明 根据指定的时间返回月份
	 * @return int
	 */
	public static int getMonth(Date date) 
	{
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * @说明 返回当前系统时间的日期(天)
	 * @return int
	 */
	public static int getDay() 
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.DATE);
	}
	
	public static int getHour()
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.HOUR);
	}
	
	public static int getMinute()
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.MINUTE);
	}
	
	/**
	 * 
	 * @Title: getSecond
	 * @author 凤梨/nathena
	 * @Description: 获取当前系统秒
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	public static int getSecond()
	{
		Calendar calendar = getCalendar();
		return calendar.get(Calendar.SECOND);
	}
	
	/**
	 * 
	 * @Title: getCurrentClock
	 * @author 凤梨/nathena
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return    设定文件
	 * @return Map<Intget,Intger>    返回类型 yyyyMMddmm
	 * @throws
	 */
	public static String getCurrentClock()
	{
		Calendar calendar = getCalendar();
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int second = calendar.get(Calendar.SECOND);
		int minute = calendar.get(Calendar.MINUTE);
		
		StringBuilder clock = new StringBuilder();
		clock.append(year).append(month).append(day);
		clock.append( hour*3600 + second*60 + minute+ 10000);
		
		return clock.toString();
	}

	/**
	 * @说明 根据指定的时间返回日期(天)
	 * @return
	 */
	public static int getDay(Date date) 
	{
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}
	/**
	 * @说明：获取月份有多少天
	 * @创建：作者:yxy	创建时间：2011-5-14
	 * @return
	 */
	public static int getDays(int year,int month)
	{
		Calendar calendar = getCalendar();
		calendar.set(Calendar.YEAR,year); 
		calendar.set(Calendar.MONTH, (month-1));//Java月份才0开始算 
		return calendar.getActualMaximum(Calendar.DATE);
	}
	/**
	 * @说明：获取月份有多少天
	 * @创建：作者:yxy	创建时间：2011-5-14
	 * @return
	 */
	public static int getDays(String tgMonth)
	{
		Calendar calendar = getCalendar();
		
		int year=Integer.valueOf(tgMonth.substring(0, 4));
		int month=Integer.valueOf(tgMonth.substring(5,7));
		calendar.set(Calendar.YEAR,year); 
		calendar.set(Calendar.MONTH, (month-1));//Java月份才0开始算 
		return calendar.getActualMaximum(Calendar.DATE);
	}
	/**
	 * @说明 返回自定义时间
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getDateTime(int year, int month, int day) 
	{
		Calendar calendar = getCalendar();
		
		calendar.set(year, month, day);
		return calendar.getTime();
	}
	
	/**
	 * 返回自定义时间
	 *
	 * @author nathena 
	 * @date 2013-7-31 下午1:26:53 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param min
	 * @param sec
	 * @return Date
	 */
	public static Date getDateTime(int year, int month, int day,int hour,int min,int sec) 
	{
		Calendar calendar = getCalendar();
		
		calendar.set(year, month, day,hour,min,sec);
		return calendar.getTime();
	}
	
	/**
	 * @说明 日期加减
	 * @param type 5--天  2--月 1--年
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date dateTimeAdd(Date date,int type,int amount) 
	{
		Calendar calendar = getCalendar();
		
		calendar.setTime(date);
		calendar.add(type, amount);
		
		return calendar.getTime();
	}
	/**
	 * @说明 日期加减
	 * @param type 5--天  2--月 1--年
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date dateTimeAdd(String str,int type,int amount,String format)
	{
		try 
		{
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			Calendar calendar = getCalendar();
			
			calendar.setTime(simpleDateFormat.parse(str));
			calendar.add(type, amount);
			return calendar.getTime();
		} 
		catch (ParseException e) 
		{
			return null;
		}
	}
	
	/**
	 * @说明 日期加减
	 * @param type 5--天  2--月 1--年
	 * @param amount
	 * @param day
	 * @return
	 */
	public static String dateTimeAddToStr(String str,int type,int amount,String format)
	{
		try 
		{
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			Calendar calendar = getCalendar();
			
			calendar.setTime(simpleDateFormat.parse(str));
			calendar.add(type, amount);
			return simpleDateFormat.format(calendar.getTime());
		} 
		catch (ParseException e) 
		{
			return "";
		}
	}
	
	/**
	 * @说明 日期加减
	 * @param type 5--天  2--月 1--年
	 * @param amount
	 * @param day
	 * @return
	 */
	public static int dateTimeAddTimeLine(String str,int type,int amount,String format)
	{
		try 
		{
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			Calendar calendar = getCalendar();
			calendar.setTime(simpleDateFormat.parse(str));
			calendar.add(type, amount);
			return (int)Math.ceil(calendar.getTime().getTime()/1000);
		} 
		catch (ParseException e) 
		{
			return 0;
		}
	}
	/**
	 * 
	 *摘要：
	 *@说明：根据日期获取当前时间是周几
	 *@创建：作者:yxy 	创建时间：2011-8-18
	 *@param dte
	 *@return 
	 *@修改历史：
	 *		[序号](yxy	2011-8-18)<修改说明>
	 */
	public static Integer getWeekByDate(Date dte)
	{
		Calendar calendar = getCalendar();
		calendar.setTime(dte);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	/**
	 * 
	 *摘要：
	 *@说明：根据日期获取当前时间是周几
	 *@创建：作者:yxy 	创建时间：2011-8-18
	 *@param str
	 *@param format
	 *@return
	 *@修改历史：
	 *		[序号](yxy	2011-8-18)<修改说明>
	 */
	public static Integer getWeekByStr(String str,String format)
	{
		try 
		{
			SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
			Calendar calendar = getCalendar();
			calendar.setTime(simpleDateFormat.parse(str));
			return calendar.get(Calendar.DAY_OF_WEEK);
		} 
		catch (ParseException e) 
		{
			return null;
		}
	}
	
	public static String getWeekDay(String str,String format,String lang)
	{
		int day = getWeekByStr(str,format);
		return "en_us".equalsIgnoreCase(lang)?weekDaysEn.get(day):weekDays.get(day);
	}
	
	public static String getWeekDay(int dateline,String lang)
	{
		int day = getWeekByDate(getTimeStampToDate(dateline));
		return "en_us".equalsIgnoreCase(lang)?weekDaysEn.get(day):weekDays.get(day);
	}
	/**
	 * 
	 *摘要：
	 *@说明：根据两个日期字符串获取之间相差多少天
	 *@创建：作者:yxy 	创建时间：2011-8-18
	 *@param str
	 *@param format
	 *@return
	 *@修改历史：
	 *		[序号](yxy	2011-8-18)<修改说明>
	 */
	public static Integer getDaysDiff(String str1,String str2,String format)
	{
		SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
		try {
			long d1 = simpleDateFormat.parse(str1).getTime();
			long d2 = simpleDateFormat.parse(str2).getTime();
			long t = (d2-d1)/1000;
			Long l = t/(3600*24);
			return l.intValue();
		} catch (ParseException e) {
			return -1;
		}
	}
	
	/**
	 * 获取时间錯到秒
	 * @return
	 */
	public static int getTimeStamp()
	{
		Calendar calendar = getCalendar();
		return (int)Math.floor(calendar.getTimeInMillis()/1000);
	}
	
	/**
	 * 获取系统当前秒值
	 *
	 * @author nathena 
	 * @date 2013-5-30 上午10:36:33 
	 * @return Timestamp
	 */
	public static Timestamp currentTimeStamp()
	{
		Calendar calendar = getCalendar();
		return new Timestamp(calendar.getTime().getTime());
	}
	
	public static Date getDate()
	{
		Calendar calendar = getCalendar();
		return calendar.getTime();
	}
	
	/**
	 * 获取当前日期，并按格式化输出
	 *
	 * @author nathena 
	 * @date 2013-7-23 下午2:27:15 
	 * @param format
	 * @return String
	 */
	public static String getCurrentDateString(String format)
	{
		Calendar calendar = getCalendar();
		return getDateToStr(calendar.getTime(), format);
	}
	
	public static String format(Date date) 
	{  
		if( null == date )
			return "";
		
        long delta = getDate().getTime() - date.getTime();  
        if (delta < 1L * ONE_MINUTE) {  
            long seconds = toSeconds(delta);  
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;  
        }  
        if (delta < 45L * ONE_MINUTE) {  
            long minutes = toMinutes(delta);  
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;  
        }  
        if (delta < 24L * ONE_HOUR) {  
            long hours = toHours(delta);  
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;  
        }  
        if (delta < 48L * ONE_HOUR) {  
            return "昨天";  
        }  
        if (delta < 30L * ONE_DAY) {  
            long days = toDays(delta);  
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;  
        }  
        if (delta < 12L * 4L * ONE_WEEK) {  
            long months = toMonths(delta);  
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;  
        } else {  
            long years = toYears(delta);  
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;  
        }  
    }  
  
    private static long toSeconds(long date) {  
        return date / 1000L;  
    }  
  
    private static long toMinutes(long date) {  
        return toSeconds(date) / 60L;  
    }  
  
    private static long toHours(long date) {  
        return toMinutes(date) / 60L;  
    }  
  
    private static long toDays(long date) {  
        return toHours(date) / 24L;  
    }  
  
    private static long toMonths(long date) {  
        return toDays(date) / 30L;  
    }  
  
    private static long toYears(long date) {  
        return toMonths(date) / 365L;  
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println(new Date(getTimeStamp()*1000));
//		System.out.println(getTimeStamp());
//		System.out.println(new Date().getTime());
//		System.out.println((long)getTimeStamp()*1000);
		
		System.out.println( getTimeStampToString(1387786977  ,"yyyy-MM-dd HH:mm:ss") );
		System.out.println( getTimeStampToString(1387787563,"yyyy-MM-dd HH:mm:ss") );
		
		System.out.println( getCalendar().getTime() );
		System.out.println( new Date() );
//		String str = getTimeStampToString(1358006400,"yyyy-MM-dd");	
//		System.out.println(DateTimeUtil.getWeekByDate(getTimeStampToDate(1366732800)));
//		Date dateTemp = DateTimeUtil.getTimeStampToDate(1366732800);
//		Date dateVar = DateTimeUtil.dateTimeAdd(dateTemp, 5, 0);
//		System.out.println(DateTimeUtil.getWeekByDate(dateVar));
//		
//		Date dateVar1 = DateTimeUtil.dateTimeAdd(dateTemp, 5, 1);
//		System.out.println(DateTimeUtil.getWeekByDate(dateVar1));
//		System.out.println(DateTimeUtil.getWeekDay(str, "yyyy-MM-dd", ""));
//		System.out.println(getStringToInt("2012-09-09","yyyy-MM-dd"));
		
		System.out.println(TimeZone.getDefault().getID());
	}
}
