<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <base href="<%=basePath%>" />
    
    <link rel="shortcut icon" href="<%=basePath%>/favicon.ico"  type="image/x-icon" /> 
	<title>天房科技考勤统计</title>
    

  </head>
  
  <body>
    <table>
    
<s:iterator value="abnormalTimeList" >
	<tr>
		<td>
		<s:property value="name" />
		</td>
		<td>
		<s:property value="abnormalTime" />
		</td>
	</tr>
	
</s:iterator>
</table>
  </body>
</html>
