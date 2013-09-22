<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String servletPath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort() + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>

<base href="<%=basePath%>" />
<link rel="shortcut icon" href="<%=basePath%>/favicon.ico"  type="image/x-icon" /> 
<title>天房科技考勤统计</title>
<style type="text/css">
body {
	background: url(images/background1.png) fixed;
	cursor:default;
}

.center {
	background: #fff;
	margin: 20px auto;
	width: 80%;
	text-align: center;
}

.table {
	margin: auto;
	width: 700px;
	border-collapse: collapse;
}

.table tr td {
	border: solid #ccc 1px;
	font-size: 16px;
	height: 20px;
}

.trContent ,.trJiaban {
	height: 30px;
}

.table tr:hover , #workerMenu li:hover{
	background-color: #000000;
	color: #FFFFFF;
}

.cleft_box {
	position: absolute;
	left: 150px;
	top: 100px;
	padding: 0px;
	margin: 0px;
	font-size: 14px;
	color: #BB5E00;

}
#workerMenu {
	width:400px;
	position: absolute;
	left: 150px;
	bottom:50px;
	padding: 0px;
	margin: 0px;
	font-size: 14px;
	color: #BB5E00;
	background-color:#F0F0F0;
}
#workerMenu ul li{
width:30%;
float:left;
list-style-type: none;
border-style: ridge;
border-color: #CCC;
text-align: center;
}
.trKuangGong , #workerMenu{
	display: none;
}

.menuImg{
	position: absolute;
	background-image:url("images/button1.png");
	width:39px;
	height:40px;
	left: 150px;
	bottom:10px;
	padding: 0px;
	margin: 0px;
}
.menuImg:hover{
	background-image:url("images/button2.png");
}

#link1{
	position: absolute;
	right: 150px;
	bottom:10px;
}
#link2{
	position: absolute;
	right: 150px;
	bottom:130px;
}

