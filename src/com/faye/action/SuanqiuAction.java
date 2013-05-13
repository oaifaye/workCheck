package com.faye.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts2.ServletActionContext;

import com.faye.bean.Suanqiu;
import com.opensymphony.xwork2.ActionSupport;

public class SuanqiuAction extends ActionSupport {
	private static final long serialVersionUID = 1915421247092408135L;
	private List<Suanqiu> suanqiuList = new ArrayList<Suanqiu>();
	private String name;
	private Integer score;
	private String comment;

	public String initSuanqiu() {
		
		System.out.println("----------------------------------"+ServletActionContext.getRequest().getRemoteAddr()+"----------------------------------");
		System.out.println("开始，");
		readTXT();

		return SUCCESS;
	}

	public String saveSuanqiu() {
		String method = ServletActionContext.getRequest().getMethod();
		if (method.equals("POST")) {
			String realPath = ServletActionContext.getServletContext()
					.getRealPath("\\");
			String wordPath = realPath + "Properties.txt";
			Suanqiu suanqiu = new Suanqiu();
			readTXT();
			suanqiu.setName(name);
			suanqiu.setScore(score);
			suanqiu.setComment(comment);
			// 将新的成绩插入List
			boolean flog = false;
			for (int i = 0; i < suanqiuList.size(); i++) {
				if (suanqiuList.get(i).getScore() > score) {
					suanqiuList.add(i, suanqiu);
					flog=true;
					break;
				}
			}
			if(flog==false){
				suanqiuList.add(suanqiu);
			}
			try {
				// 将List变成String并写入txt
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < suanqiuList.size(); i++) {
					buffer.append(suanqiuList.get(i).getName());
					buffer.append(",");
					buffer.append(suanqiuList.get(i).getScore());
					buffer.append(",");
					buffer.append(suanqiuList.get(i).getComment());
					buffer.append("\r\n");
				}
				FileWriter fileWriter = new FileWriter(wordPath);
				fileWriter.write(buffer.toString());
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return SUCCESS;
	}

	// 读取txt并将内容转化成List<Suanqiu>（调用方法）
	public void readTXT() {
		String realPath = ServletActionContext.getServletContext().getRealPath(
				"\\");
		String wordPath = realPath + "Properties.txt";
		File file = null;
		InputStreamReader read = null;
		String encoding = "GBK"; // 字符编码(可解决中文乱码问题 )
		try {

			file = new File(wordPath);
			read = new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTXT = null;
			while ((lineTXT = bufferedReader.readLine()) != null) {
				Suanqiu suanqiu1 = new Suanqiu();
				String[] lineArray = lineTXT.split(",");
				suanqiu1.setName(lineArray[0]);
				suanqiu1.setScore(Integer.parseInt(lineArray[1]));
				suanqiu1.setComment(lineArray[2]);
				suanqiuList.add(suanqiu1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<Suanqiu> getSuanqiuList() {
		return suanqiuList;
	}

	public void setSuanqiuList(List<Suanqiu> suanqiuList) {
		this.suanqiuList = suanqiuList;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
