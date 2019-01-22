package com.faye.bean;

public class Worker {
	private String number;
	private String name;
	private String signTime;
	private String flog;//1:迟到早退 、上班未打卡、下班未打卡（红） 2：加班、加班只打一次卡（黄） 3：旷工（灰）  4:请假 事假、年假、病假
	private String week;
	private double abnormalTime;
	private String signDate;
	
	private String signTime1;//上班时间
	private String signName1;//上班时间的文字
	private String signSite1;//上班地点的文字
	private String signTime2;//下班时间
	private String signName2;//下班时间的文字
	private String signSite2;//下班地点的文字
	private String flogName;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public String getFlog() {
		return flog;
	}

	public void setFlog(String flog) {
		this.flog = flog;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public double getAbnormalTime() {
		return abnormalTime;
	}

	public void setAbnormalTime(double abnormalTime) {
		this.abnormalTime = abnormalTime;
	}

	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	public String getSignTime2() {
		return signTime2;
	}

	public void setSignTime2(String signTime2) {
		this.signTime2 = signTime2;
	}

	public String getSignTime1() {
		return signTime1;
	}

	public void setSignTime1(String signTime1) {
		this.signTime1 = signTime1;
	}

	public String getSignName1() {
		return signName1;
	}

	public void setSignName1(String signName1) {
		this.signName1 = signName1;
	}

	public String getSignName2() {
		return signName2;
	}

	public void setSignName2(String signName2) {
		this.signName2 = signName2;
	}

	public String getFlogName() {
		return flogName;
	}

	public void setFlogName(String flogName) {
		this.flogName = flogName;
	}

	public String getSignSite1() {
		return signSite1;
	}

	public void setSignSite1(String signSite1) {
		this.signSite1 = signSite1;
	}

	public String getSignSite2() {
		return signSite2;
	}

	public void setSignSite2(String signSite2) {
		this.signSite2 = signSite2;
	}

}