#closeMenu{
	margin-top: 0px;
}
</style>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript">
		$(function(){
			$("#form1").submit(function(){
				if(""==$("#file1").val()){
					alert("想嘛呢！！！没上传文件！！！");
					return false;
				}
			});
			
			$("#callback").click(function(){
				location="<%=basePath%>";
		});

		$("#yincangButton").click(function() {
			$(".yincang").hide();
			$(document).scrollTop(0);
		});

		$("#xianshiButton").click(function() {
			$(".yincang").show();
			$(document).scrollTop(0);
		});
		
		$("#dakayici").click(function(){
			$(".table tr").each(function(){
				if($(this).attr("class")=="trJiaban"&&$(this).find("#abnormalTime").text()==0){
					$(this).show();
				}else if($(this).attr("class")=="tableHead"){
					$(this).show();
				}else{
					$(this).hide();
				}
			});
			$(document).scrollTop(0);
		});
		
		$("#wentikaoqin").click(function(){
			$(".table tr").each(function(){
				if($(this).attr("class")=="tableHead"||$(this).attr("class")=="trContent"||$(this).attr("class")=="trKuangGong"){
					$(this).show();
				}else{
					$(this).hide();
				}
			});
			$(document).scrollTop(0);
		});
		
		$("#huifu").click(function(){
			window.location.reload();
		});
		
		//图标相对窗口静止不动
		$(window).scroll(function(){
			//$(document).scrollTop() : 设置 <div> 元素中滚动条的垂直偏移：
			//parseInt : 解析一个字符串，并返回一个整数
			nowtop = parseInt($(document).scrollTop());	
			$('.cleft_box').css('top', nowtop + 100 + 'px');
			$('#workerMenu').css('bottom', -nowtop + 50 + 'px');
			$('.menuImg').css('bottom', -nowtop + 10 + 'px');
			$('#link1').css('bottom', -nowtop + 10 + 'px');
			$('#link2').css('bottom', -nowtop + 130 + 'px');
		});
		
		$("#buKuang").click(function(){
			$(".trKuangGong").hide();
			$(".trContent").add(".trJiaban").add(".yincang").show();
			$(document).scrollTop(0);
		});
		
		$("#kuang").click(function(){
			$(".trKuangGong").show();
			$(".trContent").add(".trJiaban").add(".yincang").hide();
			$(document).scrollTop(0);
		});
		//双击tr选出全部这个人的信息
		$("tr").dblclick(function(){
			$workerName=$(this).find(".workerName").text();
			if($workerName!=""){
				$(".trContent").add(".trJiaban").add(".trKuangGong").add(".yincang").hide();
				$("tr").each(function(){
					if($(this).find(".workerName").text()==$workerName){
						$(this).show();
					}
				});
				$(document).scrollTop(0); 
			}
		});
		
		//同名删除
		$(".deleteWorker").click(function(){
			$workerName=$(this).parent().parent().find(".workerName").text();
			if($workerName!=""){
				$("tr").each(function(){
					if($(this).find(".workerName").text()==$workerName){
						$(this).remove();
					}
				});
				//人名菜单删除本人
				$("#workerMenu li").each(function(){
					if($workerName=="苏丽沣(最伟大的程序提供者！)"){
					$workerName="苏丽沣";
				}
					if($(this).find("a").text()==$workerName){
						$(this).remove();
					}
				});
			}
		});
		
		//人名菜单点击li选出全部本人信息，人名菜单隐藏
		$("#workerMenu li").click(function(){
			
			$workerName=$(this).find("a").text();
			if($workerName=="苏丽沣"){
				$workerName="苏丽沣(最伟大的程序提供者！)";
			}
			if($workerName!=""){
				$(".trContent").add(".trJiaban").add(".trKuangGong").add(".yincang").hide();
				$("tr").each(function(){
					if($(this).find(".workerName").text()==$workerName){
						$(this).show();
					}
				});
				$(document).scrollTop(0); 
			}
			$("#workerMenu").hide();
		});
		
		//菜单键点击
		$(".menuImg").click(function(){
			if($("#workerMenu").is(":visible")==true){
				$("#workerMenu").hide();
			}else{
				$("#workerMenu").show();
			}
		});
		
		$("#abnormalTime").click(function(){
			/**
				window.open([URL ][, name ][, features ][, replace]]]])
				URL：新窗口的URL地址
				name：新窗口的名称，可以为空
				featurse：属性控制字符串，在此控制窗口的各种属性，属性之间以逗号隔开。
				fullscreen= { yes/no/1/0 } 是否全屏，默认no
				channelmode= { yes/no/1/0 } 是否显示频道栏，默认no
				toolbar= { yes/no/1/0 } 是否显示工具条，默认no
				location= { yes/no/1/0 } 是否显示地址栏，默认no
				directories = { yes/no/1/0 } 是否显示转向按钮，默认no
				status= { yes/no/1/0 } 是否显示窗口状态条，默认no
				menubar= { yes/no/1/0 } 是否显示菜单，默认no
				scrollbars= { yes/no/1/0 } 是否显示滚动条，默认yes
				resizable= { yes/no/1/0 } 是否窗口可调整大小，默认no
				width=number 窗口宽度（像素单位）
				height=number 窗口高度（像素单位）
				top=number 窗口离屏幕顶部距离（像素单位）
				left=number 窗口离屏幕左边距离（像素单位）
			*/
			var sFeatures = "height=600, width=600, scrollbars=yes,top=100,left=500";
			window.open( "listAbnormalTime.action", '', sFeatures );
		});
		
		$("#exportAbnormalTime").click(function(){
			/**
				window.open([URL ][, name ][, features ][, replace]]]])
				URL：新窗口的URL地址
				name：新窗口的名称，可以为空
				featurse：属性控制字符串，在此控制窗口的各种属性，属性之间以逗号隔开。
				fullscreen= { yes/no/1/0 } 是否全屏，默认no
				channelmode= { yes/no/1/0 } 是否显示频道栏，默认no
				toolbar= { yes/no/1/0 } 是否显示工具条，默认no
				location= { yes/no/1/0 } 是否显示地址栏，默认no
				directories = { yes/no/1/0 } 是否显示转向按钮，默认no
				status= { yes/no/1/0 } 是否显示窗口状态条，默认no
				menubar= { yes/no/1/0 } 是否显示菜单，默认no
				scrollbars= { yes/no/1/0 } 是否显示滚动条，默认yes
				resizable= { yes/no/1/0 } 是否窗口可调整大小，默认no
				width=number 窗口宽度（像素单位）
				height=number 窗口高度（像素单位）
				top=number 窗口离屏幕顶部距离（像素单位）
				left=number 窗口离屏幕左边距离（像素单位）
			*/
			var sFeatures = "height=600, width=600, scrollbars=yes,top=100,left=500";
			window.open( "exportAbnormalTime.action", '', sFeatures );
		});
	});
	
