<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'updatekeywords.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
     <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="15" height="30"><img src="login/images/tab_03.gif" width="15" height="30" /></td>
        <td width="1101" background="login/images/tab_05.gif"><img src="login/images/311.gif" width="16" height="16" /> <span class="STYLE4">服务器黑名单操作</span></td>
        </tr></table>
 </td>
 </tr>
 <tr><td>
  <center>
       修改黑名单<br>
   <form action="login/updatebackuser.action" method="post"> 
    <textarea name="blackuser" rows="40" cols="150">${blackuser}
</textarea>
   <input type="submit" value="确认修改">
   </form>
   </center></td><td align="left" ><p style="line-height:30px; font-size:16px; width:150px; height:600px;"> 电话号码之间使用逗号隔开，列表第一个字符不能为","，列表最后必须以","结尾，间隔符号必须使用英文下的","
   <br><br> 提示信息：<font color="red">${sessionScope.backlists}</font>
   </p>
    </td>
 </tr>
 </table>   
  
  
  </body>
</html>
