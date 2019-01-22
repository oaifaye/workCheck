package com.faye.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 常用的日期函数
 * 
 */
public class DateUtil {


	public static java.util.Date addByField(java.util.Date date, int amount, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, amount);
		return c.getTime();
	}

	/**
	 * 
	 * @标题: addDay
	 * @描述:在日期上增加 N 天
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param day
	 * @return
	 * @return java.util.Date
	 */
	
	public static java.util.Date addDay(java.util.Date date, int day) {
		return addByField(date, day, Calendar.DATE);
	}

	private static java.util.Date ceil(java.util.Date date, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		if (field == Calendar.YEAR) {
			c.add(Calendar.YEAR, 1);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.MONTH) {
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.DATE) {
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.HOUR || field == Calendar.HOUR_OF_DAY) {
			c.add(Calendar.HOUR_OF_DAY, 1);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.MINUTE) {
			c.add(Calendar.MINUTE, 1);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.SECOND) {
			c.add(Calendar.SECOND, 1);
			return c.getTime();
		} else if (field == Calendar.MILLISECOND) {
			return c.getTime();
		} else {
			throw new IllegalArgumentException("Invalid date field: " + field);
		}
	}

	private static java.util.Date floor(java.util.Date date, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		if (field == Calendar.YEAR) {
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.MONTH) {
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.DATE) {
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.HOUR || field == Calendar.HOUR_OF_DAY) {
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.MINUTE) {
			c.set(Calendar.SECOND, 0);
			return c.getTime();
		} else if (field == Calendar.MILLISECOND) {
			return c.getTime();
		} else {
			throw new IllegalArgumentException("Invalid date field: " + field);
		}
	}

	/**
	 * 
	 * @标题: addHour
	 * @描述:在日期上增加 N 小时
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param minute
	 * @return
	 * @return java.util.Date
	 */
	
	public java.util.Date addHour(java.util.Date date, int amount) {
		return addByField(date, amount, Calendar.HOUR);
	}

	/**
	 * 
	 * @标题: addMinute
	 * @描述:在日期上增加 N 分钟
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param minute
	 * @return
	 * @return java.util.Date
	 */
	
	public java.util.Date addMinute(java.util.Date date, int amount) {
		return addByField(date, amount, Calendar.MINUTE);
	}

	/**
	 * 
	 * @标题: addMonth
	 * @描述:在日期上增加 N 月
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param month
	 * @return
	 * @return java.util.Date
	 */
	
	public java.util.Date addMonth(java.util.Date date, int amount) {
		return addByField(date, amount, Calendar.MONTH);
	}

	/**
	 * 
	 * @标题: addSecond
	 * @描述:在日期上增加 N 秒
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param second
	 * @return
	 * @return java.util.Date
	 */
	public java.util.Date addSecond(java.util.Date date, int amount) {
		return addByField(date, amount, Calendar.SECOND);
	}

	/**
	 * 
	 * @标题: addYear
	 * @描述:在日期上增加 N 年
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param year
	 * @return
	 * @return java.util.Date
	 */
	
	public java.util.Date addYear(java.util.Date date, int amount) {
		return addByField(date, amount, Calendar.YEAR);
	}

	/**
	 * 
	 * @标题: ceilDay
	 * @描述:对一个日期按天向上取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到结束日期，进行向上取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public static Date ceilDay(Date date) {
		return ceil(date, Calendar.DATE);
	}

	/**
	 * 
	 * @标题: ceilHour
	 * @描述: 对一个日期按小时向上取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到结束日期，进行向上取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date ceilHour(Date date) {
		return ceil(date, Calendar.HOUR);
	}

	/**
	 * 
	 * @标题: ceilMinute
	 * @描述:对一个日期按分钟向上取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到结束日期，进行向上取整
	 * @使用方法:
	 * @注意事项:
	 * @param data
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date ceilMinute(Date date) {
		return ceil(date, Calendar.MINUTE);
	}

	/**
	 * 
	 * @标题: ceilMonth
	 * @描述:对一个日期按月向上取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到结束日期，进行向上取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date ceilMonth(Date date) {
		return ceil(date, Calendar.MONTH);
	}

	/**
	 * 
	 * @标题: ceilYear
	 * @描述:对一个日期按年向上取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到结束日期，进行向上取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date ceilYear(Date date) {
		return ceil(date, Calendar.YEAR);
	}

	/**
	 * 
	 * @标题: dateSubToDayCount
	 * @描述: 计算两个日期差，返回值为这两个日期之间差多少天
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date1
	 * @param date2
	 * @return
	 * @return int
	 */
	
	public static float dateDiff(Date date1, Date date2, int field) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("Date must be specified");
		}
		long millisecond = date1.getTime() - date2.getTime();
		// millisecond = Math.abs(millisecond);
		if (field == Calendar.MILLISECOND) {
			return millisecond;
		}
		if (field == Calendar.SECOND) {
			return millisecond / 1000F;
		}
		if (field == Calendar.MINUTE) {
			return millisecond / 1000f / 60f;
		}
		if (field == Calendar.HOUR_OF_DAY || field == Calendar.HOUR) {
			return millisecond / 1000f / 60f / 60f;
		}
		if (field == Calendar.DATE) {
			return millisecond / 1000f / 60f / 60f / 24f;
		}
		if (field == Calendar.MONTH) {
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(date1.after(date2) ? date1 : date2);
			c2.setTime(date1.before(date2) ? date1 : date2);
			int sumMonths = 0;
			int sumDays = 0;
			while (c2.before(c1)) {
				sumDays += c2.getActualMaximum(Calendar.DAY_OF_MONTH);
				c2.add(Calendar.MONTH, 1);
				sumMonths++;
			}
			float months = (float) div(sumDays, sumMonths, 1);
			return millisecond / 1000f / 60f / 60f / 24f / months;
		}
		if (field == Calendar.YEAR) {
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(date1.after(date2) ? date1 : date2);
			c2.setTime(date1.before(date2) ? date1 : date2);
			int sumDays = 0;
			while (c2.before(c1)) {
				sumDays += c2.getActualMaximum(Calendar.DAY_OF_YEAR);
				c2.add(Calendar.YEAR, 1);
			}
			return millisecond / 1000f / 60f / 60f / 24f / sumDays;
		}
		throw new IllegalArgumentException("Invalid parameter field: " + field);
	}

	private static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 
	 * @标题: dateToStr
	 * @描述: 由日期按指定格式返回字符串
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param pattern
	 * @return
	 * @return String
	 */
	
	public static String dateToStr(java.util.Date date, String pattern) {
		return dateToStr(date, pattern, null);
	}

	/**
	 * 返回指定时区的格式化结果
	 * 
	 * @param date
	 * @param pattern
	 * @param timeZone
	 * @return
	 * @author chen jinyan
	 */
	public static String dateToStr(java.util.Date date, String pattern, TimeZone timeZone) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		if (timeZone != null) {
			df.setTimeZone(timeZone);
		}
		if (date != null)
			return df.format(date);
		else {
			return "";
		}
	}

	/**
	 * 
	 * @标题: dateToTimestamp
	 * @描述: 日期类型转时间戳
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param dataTime
	 * @return
	 * @return Timestamp
	 */
	
	public Timestamp dateToTimestamp(Date dataTime) {
		return new java.sql.Timestamp(dataTime.getTime());
	}

	/**
	 * 
	 * @标题: floorDay
	 * @描述:对一个日期按天向下取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到开始日期，进行向下取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public static Date floorDay(Date date) {
		return floor(date, Calendar.DATE);
	}

	/**
	 * 
	 * @标题: floorHour
	 * @描述:对一个日期按小时向下取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到开始日期，进行向下取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date floorHour(Date date) {
		return this.floor(date, Calendar.HOUR);
	}

	/**
	 * 
	 * @标题: floorMinute
	 * @描述:对一个日期按分钟向下取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到开始日期，进行向下取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date floorMinute(Date date) {
		return this.floor(date, Calendar.MINUTE);
	}

	/**
	 * 
	 * @标题: floorMonth
	 * @描述:对一个日期按月向下取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到开始日期，进行向下取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date floorMonth(Date date) {
		return this.floor(date, Calendar.MONTH);
	}

	/**
	 * 
	 * @标题: floorYear
	 * @描述:对一个日期按年向下取整
	 * @适用条件:在对日期进行搜索查询时，需要对得到开始日期，进行向下取整
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public Date floorYear(Date date) {
		return this.floor(date, Calendar.YEAR);
	}

	/**
	 * 
	 * @标题: getAge
	 * @描述: 通过一个出生日期获得年龄
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param born
	 * @return
	 * @return int
	 */
	
	public int getAge(Date born) {
		if (null != born) {
			Calendar now = Calendar.getInstance();
			int curYear = now.get(Calendar.YEAR);

			Calendar cborn = Calendar.getInstance();
			cborn.setTime(born);
			return curYear - cborn.get(Calendar.YEAR);
		} else {
			return 0;
		}
	}

	/**
	 * 
	 * @标题: strToDate
	 * @描述:字符串转时间
	 * @适用条件:
	 * @使用方法:
	 * @注意事项:
	 * @param date
	 * @param pattern
	 * @return
	 * @throws ParseException
	 * @return Date
	 */
	
	public static Date strToDate(String date, String pattern) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		java.util.Date outDate = formatter.parse(date);
		return outDate;
	}

	
	public String getTimeDes(long ms) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

		StringBuilder str = new StringBuilder();
		if (day > 0) {
			str.append(day).append("天");
		}
		if (hour > 0) {
			str.append(hour).append("小时");
		}
		if (minute > 0) {
			str.append(minute).append("分钟");
		}
		if (second > 0) {
			str.append(second).append("秒");
		}
		// if(milliSecond>0){
		// str.append(milliSecond).append("毫秒,");
		// }
		// if(str.length()>0){
		// str=str.deleteCharAt(str.length()-1);
		// }

		return str.toString();
	}
	
	
	public String getTimeDesSimilar(Date time) {
		Date now=new Date();
		long ms=now.getTime()-time.getTime();
		long ss = 1000;
		long mi = ss * 60;
		long hh = mi * 60;
		long dd = hh * 24;
		long MM=dd*30;
		long yy=MM*12;

		long year=ms/yy;
		long month=(ms-year*yy)/MM;
		long day = (ms-year*yy-month*MM) / dd;
		long hour = (ms - year*yy-month*MM-day * dd) / hh;
		long minute = (ms - year*yy-month*MM-day * dd - hour * hh) / mi;

		StringBuilder str = new StringBuilder();
		if (year > 0) {
			str.append(year).append("年前");
			return str.toString();
		}
		if (month > 0) {
			str.append(month).append("月前");
			return str.toString();
		}
		if (day > 0) {
			str.append(day).append("天前");
			return str.toString();
		}
		if (hour > 0) {
			str.append(hour).append("小时前");
			return str.toString();
		}
		if (minute > 0) {
			str.append(minute).append("分钟前");
			return str.toString();
		}
		return "刚刚";
	}
	
	
	public Date getGMT0Date(Date thisZoneDate) {
		Calendar c = Calendar.getInstance();
		TimeZone thisTimeZone = TimeZone.getDefault();// 得到当前系统的时区
		c.setTimeInMillis(thisZoneDate.getTime() - thisTimeZone.getOffset(thisZoneDate.getTime()));
		return c.getTime();
	}

	
	public Date getGMT0DateByTimeZone(Date thisZoneDate, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(thisZoneDate.getTime() - timeZone.getOffset(thisZoneDate.getTime()));
		return c.getTime();
	}

	
	public Date getDateByGMT0(Date gmt0Date) {
		Calendar c = Calendar.getInstance();
		TimeZone thisTimeZone = TimeZone.getDefault();// 得到当前系统的时区
		c.setTimeInMillis(gmt0Date.getTime() + thisTimeZone.getOffset(gmt0Date.getTime()));
		return c.getTime();
	}

	
	public Date getTimeZoneDateByGMT0(Date gmt0Date, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(gmt0Date.getTime() + timeZone.getOffset(gmt0Date.getTime()));
		return c.getTime();
	}
	
	//获取请假时长
	public static float getLeaveDayNum(String beginDate,String endDate) throws ParseException {
		String bh = beginDate.substring(11, 13);
		if(Integer.valueOf(bh) < 13) {
			beginDate = beginDate.substring(0,11) + "00:00";
		}else {
			beginDate = beginDate.substring(0,11) + "12:00";
		}
		
		String eh = endDate.substring(11, 13);
		if(Integer.valueOf(eh) < 13) {
			endDate = endDate.substring(0,11) + "12:00";
		}else {
			endDate = endDate.substring(0,11) + "24:00";
		}
		float difh = dateDiff(strToDate(endDate, "yyyy-MM-dd HH:mm"), strToDate(beginDate, "yyyy-MM-dd HH:mm"), Calendar.HOUR_OF_DAY);
//		System.out.println(difh/24);
		return difh/24;
	}
	
	public static String formateShangban(String beginDate) {
		String bh = beginDate.substring(11, 13);
		if(Integer.valueOf(bh) < 13) {
			beginDate = beginDate.substring(0,11) + "08:30";
		}else {
			beginDate = beginDate.substring(0,11) + "13:30";
		}
		return beginDate;
	}
	
	public static String formateXiaban(String endDate) {
		String eh = endDate.substring(11, 13);
		if(Integer.valueOf(eh) < 13) {
			endDate = endDate.substring(0,11) + "12:00";
		}else {
			endDate = endDate.substring(0,11) + "17:30";
		}
		return endDate;
	}
	
	/**
	 * 返回time1-time2 > 0?
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean compareStrTime(String time1,String time2) {
		String h1 = time1.split(":")[0];
		String h2 = time2.split(":")[0];
		if(!h1.equals(h2)) {
			return Integer.valueOf(h1) - Integer.valueOf(h2) > 0;
		}
		String m1 = time1.split(":")[1];
		String m2 = time2.split(":")[1];
		return Integer.valueOf(m1) - Integer.valueOf(m2) > 0;
	}
	
	/**
	 * 返回time1-time2 的小时
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static float diffStrTimeH(String time1,String time2) {
		String h1 = time1.split(":")[0];
		String h2 = time2.split(":")[0];
		String m1 = time1.split(":")[1];
		String m2 = time2.split(":")[1];
		
		float hdiff = Integer.valueOf(h1) - Integer.valueOf(h2);
		int mdiff = Integer.valueOf(m1) - Integer.valueOf(m2) ;
		float m = 0f;
		if(mdiff > 0 && mdiff >=30) {
			m = 0.5f;
		}else if(mdiff <0 ) {
			if(mdiff >= -30) {
				m = -0.5f;
			}else{
				m = -1f;
			}
		}
		
		return hdiff + m;
	}
	
	public static void main(String[] args) throws ParseException {
//		getLeaveDayNum("2018-12-14 11:30", "2018-12-18 17:30");
//		System.out.println(compareStrTime("14:50", "14:00"));
		System.out.println(diffStrTimeH("11:00","09:20"));
	}

}