</script>
</head>

<body>
<!-- 浮动页 -->
<div class="cleft_box">
	黄色：加班！！<br />
	红的：迟到或早退！！<br />
	灰色：旷工！！<br />
	六日上班都算加班<br />
	本次统计起止时间：<br />
	<s:property value="startTime" />致<s:property value="endTime" /><br /><br />
	
	<input type="button" id="huifu" value="恢复原始状态" /> 
	<!-- <input type="button" id="kuang" value="只统计旷工人" /><br />
	<input type="button" id="yincangButton" value="隐藏正常考勤" />
	<input type="button" id="xianshiButton" value="显示正常考勤" /><br /> -->
	<input type="button" id="callback" value="清空所有数据" /><br />
	<input type="button" id="buKuang" value="显示未删人员" /> 
	
	<input type="button" id="abnormalTime" value="员工加班情况" /><br />
	<input type="button" id="dakayici" value="节假日加班只打卡一次" /><br />
	<input type="button" id="wentikaoqin" value="显示迟到及旷工人员" /><br />
	<input type="button" id="exportAbnormalTime" value="导出加班表格" /><br /><br />
	
	<div style="font-size: 16px;color:#A23400;text-align: center;">老孙&nbsp;&nbsp;研发<br />裙下尽责&nbsp;&nbsp;技术支持</div>
</div>

<div id="workerMenu">
	<ul>
		<s:iterator value="allPeopleName" id="peopleName">
			<li>
				<s:if test='#peopleName=="苏丽沣(最伟大的程序提供者！)"'>
					<a>苏丽沣</a>
				</s:if>
				<s:else>
					<a><s:property value="peopleName"/></a>
				</s:else>
			</li>
		</s:iterator>
	</ul>
</div>
<div class="menuImg"></div>
<a id="link1" href="<%= servletPath %>orderDishes" target="_blank" ><img src="images/wenzhang.png" /></a>
<a id="link2" href="<%= basePath %>initSuanqiu" target="_blank" ><img src="images/suanqiu.jpg" /></a>

<!-- 正文 -->
	<div class="center">
		<div align="center">
			<s:form action="aainput" namespace="/" id="form1" method="post" enctype="multipart/form-data">
				<s:file id="file1" name="uploadFile" label="考勤表"></s:file>
				<s:textfield name="normTime1" value="08:35:00" label="上班时间"></s:textfield>
				<s:textfield name="normTime2" value="17:30:00" label="下班时间"></s:textfield>
				<s:textfield name="normTime3" value="19:30:00" label="加班时间"></s:textfield>
				<s:textfield name="holiday" label="放假时间" ></s:textfield>
				<s:textfield name="weekendWorkday" label="放假时间算上班" ></s:textfield>
				<s:submit value="开始"></s:submit>
			</s:form>
		</div>
		<s:property value="workerList.size()" />
		<table class="table">
			<tr class="tableHead" >
				<td>序号</td>
				<td>登记号码</td>
				<td>姓名</td>
				<td>出勤时间</td>
				<td>星期</td>
				<td>加班时间</td>
				<td>flog</td>
			</tr>
			
			<s:iterator value="workerList" status="st">
				<s:if test="flog==1">
					<tr bgcolor="#FF2D2D" class="trContent">
				</s:if>
				<s:elseif test="flog==2">
					<tr bgcolor="#F9F900" class="trJiaban">
				</s:elseif>
				<s:elseif test="flog==3">
					<tr bgcolor="#7B7B7B" class="trKuangGong">
				</s:elseif>
				<s:else>
					<tr class="yincang">
				</s:else>
				<td><s:property value="#st.count" /></td>
				<td><s:property value="number" /></td>
				<td><a class="workerName"><s:property value="name" /><input type="button" class="deleteWorker" value="同名删除" /></a></td>
				<td><s:property value="signTime" /></td>
				<td><s:property value="week" /></td>
				<td id="abnormalTime" ><s:property value="abnormalTime" /></td>
				<td><s:property value="flog" /></td>
				</tr>
			</s:iterator>
		</table>
	</div>
</body>
</html>
