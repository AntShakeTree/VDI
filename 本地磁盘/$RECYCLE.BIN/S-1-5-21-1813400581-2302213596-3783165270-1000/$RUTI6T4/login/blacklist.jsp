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
<script>
function addaction()
{var user=document.getElementById("blackus").value;
	if(isNaN(user))
	  {alert("添加的对象有非法字符存在");}else{
        if(user.length==11){
        	window.location.href="login/addbackuser.action?blackuser="+user;
            }else{alert("添加的对象不是一个电话号码");}}
}
</script>
	</head>
	<body>
<%int x=13;  List<String> listy=(List<String>)request.getAttribute("blackList");int y=(listy.size()-1)/x+1;
  int ss=x*y-listy.size()-1;for(int b=0;b<=ss;b++){listy.add("  ");} %>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="15" height="30"><img src="login/images/tab_03.gif" width="15" height="30" /></td>
        <td width="2201" background="login/images/tab_05.gif"><img src="login/images/311.gif" width="16" height="16" /> <span class="STYLE4">服务器短信黑名单列表
        <input type="text" id="blackus" name="blackuser"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" onclick="addaction()" value="添加"/>
        </span></td>
        <td width="281" background="login/images/tab_05.gif"><table border="0" align="right" cellpadding="0" cellspacing="0">
            <tr>
              <td width="60"><table width="87%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="STYLE1"><div align="center">
                        &nbsp;
                    </div></td>
                    <td class="STYLE1"><div align="center"> &nbsp;</div></td>
                  </tr>
              </table></td>
              <td width="60"><table width="90%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="STYLE1"><div align="center"><img src="login/images/083.gif" width="14" height="14" /></div></td>
                    <td class="STYLE1"><div align="center" ><a href="login/getbackuser.action">删除</a></div></td>
                  </tr>
              </table></td>
              <td width="60"><table width="90%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="STYLE1"><div align="center"><img src="login/images/114.gif" width="14" height="14" /></div></td>
                    <td class="STYLE1"><div align="center"><a href="login/getbackuser.action">修改</a></div></td>
                  </tr>
              </table></td>
              <td width="52"><table width="88%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="STYLE1">&nbsp;</div></td>
                    <td class="STYLE1"><div align="center">&nbsp;</div></td>
                  </tr>
              </table></td>
            </tr>
        </table></td>
        <td width="14"><img src="login/images/tab_07.gif" width="14" height="30" /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td><table width="100%"  border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="9" background="login/images/tab_12.gif">&nbsp;</td>
        <td bgcolor="#f3ffe3"><table width="99%" height="98%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor="#c0de98" onmouseover="changeto()"  onmouseout="changeback()">
          <tr>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="7%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
            <td width="8%" height="18" background="images/tab_14.gif" class="STYLE1"><div align="center" class="STYLE2 STYLE1">名单</div></td>
          </tr>
          <% for(int i=0;i<y;i++){int a=x*i; %>
      	<tr>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE1">
				<%if(listy.get(a+0)!=null){out.println(listy.get(a+0));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF" class="STYLE2">
			<div align="center" class="STYLE2 STYLE1">
			<%if(listy.get(a+1)!=null){out.println(listy.get(a+1));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+2)!=null){out.println(listy.get(a+2));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+3)!=null){out.println(listy.get(a+3));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+4)!=null){out.println(listy.get(a+4));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+5)!=null){out.println(listy.get(a+5));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center">
				<%if(listy.get(a+6)!=null){out.println(listy.get(a+6));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center">
				<%if(listy.get(a+7)!=null){out.println(listy.get(a+7));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE1">
				<%if(listy.get(a+8)!=null){out.println(listy.get(a+8));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF" class="STYLE2">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+9)!=null){out.println(listy.get(a+9));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+10)!=null){out.println(listy.get(a+10));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+11)!=null){out.println(listy.get(a+11));}else{out.println(" ");} %>
			</div>
		</td>
		<td height="18" bgcolor="#FFFFFF">
			<div align="center" class="STYLE2 STYLE1">
				<%if(listy.get(a+12)!=null){out.println(listy.get(a+12));}else{out.println(" ");} %>
			</div>
		</td>
	    </tr>
          <%} %>
        </table></td>
        <td width="9" background="images/tab_16.gif">&nbsp;</td>
      </tr>
    </table>
    </td>
  </tr>
</table>
	</body>
</html>
