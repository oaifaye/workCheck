<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String servletPath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<base href="<%=basePath%>" />

<title>算逑</title>

<style type="text/css">
 body {
	background: url(images/background2.jpg) center top repeat fixed;
	cursor:default;
} 

/* #bodybg { 
    width: 100%;  
    height: 100%;  
    position: absolute;  
    left: 0px;  
    top: 0px;  
    z-index: -1; 
} 
 
.stretch { 
    width:100%; 
    height:100%; 
}  */

#greybackground {
	background: #000;
	filter: Alpha(opacity = 30);
	-moz-opacity: .3;
	opacity: 0.5;
	display: block;
	z-index: 1;
	width: 100%;
	height: 0px;
	position: absolute;
	top: 0;
	left: 0;
}

.center {
	background: url(images/background3.jpg) ;
	margin: 60px auto;
	width: 1200px;
	height:570px;
	text-align: center;
	padding-top: 20px;
}

.rule{
	float: left;
	margin-left: 50px;
	width: 200px;
	text-align: left;
	color:red;
}

.table {
	margin: auto;
	width: 400px;
	border-collapse: collapse;
	font-size: 40px;
}

#table{
	float:left;
	margin-left: 80px;
}

.table tr td {
	border: double #FF0000  1px;
	background-color:#ffffff;
	height: 100px;
	width: 100px;
}

.answering,.success {
	position: absolute;
	top: 230px;
	left: 350px;
	height: 380px;
	width: 700px;
	background-color: #ffffff;
	text-align: center;
	padding-top: 50px;
	z-index: 2;
}

#answering1,#answering2,#fail,.success {
	display: none;
}

/* BUTTONS */
/*橘黄色*/
.button1 {
	display: block;
	background-color: #FFAA33;
	border: 1px solid #EE7700;
	font-size: 16px;
	font-weight: bold;
	color: #ffffff;
	cursor: pointer;
	padding: 10px;
	padding-top: 5px;
	padding-bottom: 5px;
	float: left;
	margin-left: 300px;
	width: 120px;
}

.button1:hover {
	background-color: #FFBB00;
}

.nameList {
	float:left;
	margin-left: 80px;
	height: 900px;
	width: 320px;
	overflow: scroll;
	height: 400px;
}

.table2 {
	margin: auto;
	width: 300px;
	border-collapse: collapse;
}

.table2 tr td {
	border: double #444444 1px;
	font-size: 16px;
	height: 30px;
}
</style>


<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/game.js"></script>

</head>

<body>
	<!-- <div id="bodybg"> 
   	 <img src="images/background2.jpg" class="stretch" alt="" /> 
	</div> -->
	<div id="greybackground"></div>
	<div class="center">
		<label><input type="radio" name="level" value="0" />练习模式，简单，结果不记录成绩</label><br />
		<label><input type="radio" name="level" value="1" />正式模式，困难，成功后记录排名</label><br /><br />
		<input type="button" id="action" class="button1" value="开始" /> <input type="button"  class="button1"
			id="over" value="完成" /> 
		
		<!-- <div id="test">&nbsp;</div> -->
		<br /><br />
		<div id="needCount"></div><div id="time">&nbsp;</div>
		<div class="rule">
			规则：<br />
			1.选择游戏模式<br />
			2.点击开始<br />
			3.准确点击有数字的方框<br />
			4.保证点击次数与要求相符<br />
			5.点击完成<br /><br />
			提醒：<br />
			1.记住每个方框内的数字<br />
			2.时间在倒计时哦
		</div>
		<div id="table">
			<table class="table">
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</table>
		</div>
		<div class="nameList">
			<a class="rule">上榜者赏糖一枚！！！<br />刷新纪录者赏糖一双！！！</a>
			<table class="table2">
				<tr>
					<td colspan="4">英雄榜</td>
				</tr>
				<tr>
					<td>排名</td>
					<td>名字</td>
					<td>用时(秒)</td>
					<td>备注</td>
				</tr>
				<s:iterator value="suanqiuList" status="st">
					<tr>
						<td><s:property value="#st.count" /></td>
						<td><s:property value="name" /></td>
						<td><s:property value="score" /></td>
						<td><s:property value="comment" /></td>
					</tr>
				</s:iterator>
			</table>
		</div>
	</div>
	<!-- 问题1 -->
	<div class="answering" id="answering1">
		<img src="images/answer1.jpg"/><br /><br />
		请输入表格上的数字加&nbsp;<a id="jisuan"></a>&nbsp;的结果： <input id="answer1" class="" type="text" maxlength="3" /><br /> <br />
		<br /> <input id="answerButton1" class="button1" type="button"
			value="确定" />
	</div>

	<!-- 问题2 -->
	<div class="answering" id="answering2">
		<img src="images/answer2.gif"/><br /><br />
		骚年，请骚等！！！
	</div>

	<input type="hidden" id="flog" />
	
	<!-- 练习模式成功 -->
	<div class="success" id="success1">
		<img src="images/success1.gif"/><br /><br />
		不错哦，还不试试更困难的！！！
	</div>
	
	<!-- 正式模式成功 -->
	<s:form name="submit">
		<div class="success" id="success">
			<img src="images/success.jpg"/>
			成功咯！！<br /><br />
			输入你的名字：<input id="name" class="" type="text" name="name" maxlength="5" /><br /><br />
			输入你的备注：<input id="comment" class="" type="text" name="comment"  maxlength="10"/><br /> <br />
			<br /> <input id="successButton" class="button1" type="button"
				value="确定" />
		</div>
		<input type="hidden" id="score" name="score" />
	</s:form>

	<!-- 失败 -->
	<div class="answering" id="fail">
		<img src="images/fail.gif"/><br /><br />
		失败咯，咯咯咯！！！
	</div>
</body>
</html>
