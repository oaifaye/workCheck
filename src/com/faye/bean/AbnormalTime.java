package com.faye.bean;

public class AbnormalTime {
	private String name;
	private String signDate;//加班的日期
	private String signTime;//加班时间
	private double dayAbnormalTime;//每天的加班时间
	private double abnormalTime;//总加班时间
	

	public String getName() {
		return name;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getDayAbnormalTime() {
		return dayAbnormalTime;
	}

	public void setDayAbnormalTime(double dayAbnormalTime) {
		this.dayAbnormalTime = dayAbnormalTime;
	}

	



}
