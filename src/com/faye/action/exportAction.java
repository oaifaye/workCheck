package com.faye.action;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts2.ServletActionContext;

import com.faye.bean.AbnormalTime;
import com.faye.bean.Worker;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class exportAction extends ActionSupport {
	private static final long serialVersionUID = -5282964253370244754L;
	private File uploadFile;
	private String uploadpicFileName;
	private String uploadpicContentType;
	private List<Worker> workerList = new ArrayList<Worker>();
	private String normTime1;
	private String normTime2;
	private String normTime3;
	private String holiday;
	private String startTime;
	private String endTime;
	private String weekendWorkday;
	private List<String> allPeopleName;
	private List<AbnormalTime> abnormalTimeList = new ArrayList<AbnormalTime>();

	private ArrayList<String> allWorkDay;
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");

	// excel向数据库导入数据
	public String aainput() {
		
		workerList = readExcel();
		// 计算时间范围
		String[] dateScope = startAndStopTime(workerList);
		// 起止时间传到页面
		startTime = dateScope[0];
		endTime = dateScope[1];
		for(Worker worker : workerList){
			// 计算所有应上班的日期
			allWorkDay = workDay(dateScope);
			// 根据上下班时间自动插入flog
			countFlog(workerList, allWorkDay, worker.getSignTime(), worker);
			
		}
		// 计算员工列表
		allPeopleName = allPeople(workerList);
		// 计算旷工,并自动向workerList中添加旷工Worker
		absenteeism(workerList, allWorkDay, allPeopleName);
		ActionContext.getContext().getSession()
				.put("allPeopleName", allPeopleName);
		ActionContext.getContext().getSession().put("workerList", workerList);
		System.out.println("=====================================登陆地址："
				+ ServletActionContext.getRequest().getRemoteAddr()
				+ "==========================================");
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String listAbnormalTime() {
		allPeopleName = (List<String>) ActionContext.getContext().getSession()
				.get("allPeopleName");
		workerList = (List<Worker>) ActionContext.getContext().getSession()
				.get("workerList");
		// AbnormalTime abnormalTime=new AbnormalTime();
		if (allPeopleName != null && workerList != null) {
			for (String workerName : allPeopleName) {
				AbnormalTime abnormalTime = new AbnormalTime();
				double allAbnormalTime = 0;
				// 平时加班的（为避免平时加班但2次以上打卡）
				Map<String , Double> pingMap = new HashMap<String, Double>();
				// 节假日整天加班的（为避免节假日整天加班但2次以上打卡）
				ArrayList<String> zhengList = new ArrayList<String>();
				// 节假日加班，只打一次卡的，标记一下
				boolean flog = false;

				for (Worker worker : workerList) {
					try {

						// workerList中name=workerName，且加班时间大于0
						if (worker.getName().equals(workerName)) {
							if (!worker.getFlog().equals("3")) {// 为去掉旷工，因为旷工时间格式不一样，会引起时间格式转换异常
								String signTime = dateFormat3
										.format(dateFormat1.parse(worker
												.getSignTime()));
								if (worker.getWeek().indexOf("(整)") == -1) {
									double workAbnormalTime = worker.getAbnormalTime();
									// 非节假日整天加班的
									if( workAbnormalTime != 0){
										if (!pingMap.keySet().contains(signTime)) {
											allAbnormalTime += worker.getAbnormalTime();
											pingMap.put(signTime, workAbnormalTime);
										}else{
											//第二次发现的时间比Map中的大，加上差值
											if(pingMap.get(signTime)<workAbnormalTime){
												allAbnormalTime+=workAbnormalTime-pingMap.get(signTime);
												pingMap.put(signTime, workAbnormalTime);
											}
										}
									}
								} else {
									// 节假日整天加班的
									if (!zhengList.contains(signTime)) {
										allAbnormalTime += worker
												.getAbnormalTime();
										zhengList.add(signTime);
										if (worker.getAbnormalTime() == 0) {
											// 加班时间为0的
											flog = true;
										}
									}
								}
							}
						}
					} catch (ParseException e) {

						e.printStackTrace();
					}
				}
				if (flog == true) {
					workerName = workerName + "(节假日加班只打卡一次)";
				}
				abnormalTime.setName(workerName);
				abnormalTime.setAbnormalTime(allAbnormalTime);
				abnormalTimeList.add(abnormalTime);
				System.out.println(workerName + " : " + allAbnormalTime);
			}
			return SUCCESS;
		} else {
			return ERROR;
		}
	}

	// ===================================================================================================

	// 读取excel数据
	private List<Worker> readExcel() {
		String number = null;
		String name = null;
		String signTime = null;
		FileInputStream fis = null;
		POIFSFileSystem fs = null;// 处理流
		HSSFWorkbook wb = null;//
		HSSFSheet hssfsheet = null;// 工作表
		HSSFRow hssfrow = null;// 单元行
		List<Worker> workerList1 = new ArrayList<Worker>();

		try {
			fis = new FileInputStream(uploadFile);
			fs = new POIFSFileSystem(fis);
			wb = new HSSFWorkbook(fs);
		
		hssfsheet = wb.getSheetAt(0);// 第一个工作表
		hssfrow = hssfsheet.getRow(0);// 第一行
		int sheetcou = wb.getNumberOfSheets();
		for (int i = 0; i < sheetcou; i++) {// 从第1个sheet开始
			hssfsheet = wb.getSheetAt(i);// 第i个sheet的数据行
			int cou = hssfsheet.getPhysicalNumberOfRows();// 获得当前表有多少行数据
			for (int j = 1; j < cou; j++) {// 从第二行开始
				hssfrow = hssfsheet.getRow(j);
				// 判断是否还存在需要导入的数据
				if (hssfrow == null) {
					break;
				}
				/** 将EXCEL中的第 j 行，第一列的值插入到实例中 */
				/**
				 * CELL_TYPE_NUMERIC 数值型 0 CELL_TYPE_STRING 字符串型 1
				 * CELL_TYPE_FORMULA 公式型 2 CELL_TYPE_BLANK 空值 3
				 * CELL_TYPE_BOOLEAN 布尔型 4 CELL_TYPE_ERROR 错误 5
				 */
				Worker worker = new Worker();
				number = hssfrow.getCell(0).getStringCellValue().trim();
				name = hssfrow.getCell(1).getStringCellValue().trim();
				signTime = hssfrow.getCell(2).getStringCellValue();

				// 登记号码
				worker.setNumber(number);
				// 姓名
				if (("孙雪").equals(name.replace(" ", ""))) {
					worker.setName("孙宝宝");
				} else if (("苏丽沣").equals(name.replace(" ", ""))) {
					worker.setName("苏丽沣(最伟大的程序提供者！)");
				} else {
					worker.setName(name);
				}
				
				worker.setSignTime(dateFormat1.format(dateFormat1.parse(signTime)));
				worker.setWeek(findWeek(signTime));
				workerList1.add(worker);
			}
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workerList1;
	}

	// 根据时间计算是否为加班，迟到等，并自动添加flog与abnormalTime
	private void countFlog(List<Worker> workerList1,
			ArrayList<String> allWorkDay, String signTime, Worker worker) {
		try {
			boolean flog = false;
			for (String workerDay : allWorkDay) {
				if (dateFormat3.format(dateFormat1.parse(signTime)).equals(
						workerDay)) {
					flog = true;
					break;
				}
			}
			if (flog == true) {
				// 打卡时间在正常上班时间内
				// 打卡时间
				long a = dateFormat2.parse(
						dateFormat2.format(dateFormat1.parse(signTime)))
						.getTime();
				// 上班时间
				long b = dateFormat2.parse(normTime1).getTime();
				// 下班时间
				long c = dateFormat2.parse(normTime2).getTime();
				// 加班时间
				long d = dateFormat2.parse(normTime3).getTime();

				if (a > b && a < c) {
					// 迟到或早退
					worker.setFlog("1");
				} else if (a > d) {
					// 加班
					worker.setFlog("2");
					worker.setAbnormalTime(tfRound((float) (a - c)
							/ (1000 * 60 * 60)));
				} else {
					// 正常
					worker.setFlog("0");
				}
			} else {
				// 打卡时间不在正常上班时间内,按加班算
				worker.setFlog("2");
				// 整天加班星期后面加上（整）
				worker.setWeek(findWeek(worker.getSignTime()) + "(整)");
				double abnormalTime = workDuration(workerList1,
						worker.getName(), dateFormat3.format(dateFormat1
								.parse(worker.getSignTime())));
				worker.setAbnormalTime(abnormalTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// 根据日期计算星期
	private String findWeek(String signTime) {
		// 星期
		String[] weekDaysName = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat3.parse(signTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}

	// 计算文档起止时间
	private String[] startAndStopTime(List<Worker> workerList) {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		long startTimeL = 0;
		long endTimeL = 0;
		String startTime = null;
		String endTime = null;
		long a;

		for (Worker w : workerList) {
			try {
				if (startTimeL == 0 && endTimeL == 0) {
					// 第一条时
					startTimeL = dateFormat1.parse(w.getSignTime()).getTime();
					endTimeL = dateFormat1.parse(w.getSignTime()).getTime();
					startTime = w.getSignTime();
					endTime = w.getSignTime();
				} else {
					// 不是第一条时
					a = dateFormat1.parse(w.getSignTime()).getTime();
					// 确定起始日期
					if (a < startTimeL) {
						startTimeL = a;
						startTime = w.getSignTime();
					}
					// 确定结束日期
					if (a > endTimeL) {
						endTimeL = a;
						endTime = w.getSignTime();
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String[] timeArray = { startTime, endTime };
		return timeArray;
	}

	// 一段时间内全部工作日的日期
	@SuppressWarnings("deprecation")
	private ArrayList<String> workDay(String[] dateScope) {
		ArrayList<String> allWorkDay = new ArrayList<String>();
		try {
			// 计算非六日
			Date startDate = dateFormat3.parse(dateScope[0]);
			Date endDate = dateFormat3.parse(dateScope[1]);
			while (startDate.before(endDate) || startDate.equals(endDate)) {
				if (!(startDate.getDay() == 6 || startDate.getDay() == 0)) {
					allWorkDay.add(dateFormat3.format(startDate));
				}
				startDate.setTime(startDate.getTime() + 86400000); // 86400000一天的毫秒数
			}

			// 工作日中除去节假日
			if (holiday != null) {
				// 处理接收到的字符串
				String[] holidayArray = holiday.split(",");

				for (String h : holidayArray) {
					boolean flog = false;
					for (String a : allWorkDay) {
						if (h.equals(a)) {
							flog = true;
							break;
						}
					}
					if (flog == true) {
						allWorkDay.remove(h);
					}
				}
			}

			// 工作日中加上需正常上班的六日
			// 截断六日上班的日期
			if (!(weekendWorkday == null || "".equals(weekendWorkday))) {
				String[] weekendWorkdayArray = weekendWorkday.split(",");
				for (String w : weekendWorkdayArray) {
					boolean flog = false;
					for (String s : allWorkDay) {
						if (w.equals(s)) {
							flog = true;
							break;
						}
					}
					if (flog == false) {
						allWorkDay.add(w);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return allWorkDay;
	}

	// 计算全部人员列表
	private ArrayList<String> allPeople(List<Worker> workerList) {
		ArrayList<String> peopleList = new ArrayList<String>();

		for (Worker w : workerList) {
			if (peopleList.size() == 0) {
				// 第一个人
				peopleList.add(w.getName());
			} else {
				boolean flog = false;
				for (String s : peopleList) {
					if (w.getName().equals(s)) {
						flog = true;
						break;
					}
				}
				if (flog == false) {
					peopleList.add(w.getName());
				}
			}
		}
		return peopleList;
	}

	// 截取double到指定为数
	private double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	// 将一个double变为小数点后是0.5或0.0或+1
	private double tfRound(double value) {
		double result = 0;
		double value1 = round(value, 0, BigDecimal.ROUND_DOWN);
		double vPoint = value - value1;
		// 5/60：差5分钟到半点或整点，也算进去
		if ((vPoint + 0.084) < 0.5 && (vPoint + 0.084) > 0) {
			result = value1;
		} else if ((vPoint + 0.084) >= 0.5 && (vPoint + 0.084) < 1) {
			result = value1 + 0.5;
		} else if ((vPoint + 0.084) >= 1) {
			result = value1 + 1;
		}
		return result;
	}

	// 根据人名和日期，计算这个日期这个人的上班时长,需要输入workerList
	private double workDuration(List<Worker> workerList1, String workerName,
			String date) {
		long workStartTime = 0;
		long workEndTime = 0;
		for (Worker worker : workerList1) {
			String workerDate;
			try {
				workerDate = dateFormat3.format(dateFormat3.parse(worker
						.getSignTime()));
				long workerTime = dateFormat2.parse(
						dateFormat2.format(dateFormat1.parse(worker
								.getSignTime()))).getTime();
				if (worker.getName().equals(workerName)
						&& workerDate.equals(date)) {
					if (workStartTime == 0) {
						workStartTime = workerTime;
					} else if (workEndTime == 0) {
						workEndTime = workerTime;
					} else if (workStartTime > workerTime) {
						workStartTime = workerTime;
					} else if (workEndTime < workerTime) {
						workEndTime = workerTime;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		double workingTime = tfRound((float) (workEndTime - workStartTime)
				/ (1000 * 60 * 60));
		return workingTime;
	}

	// 计算旷工,并自动向workerList中添加旷工Worker
	private void absenteeism(List<Worker> workerList1,
			ArrayList<String> allWorkDay, List<String> allPeopleName) {
		for (String peopleName : allPeopleName) {
			for (String workDay : allWorkDay) {
				int flog1 = 0;
				// 计算这个人在workDay这一天的工作时长
				double workerTimeD = workDuration(workerList1, peopleName,
						workDay);
				for (Worker worker : workerList1) {

					// 用name选workerList中的人
					if (worker.getName().equals(peopleName)) {
						try {
							Date dd = dateFormat3.parse(worker.getSignTime());
							String workerTime = dateFormat3.format(dd);

							// 整天旷工
							if (workDay.equals(workerTime)) {
								flog1 = 1;
								// 只打一次卡
							} else if (workerTimeD < 0.0) {
								flog1 = 2;
							} else if (workerTimeD > 0 && workerTimeD < 8) {
								// 没上满8小时
								flog1 = 3;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				if (flog1 != 1) {
					Worker worker1 = new Worker();
					worker1.setName(peopleName);
					//为时间加上00:00,以免后面的方法时间转换出异常
					//String workDayFinel=dateFormat1.format(dateFormat1.parse(workDay));
					String workDayFinel=workDay+" 00:00";
					
					if (flog1 == 0) {
						worker1.setSignTime(workDayFinel);
					} else if (flog1 == 2) {
						worker1.setSignTime(workDayFinel + "(下)");
					} else if (flog1 == 3) {
						worker1.setSignTime(workDayFinel + "(缺)");
					}
					worker1.setFlog("3");
					worker1.setWeek(findWeek(workDay));
					workerList.add(worker1);
					System.out.println(worker1.getName());
				}
			}
		}
	}

	// =============================================================================================
	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadpicFileName() {
		return uploadpicFileName;
	}

	public void setUploadpicFileName(String uploadpicFileName) {
		this.uploadpicFileName = uploadpicFileName;
	}

	public String getUploadpicContentType() {
		return uploadpicContentType;
	}

	public void setUploadpicContentType(String uploadpicContentType) {
		this.uploadpicContentType = uploadpicContentType;
	}

	public List<Worker> getWorkerList() {
		return workerList;
	}

	public void setWorkerList(List<Worker> workerList) {
		this.workerList = workerList;
	}

	public String getNormTime1() {
		return normTime1;
	}

	public void setNormTime1(String normTime1) {
		this.normTime1 = normTime1;
	}

	public String getNormTime2() {
		return normTime2;
	}

	public void setNormTime2(String normTime2) {
		this.normTime2 = normTime2;
	}

	public String getNormTime3() {
		return normTime3;
	}

	public void setNormTime3(String normTime3) {
		this.normTime3 = normTime3;
	}

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getWeekendWorkday() {
		return weekendWorkday;
	}

	public void setWeekendWorkday(String weekendWorkday) {
		this.weekendWorkday = weekendWorkday;
	}

	public List<String> getAllPeopleName() {
		return allPeopleName;
	}

	public void setAllPeopleName(List<String> allPeopleName) {
		this.allPeopleName = allPeopleName;
	}

	public List<AbnormalTime> getAbnormalTimeList() {
		return abnormalTimeList;
	}

	public void setAbnormalTimeList(List<AbnormalTime> abnormalTimeList) {
		this.abnormalTimeList = abnormalTimeList;
	}

}
