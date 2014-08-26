<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'addsms.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
<script>
function addph()
{
var phvalue=document.getElementById("nameph").value;
var phlist=document.getElementById("phlist").value;
	if(isNaN(phvalue))
	  {alert("添加的对象有非法字符存在");}else{
        if(phvalue.length==11){
        	document.getElementById("phlist").value=phlist+phvalue+",";
            }else{alert("添加的对象不是一个电话号码");}}
}

function deleteph()
{
var phvalue=document.getElementById("nameph").value;
var phlist=document.getElementById("phlist").value;
var context=document.getElementById("contextsms").value;

if(isNaN(phvalue))
{alert("添加的对象有非法字符存在");}else{
  if(phvalue.length==11){
	  window.location.href="login/smsdeletephsend.action?phonelist="+phlist+"&smscontex="+context+"&phone="+phvalue;
      }else{alert("添加的对象不是一个电话号码");}}
}

function regist()
{
	var phfile=document.getElementById("smsfile").value;
	if(phfile.length>1){
	window.location.href="login/smsaddlistfile.action?smsfile="+phfile;}else{alert("请选择一个手机号码集文件");}
}
</script>
  </head>
  <body >
  <br><br><br><br>
  <center>
  <form action="login/smsaddsend.action"  method="post">
    <table width="80%" border="1" align="center" cellpadding="0" bordercolor="#00E00D" cellspacing="0" > 
      <tr>
     <td align="right">添加号码:&nbsp;&nbsp;</td>
     <td>&nbsp;&nbsp;<input type="button" onclick="addph()" value="添加号码">&nbsp;&nbsp;<input type="text" id="nameph" maxlength="11">
     &nbsp;&nbsp;<input type="button" onclick="deleteph()" value="删除号码">&nbsp;&nbsp;&nbsp;&nbsp;
  
     </td>
     </tr>
        <tr>
        <td align="right">SP号:&nbsp;&nbsp;</td>
     <td>&nbsp;&nbsp;   <select name="smstype">
	<option value="2" selected>营销
	<option value="1">OA
</select></td>
     </tr>
      <tr>
     <td align="right">导入号码集:&nbsp;&nbsp;</td>
     <td>&nbsp;&nbsp;<font style="FONT-SIZE:15px;COLOR:#FF8000">号码集文件:</font>
		<input type="File" id="smsfile" size="40" >&nbsp;&nbsp;&nbsp;<input type="button" onclick="regist()" value="导入资源">
		<font color="red" size="2">${errrmessge}</font>
		</td>
     </tr>
      <tr>
     <td align="right">号码集:&nbsp;&nbsp;</td>
     <td><textarea name="phonelist" id="phlist"  rows="10" cols="80" readonly>${phonelist}</textarea></td>
     </tr>
     <tr>
        <td>&nbsp;</td>
     <td>&nbsp;&nbsp;手机号码集文件为*.txt 文件，电话号码之间使用英文的","隔开    
                      规范文件<a href="<%=basePath%>/login/phonlist.txt">示例</a></td>
     </tr>
      <tr>
     <td align="right">短信内容:&nbsp;&nbsp;</td>
     <td><textarea name="smscontex" id="contextsms" rows="10" cols="80">${smscontex}</textarea>
      </td>
     </tr>
     <tr>
     <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td align="center"> <input type="submit" value="确认发送"></td>
     </tr>
     </table>
 </form></center>
  </body>
</html>
