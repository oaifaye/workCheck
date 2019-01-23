package com.faye.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.ServletActionContext;

import com.faye.bean.AbnormalTime;
import com.faye.bean.Leave;
import com.faye.bean.Worker;
import com.faye.util.DateUtil;
import com.faye.util.FayeUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ExportTkAction extends ActionSupport {
	private static final long serialVersionUID = -5282964253370244754L;
	private File[] uploadFile;
	private String[] uploadpicFileName;
	private String[] uploadpicContentType;
	private List<Worker> workerList = new ArrayList<Worker>();
	private String normTime1;
	private String normTime2;
	private String normTime3;//下班时间
//	private String startTime;
//	private String endTime;
	private String holiday;//上班时间 应该放假
	private String weekendWorkday;//放假的日子 但是应该上班
	private List<String> allPeopleName;
	private List<AbnormalTime> abnormalTimeList = new ArrayList<AbnormalTime>();
	private Date minDate;
	private Date maxDate;
	private String needDept;//需要统计的部门
	private String needSite;//可用的打卡地点
	
	private SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
	private static List<String> NEED_DEPT = new ArrayList<String>();
	static {
		NEED_DEPT.add("软件一部");
		NEED_DEPT.add("软件四部");
		NEED_DEPT.add("软件项目中心");
		NEED_DEPT.add("软件管理");
	}
	private static List<String> NEED_SITE = new ArrayList<String>();
	static {
		NEED_SITE.add("天房科技大厦");
		NEED_SITE.add("北京办事处");
		NEED_SITE.add("西安办事处");
		NEED_SITE.add("河南办事处");
	}
	
	public String init() {
		return SUCCESS;
	}
	
	// excel向数据库导入数据
	public String aainputTk() throws Exception {
		saveNeedDept();
		saveNeedSite();
		
		long l = System.currentTimeMillis();
		System.out.println("开始读取Excel..");
		if(uploadFile == null) {
			throw new Exception("请上传文件");
		}
		if(uploadFile.length != 3) {
			throw new Exception("三个文件一个不能少");
		}
		List<Worker> workerList1 = readExcel1(uploadFile[0]);
		List<Leave> leaveList1 = readExcel2(uploadFile[1]);
		List<Leave> leaveList2 = readExcel3(uploadFile[2]);
		List<Leave> leaveList = new ArrayList<Leave>();
		leaveList.addAll(leaveList1);
		leaveList.addAll(leaveList2);
		System.out.println("读取Excel结束.."+((System.currentTimeMillis()-l)/1000));
		l = System.currentTimeMillis();
		System.out.println("开始计算..");
		//工作日map
		Map<String, Boolean> workDayMap = makeWorkDays();
		workerList = makeWorkerList(workerList1,leaveList,workDayMap);
		System.out.println("计算结束.."+((System.currentTimeMillis()-l)/1000));
		
		ActionContext.getContext().getSession().put("allPeopleNameTk", allPeopleName);
		ActionContext.getContext().getSession().put("workerListTk", workerList);
		System.out.println("=====================================登陆地址："
				+ ServletActionContext.getRequest().getRemoteAddr()
				+ "==========================================");
		return SUCCESS;
	}

	/**
	 * 导出Excel，包括姓名、加班日期、时间、攻击加班时间、加班合计
	 * */
	@SuppressWarnings("unchecked")
	public String exportAbnormalTimeTk() {
		allPeopleName = (List<String>) ActionContext.getContext().getSession()
				.get("allPeopleNameTk");
		workerList = (List<Worker>) ActionContext.getContext().getSession()
				.get("workerListTk");
		
		/*计算所有加班中加班起止时间*/
		if (allPeopleName != null && workerList != null) {
			HashMap<String, Float> userAbnormalTimeMap = new HashMap<String,Float>();
			for (Worker worker : workerList) {
				if("2".equals(worker.getFlog()) && worker.getAbnormalTime() > 0) {
					AbnormalTime abnormalTime = new AbnormalTime();
					abnormalTime.setName(worker.getName());
					abnormalTime.setSignDate(worker.getSignDate());
					abnormalTime.setSignTime(worker.getSignTime1()+"-"+worker.getSignTime2());
					abnormalTime.setDayAbnormalTime(worker.getAbnormalTime());
					abnormalTimeList.add(abnormalTime );
					
					Float time = userAbnormalTimeMap.get(worker.getName());
					if(time == null) {
						time = 0f;
					}
					userAbnormalTimeMap.put(worker.getName(),(float)(time+worker.getAbnormalTime()));
				}
			}
			for (AbnormalTime abnormalTime : abnormalTimeList) {
				abnormalTime.setAbnormalTime(userAbnormalTimeMap.get(abnormalTime.getName()));
			}
			
			/*导出excel*/
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("APPLICATION/OCTET-STREAM");
			String fileName="abnormalTime"+(new Timestamp(System.currentTimeMillis())).toString()+".xls";
			response.addHeader("Content-Disposition", "attachment;filename="+fileName);
			//创建一个新的excel
			HSSFWorkbook wb = new HSSFWorkbook();
			//创建sheet页
			HSSFSheet sheet = wb.createSheet();
			
			/*合并单元格*/
			//标题
			sheet.addMergedRegion(new CellRangeAddress(
	                0, //first row (0-based)
	                0, //last row  (0-based)
	                0, //first column (0-based)
	                4  //last column  (0-based)
	        ));
			//实际导出的List
			List<AbnormalTime> realList = new ArrayList<AbnormalTime>();
			//合并单元格时使用
			Map<String , Integer> hebingMap = new HashMap<String , Integer>();
			for(String name:allPeopleName){
				int num = 0;//每个人的条数
				for(AbnormalTime a:abnormalTimeList){
					if(a.getName().equals(name)){
						num+=1;
						realList.add(a);
					}
				}
				hebingMap.put(name , num);
			}
			int rewNum = 2;//第3行开始
			for(String name:allPeopleName){
				for(String key :hebingMap.keySet()){
					if(name.equals(key)&&hebingMap.get(key)!=0){
						//姓名列
						sheet.addMergedRegion(new CellRangeAddress(
								rewNum, //first row (0-based)
								rewNum+hebingMap.get(key)-1, //last row  (0-based)
								0, //first column (0-based)
								0  //last column  (0-based)
							));
						//加班合计列
						sheet.addMergedRegion(new CellRangeAddress(
								rewNum, //first row (0-based)
								rewNum+hebingMap.get(key)-1, //last row  (0-based)
								4, //first column (0-based)
								4  //last column  (0-based)
							));
					rewNum+=hebingMap.get(key);
					}
				}
			}
			HSSFRow row = sheet.createRow(1);
			HSSFRow row1 = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			HSSFCell cell1 = row.createCell(0);
			cell1 = row1.createCell(0);
			cell1.setCellValue("部门加班统计");
			//设置居中
			HSSFCellStyle setBorder = wb.createCellStyle();
			setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);//水平居中  
			setBorder.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);//垂直居中 
			for(int s = 0; s <= 4; s++){
				cell = row.createCell(s);
				if (s == 0) {
					cell.setCellValue("姓名");
				} else if (s == 1) {
					cell.setCellValue("日期");
				} else if (s == 2) {
					cell.setCellValue("时间");
				} else if (s == 3) {
					cell.setCellValue("共计加班时间");
				} else if (s == 4) {
					cell.setCellValue("加班合计");
				}
			}
			try {
				if (realList != null && realList.size() > 0) {
					int s = 2;  //内容从第3行开始
					for (int i = 0; i < realList.size(); i++) {
						AbnormalTime tr = (AbnormalTime) realList.get(i);
						row = sheet.createRow(s++);
						for (int j = 0; j <= 4; j++) {
							cell = row.createCell(j);
							if (j == 0) {
								cell.setCellValue(tr.getName());
							} else if (j == 1) {
								cell.setCellValue(tr.getSignDate());
							} else if (j == 2) {
								cell.setCellValue(tr.getSignTime());
							} else if (j == 3) {
								cell.setCellValue(tr.getDayAbnormalTime());
							} else if (j == 4) {
								cell.setCellValue(tr.getAbnormalTime());
							}
						}
					}
				}
				
				wb.write(response.getOutputStream());
				response.getOutputStream().close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "downsxls";
			} catch (IOException e) {
				e.printStackTrace();
				return "downsxls";
			}
			return null;
		} else {
			return ERROR;
		}
	}
	
	
	// 读取天津天房科技天科云样表 excel数据
	private List<Worker> readExcel1(File uploadFile1) throws Exception {
		String number = null;
		String name = null;
		FileInputStream fis = null;
		POIFSFileSystem fs = null;// 处理流
		HSSFWorkbook wb = null;//
		HSSFSheet hssfsheet = null;// 工作表
		HSSFRow hssfrow = null;// 单元行
		List<Worker> workerList1 = new ArrayList<Worker>();
		fis = new FileInputStream(uploadFile1);
		fs = new POIFSFileSystem(fis);
		wb = new HSSFWorkbook(fs);
		
		allPeopleName = new ArrayList<String>();
		
		hssfsheet = wb.getSheetAt(0);// 第一个工作表
		hssfrow = hssfsheet.getRow(0);// 第一行
		int sheetcou = wb.getNumberOfSheets();
		for (int i = 0; i < sheetcou; i++) {// 从第1个sheet开始
			hssfsheet = wb.getSheetAt(i);// 第i个sheet的数据行
			int cou = hssfsheet.getPhysicalNumberOfRows();// 获得当前表有多少行数据
			for (int j = 3; j < cou; j++) {// 从第4行开始
				try {
					hssfrow = hssfsheet.getRow(j);
					// 判断是否还存在需要导入的数据
					if (hssfrow == null) {
						break;
					}
					if(hssfrow.getCell(0).getCellType() != 0) {
						break;
					}
					
					/** 将EXCEL中的第 j 行，第一列的值插入到实例中 */
					/**
					 * CELL_TYPE_NUMERIC 数值型 0 CELL_TYPE_STRING 字符串型 1
					 * CELL_TYPE_FORMULA 公式型 2 CELL_TYPE_BLANK 空值 3
					 * CELL_TYPE_BOOLEAN 布尔型 4 CELL_TYPE_ERROR 错误 5
					 */
					String dept = hssfrow.getCell(1).getStringCellValue();
					if(!NEED_DEPT.contains(dept.trim())) {
						continue;
					}
					
					Worker worker = new Worker();
					number = hssfrow.getCell(3).getStringCellValue().trim();
					number = number.replace("@tfkj", "");
					name = hssfrow.getCell(2).getStringCellValue().trim();
					//名字前加上工号
					name= FayeUtils.getUserName(number, name);
					String signDate = hssfrow.getCell(4).getStringCellValue();
	
					// 登记号码
					worker.setNumber(number);
					// 姓名
					worker.setName(name);
					worker.setSignDate(signDate);
					worker.setSignTime1(hssfrow.getCell(5).getStringCellValue());
					worker.setSignName1(hssfrow.getCell(6).getStringCellValue());
					worker.setSignSite1(hssfrow.getCell(8).getStringCellValue());
					worker.setSignTime2(hssfrow.getCell(10).getStringCellValue());
					worker.setSignName2(hssfrow.getCell(11).getStringCellValue());
					worker.setSignSite2(hssfrow.getCell(13).getStringCellValue());
					worker.setWeek(findWeek(signDate));
					Date date = DateUtil.strToDate(signDate, "yyyy-MM-dd");
					if(minDate == null || DateUtil.dateDiff(minDate, date, Calendar.DATE) > 0) {
						minDate = date;
					}
					if (maxDate == null || DateUtil.dateDiff(maxDate, date, Calendar.DATE) < 0) {
						maxDate = date;
					}
					workerList1.add(worker);
					//
					if(!allPeopleName.contains(name)) {
						allPeopleName.add( name);
					}
				}catch(Exception e) {
					throw new Exception("第一个excel第["+j+"]行发生异常:",e);
				}
			}
			
		}
		return workerList1;
	}
	
	// 根据日期计算星期
	private String findWeek(String signTime) throws ParseException {
		// 星期
		String[] weekDaysName = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar calendar = Calendar.getInstance();
//		try {
			calendar.setTime(dateFormat3.parse(signTime));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
	
	// 读取请假申请单 excel数据
	private List<Leave> readExcel2(File uploadFile2) throws Exception {
		String name = null;
		FileInputStream fis = null;
		POIFSFileSystem fs = null;// 处理流
		HSSFWorkbook wb = null;//
		HSSFSheet hssfsheet = null;// 工作表
		HSSFRow hssfrow = null;// 单元行
		List<Leave> leaveList1 = new ArrayList<Leave>();
		fis = new FileInputStream(uploadFile2);
		fs = new POIFSFileSystem(fis);
		wb = new HSSFWorkbook(fs);
		
		hssfsheet = wb.getSheetAt(0);// 第一个工作表
		hssfrow = hssfsheet.getRow(0);// 第一行
		int sheetcou = wb.getNumberOfSheets();
		for (int i = 0; i < sheetcou; i++) {// 从第1个sheet开始
			hssfsheet = wb.getSheetAt(i);// 第i个sheet的数据行
			int cou = hssfsheet.getPhysicalNumberOfRows();// 获得当前表有多少行数据
			for (int j = 3; j < cou; j++) {// 从第4行开始
				try {
					hssfrow = hssfsheet.getRow(j);
					// 判断是否还存在需要导入的数据
					if (hssfrow == null || hssfrow.getCell(0)== null) {
						break;
					}
					if(hssfrow.getCell(0).getCellType() != 1) {
						break;
					}
					
					/** 将EXCEL中的第 j 行，第一列的值插入到实例中 */
					/**
					 * CELL_TYPE_NUMERIC 数值型 0 CELL_TYPE_STRING 字符串型 1
					 * CELL_TYPE_FORMULA 公式型 2 CELL_TYPE_BLANK 空值 3
					 * CELL_TYPE_BOOLEAN 布尔型 4 CELL_TYPE_ERROR 错误 5
					 */
					Leave leave = new Leave();
					if(FayeUtils.isEmpty(hssfrow.getCell(0).getStringCellValue())) {
						continue;
					}
					name = hssfrow.getCell(0).getStringCellValue().trim()+FayeUtils.getUserName(hssfrow.getCell(1).getStringCellValue().trim());
					String dept = hssfrow.getCell(2).getStringCellValue().trim();
					String beginTime = hssfrow.getCell(3).getStringCellValue();
					if(beginTime == null || beginTime.length() != 16) {
						continue;
					}
					
					String endTime = hssfrow.getCell(4).getStringCellValue();
					if(endTime == null || endTime.length() != 16) {
						continue;
					}
	
					leave.setName(name);
					leave.setDept(dept);
					leave.setBeginTime(beginTime);
					leave.setEndTime(endTime);
					leave.setDayNum(DateUtil.getLeaveDayNum(beginTime, endTime));
					
					String shijia = hssfrow.getCell(6).getStringCellValue().trim();
					String bingjia = hssfrow.getCell(7).getStringCellValue().trim();
					String zhuyuan = hssfrow.getCell(8).getStringCellValue().trim();
					leave.setLeaveType("1");
					leave.setLeaveMsg("事假");
					if("勾选".equals(bingjia) || "勾选".equals(zhuyuan)) {
						leave.setLeaveType("2");
						leave.setLeaveMsg("病假");
					}
					
					leaveList1.add(leave);
				}catch(Exception e) {
					throw new Exception("第二个excel第["+j+"]行发生异常:",e);
				}
			}
			
		}
		return leaveList1;
	}
	
	// 读取带薪休假申请表 excel数据
	private List<Leave> readExcel3(File uploadFile3) throws Exception {
		String name = null;
		FileInputStream fis = null;
		POIFSFileSystem fs = null;// 处理流
		HSSFWorkbook wb = null;//
		HSSFSheet hssfsheet = null;// 工作表
		HSSFRow hssfrow = null;// 单元行
		List<Leave> leaveList1 = new ArrayList<Leave>();
		fis = new FileInputStream(uploadFile3);
		fs = new POIFSFileSystem(fis);
		wb = new HSSFWorkbook(fs);
		
		hssfsheet = wb.getSheetAt(0);// 第一个工作表
		hssfrow = hssfsheet.getRow(0);// 第一行
		int sheetcou = wb.getNumberOfSheets();
		for (int i = 0; i < sheetcou; i++) {// 从第1个sheet开始
			hssfsheet = wb.getSheetAt(i);// 第i个sheet的数据行
			int cou = hssfsheet.getPhysicalNumberOfRows();// 获得当前表有多少行数据
			for (int j = 3; j < cou; j++) {// 从第4行开始
				try {
					hssfrow = hssfsheet.getRow(j);
					// 判断是否还存在需要导入的数据
					if (hssfrow == null || hssfrow.getCell(0) == null) {
						break;
					}
					/** 将EXCEL中的第 j 行，第一列的值插入到实例中 */
					/**
					 * CELL_TYPE_NUMERIC 数值型 0 CELL_TYPE_STRING 字符串型 1
					 * CELL_TYPE_FORMULA 公式型 2 CELL_TYPE_BLANK 空值 3
					 * CELL_TYPE_BOOLEAN 布尔型 4 CELL_TYPE_ERROR 错误 5
					 */
					Leave leave = new Leave();
					if(FayeUtils.isEmpty(hssfrow.getCell(0).getStringCellValue())) {
						continue;
					}
					name = hssfrow.getCell(0).getStringCellValue().trim()+FayeUtils.getUserName(hssfrow.getCell(1).getStringCellValue().trim());
					String dept = hssfrow.getCell(2).getStringCellValue().trim();
					String leaveMsg = hssfrow.getCell(3).getStringCellValue().trim();
					String beginTime = hssfrow.getCell(4).getStringCellValue();
					if(beginTime == null || beginTime.length() != 16) {
						continue;
					}
					
					int dayNum = (int)(hssfrow.getCell(5).getNumericCellValue());
					Date endTimeDate = DateUtil.addDay(DateUtil.strToDate(beginTime, "yyyy-MM-dd HH:mm"), dayNum);
					String endTime = DateUtil.dateToStr(endTimeDate, "yyyy-MM-dd HH:mm");
					
					leave.setName(name);
					leave.setDept(dept);
					leave.setBeginTime(beginTime);
					leave.setEndTime(endTime);
					leave.setDayNum(Float.valueOf(dayNum));
					leave.setLeaveType("3");
					leave.setLeaveMsg(leaveMsg);
					leaveList1.add(leave);
				}catch(Exception e) {
					throw new Exception("第三个excel第["+j+"]行发生异常:",e);
				}
			}
			
		}
		return leaveList1;
	}
	
	//返回值 key:日期 ；value：是否工作日
	private Map<String,Boolean> makeWorkDays() throws ParseException {
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		Date date = minDate;
		//上班时间 应该放假
		List<String> holidays = new ArrayList<String>();
		if(!FayeUtils.isEmpty(holiday)) {
			holidays = Arrays.asList(holiday.split(","));
		}
		//放假的日子 但是应该上班
		List<String> weekendWorkdays = new ArrayList<String>();
		if(!FayeUtils.isEmpty(weekendWorkday)) {
			weekendWorkdays = Arrays.asList(weekendWorkday.split(","));
		}
		while (DateUtil.dateDiff(date, maxDate, Calendar.DATE) <= 0) {
			String key = DateUtil.dateToStr(date, "yyyy-MM-dd");
			boolean value = true;
			String week = findWeek(key);
			if("六".equals(week) || "日".equals(week) || holidays.contains(key)) {
				value = false;
			}
			if(weekendWorkdays.contains(key)) {
				value = true;
			}
			map.put(key, value);
			date = DateUtil.addDay(date, 1);
		}
		return map;
	}
	
	private List<Worker> makeWorkerList(List<Worker> workerList,List<Leave> leaveList,Map<String, Boolean> workDayMap) throws ParseException {
		//key1:用户姓名；  key2：日期 ；value：worker
		Map<String,Map<String,Worker>> workerMap = new HashMap<String,Map<String,Worker>>();
		for (Worker worker : workerList) {
			Map<String, Worker> dayMap = workerMap.get(worker.getName());
			if(dayMap == null) {
				dayMap = new HashMap<String, Worker>();
			}
			dayMap.put(worker.getSignDate(),worker);
			workerMap.put(worker.getName(), dayMap);
		}
		
		Date date = minDate;
		while (DateUtil.dateDiff(date, maxDate, Calendar.DATE) <= 0) {
			String key = DateUtil.dateToStr(date, "yyyy-MM-dd");
			Boolean isWorkDay = workDayMap.get(key);
			Set<Entry<String, Map<String, Worker>>> wokerMap = workerMap.entrySet();
			for (Entry<String, Map<String, Worker>> entry : wokerMap) {
				Map<String, Worker> dayMap = entry.getValue();
				String wokerName = entry.getKey();
				Worker woker = dayMap.get(key);
				if(woker == null) {
					woker = new Worker();
					// 姓名
					woker.setName(wokerName);
					woker.setSignDate(key);
					woker.setSignTime1("-");
					woker.setSignName1("缺卡");
					woker.setSignTime2("-");
					woker.setSignName2("缺卡");
					woker.setWeek(findWeek(key));
				}
				//判断这个人这一天的考勤情况
				if(isWorkDay) {
					//1.旷工
					if("缺卡".equals(woker.getSignName1()) && "缺卡".equals(woker.getSignName2())) {
						woker.setFlog("3");
						woker.setFlogName("旷工");
						//处理请假
						dealLeave(woker, date, leaveList);
						dayMap.put(key, woker);
						continue;
					}
					//2.上班未打卡
					if("缺卡".equals(woker.getSignName1())) {
						woker.setFlog("1");
						woker.setFlogName("上班未打卡");
						dayMap.put(key, woker);
						continue;
					}
					//3.下班未打卡
					if("缺卡".equals(woker.getSignName2())) {
						woker.setFlog("1");
						woker.setFlogName("下班未打卡");
						dayMap.put(key, woker);
						continue;
					}
					//4.迟到早退
					if(DateUtil.compareStrTime(woker.getSignTime1(), normTime1)
							|| DateUtil.compareStrTime(normTime2,woker.getSignTime2())) {
						if(!FayeUtils.isEmpty(woker.getSignSite1())) {
							boolean isNeedSite = false;
							for (String needSite : NEED_SITE) {
								if(woker.getSignSite1().indexOf(needSite) != -1) {
									isNeedSite = true;
									break;
								}
							}
							if(isNeedSite) {
								continue;
							}
						}
						woker.setFlog("1");
						woker.setFlogName("迟到/早退");
						dayMap.put(key, woker);
						continue;
					}
				}else {
					//加班
					if(!"缺卡".equals(woker.getSignName1()) && !"缺卡".equals(woker.getSignName2())) {
						float hour = DateUtil.diffStrTimeH(woker.getSignTime2(), woker.getSignTime1());
						woker.setFlog("2");
						woker.setFlogName("加班");
						woker.setAbnormalTime(hour);
						dayMap.put(key, woker);
						continue;
					}
					if(!"缺卡".equals(woker.getSignName1()) || !"缺卡".equals(woker.getSignName2())){
						woker.setFlog("2");
						woker.setFlogName("加班只打一次卡");
						dayMap.put(key, woker);
						continue;
					}
				}
				dayMap.put(key, woker);
//				workerMap.put(key, value)
			}
			date = DateUtil.addDay(date, 1);
		}
		
		List<Worker> result = new ArrayList<Worker>();
		for (String name : allPeopleName) {
			Map<String, Worker> workerDateMap = workerMap.get(name);
			date = minDate;
			while (DateUtil.dateDiff(date, maxDate, Calendar.DATE) <= 0) {
				String dateKey = DateUtil.dateToStr(date, "yyyy-MM-dd");
				Worker worker = workerDateMap.get(dateKey);
				result.add(worker);
				date = DateUtil.addDay(date, 1);
			}
		}
		return result;
	}
	
	private void dealLeave(Worker worker,Date date,List<Leave> leaveList) throws ParseException {
		for (Leave leave : leaveList) {
			if(leave.getName().equals(worker.getName())) {
				Date bDate = DateUtil.strToDate(leave.getBeginTime(), "yyyy-MM-dd HH:mm");
				bDate = DateUtil.floorDay(bDate);
				Date eDate = DateUtil.strToDate(leave.getEndTime(), "yyyy-MM-dd HH:mm");
				eDate = DateUtil.ceilDay(eDate);
				if(DateUtil.dateDiff(date, bDate, Calendar.DATE) >=0 && DateUtil.dateDiff(date, eDate, Calendar.DATE) <=0) {
					worker.setFlog("4");
					worker.setFlogName(leave.getLeaveMsg());
				}
			}
		}
	}
	
	private void saveNeedDept() {
		String[] needDepts = needDept.split(",");
		NEED_DEPT = new ArrayList<String>();
		for (String nd : needDepts) {
			NEED_DEPT.add(nd.trim());
		}
	}
	
	private void saveNeedSite() {
		String[] needSites = needSite.split(",");
		NEED_SITE = new ArrayList<String>();
		for (String ns : needSites) {
			NEED_SITE.add(ns.trim());
		}
	}
	
	// =============================================================================================


	public SimpleDateFormat getDateFormat3() {
		return dateFormat3;
	}

	public void setDateFormat3(SimpleDateFormat dateFormat3) {
		this.dateFormat3 = dateFormat3;
	}


	public void setNormTime3(String normTime3) {
		this.normTime3 = normTime3;
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

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
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

	public File[] getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File[] uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String[] getUploadpicFileName() {
		return uploadpicFileName;
	}

	public void setUploadpicFileName(String[] uploadpicFileName) {
		this.uploadpicFileName = uploadpicFileName;
	}

	public String[] getUploadpicContentType() {
		return uploadpicContentType;
	}

	public void setUploadpicContentType(String[] uploadpicContentType) {
		this.uploadpicContentType = uploadpicContentType;
	}

	public String getNeedDept() {
		String str = "";
		for (int i = 0; i < NEED_DEPT.size(); i++) {
			if(i !=0) {
				str += ",";
			}
			str += NEED_DEPT.get(i);
		}
		return str;
	}

	public void setNeedDept(String needDept) {
		this.needDept = needDept;
	}

	public String getNeedSite() {
		String str = "";
		for (int i = 0; i < NEED_SITE.size(); i++) {
			if(i !=0) {
				str += ",";
			}
			str += NEED_SITE.get(i);
		}
		return str;
	}

	public void setNeedSite(String needSite) {
		this.needSite = needSite;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

}
