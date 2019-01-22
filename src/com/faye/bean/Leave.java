package com.faye.bean;

public class Leave {

	private String dept;
	private String name;
	private String leaveType;//1病假 2事假 3带薪休假
	private String leaveMsg;//病假 事假 陪产假 年休假。。。
	private String beginTime;//yyyy-MM-dd HH:mm 08:30 上午 13:00下午
	private String endTime;
	private float dayNum;
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public String getLeaveMsg() {
		return leaveMsg;
	}
	public void setLeaveMsg(String leaveMsg) {
		this.leaveMsg = leaveMsg;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public float getDayNum() {
		return dayNum;
	}
	public void setDayNum(float dayNum) {
		this.dayNum = dayNum;
	}
	
	
}
