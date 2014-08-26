<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>用户中心</title>
	<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
.STYLE1 {font-size: 12px}
.STYLE4 {
	font-size: 12px;
	color: #1F4A65;
	font-weight: bold;
}

a:link {
	font-size: 12px;
	color: #06482a;
	text-decoration: none;

}
a:visited {
	font-size: 12px;
	color: #06482a;
	text-decoration: none;
}
a:hover {
	font-size: 12px;
	color: #FF0000;
	text-decoration: underline;
}
a:active {
	font-size: 12px;
	color: #FF0000;
	text-decoration: none;
}
.STYLE7 {font-size: 12}

-->
</style>

<script>
var  highlightcolor='#eafcd5';
//此处clickcolor只能用win系统颜色代码才能成功,如果用#xxxxxx的代码就不行,还没搞清楚为什么:(
var  clickcolor='#51b2f6';
function  changeto(){
source=event.srcElement;
if  (source.tagName=="TR"||source.tagName=="TABLE")
return;
while(source.tagName!="TD")
source=source.parentElement;
source=source.parentElement;
cs  =  source.children;
//alert(cs.length);
if  (cs[1].style.backgroundColor!=highlightcolor&&source.id!="nc"&&cs[1].style.backgroundColor!=clickcolor)
for(i=0;i<cs.length;i++){
	cs[i].style.backgroundColor=highlightcolor;
}
}

function  changeback(){
if  (event.fromElement.contains(event.toElement)||source.contains(event.toElement)||source.id=="nc")
return
if  (event.toElement!=source&&cs[1].style.backgroundColor!=clickcolor)
//source.style.backgroundColor=originalcolor
for(i=0;i<cs.length;i++){
	cs[i].style.backgroundColor="";
}
}

function  clickto(){
source=event.srcElement;
if  (source.tagName=="TR"||source.tagName=="TABLE")
return;
while(source.tagName!="TD")
source=source.parentElement;
source=source.parentElement;
cs  =  source.children;
//alert(cs.length);
if  (cs[1].style.backgroundColor!=clickcolor&&source.id!="nc")
for(i=0;i<cs.length;i++){
	cs[i].style.backgroundColor=clickcolor;
}
else
for(i=0;i<cs.length;i++){
	cs[i].style.backgroundColor="";
}
}
</script>
<script language="javascript">
    function closewin()
    {
        var div1=document.getElementById("Layer1");
         div1=document.all.Layer1.style.visibility='hidden';
    }
</script>
<script language="javascript">
    function showw()
    {
        var div1=document.getElementById("Layer1");
        div1=document.all.Layer1.style.visibility='';
    }
</script>
	</head>

	<body>
	
	<div id="Layer1"
			style="position: absolute; left: 200px; top: 50px; width: 200px; height: 100px; z-index:5; background-color: #CCFFFF; layer-background-color: #CCFFFF; border: 1px none #000000; visibility: hidden">
			<center>
				<button onclick="closewin()">
					关 闭
				</button>
				<form action="">
                  <input type="text" name="keyWords"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="submit" value="添加"/>
				</form>
			</center>
		</div>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="15" height="30"><img src="login/images/tab_03.gif" width="15" height="30" /></td>
        <td width="1101" background="login/images/tab_05.gif"><img src="login/images/311.gif" width="16" height="16" /> <span class="STYLE4">服务器进程配置列表</span></td>
        <td width="281" background="login/images/tab_05.gif"><table border="0" align="right" cellpadding="0" cellspacing="0">
            <tr>
              <td width="60">
                                            短信
              </td>
            </tr>
        </table></td>
        <td width="14"><img src="login/images/tab_07.gif" width="14" height="30" /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="9" background="login/images/tab_12.gif">&nbsp;</td>
        <td bgcolor="#f3ffe3"><table width="99%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor="#c0de98" onmouseover="changeto()"  onmouseout="changeback()">
          <tr>
            <td width="6%" height="26" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">选择</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">序号</div></td>
            <td width="52%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">短信内容</div></td>
            <td width="10%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">接收者</div></td>
            <td width="14%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">状态</div></td>
            <td width="10%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2">发送者</div></td>
          </tr>
          <s:iterator value="autoSendSmsList" var="c">
            <tr>
           <td height="18" bgcolor="#FFFFFF"><div align="center" class="STYLE1">
              <input name="checkbox" type="checkbox" class="STYLE2" value="checkbox" />
            </div></td>
            <td height="18" bgcolor="#FFFFFF" class="STYLE2"><div align="center" class="STYLE2 STYLE1">${c.id}</div></td>
            <td height="72" bgcolor="#FFFFFF"><div align="center" class="STYLE2 STYLE1">${c.customer}</div></td>
            <td height="18" bgcolor="#FFFFFF"><div align="center" class="STYLE2 STYLE1">${c.mobile}</div></td>
            <td height="18" bgcolor="#FFFFFF"><div align="center" class="STYLE2 STYLE1">${c.endTime}</div></td>
            <td height="18" bgcolor="#FFFFFF"><div align="center" class="STYLE2 STYLE1">${c.content}</div></td>
          </tr>
       </s:iterator>
        </table></td>
        <td width="9" background="login/images/tab_16.gif">&nbsp;</td>
      </tr>
    </table>
    </td>
  </tr>
</table>
	</body>
</html>
