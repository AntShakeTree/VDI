<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.tibetone.ztkjhim.bean.AdminUser"%>
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

.STYLE1 {
	font-size: 12px
}

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

.STYLE7 {
	font-size: 12
}
.btn {height:35;
BORDER-RIGHT: #7b9ebd 1px solid; PADDING-RIGHT: 2px; BORDER-TOP:
#7b9ebd 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER:
progid:DXImageTransform.Microsoft.Gradient(GradientType=0,
StartColorStr=#ffffff, EndColorStr=#cecfde); BORDER-LEFT: #7b9ebd
1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px;
BORDER-BOTTOM: #7b9ebd 1px solid
}
.btn1_mouseout {height:35;
BORDER-RIGHT: #7EBF4F 1px solid; PADDING-RIGHT: 2px; BORDER-TOP:
#7EBF4F 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER:
progid:DXImageTransform.Microsoft.Gradient(GradientType=0,
StartColorStr=#ffffff, EndColorStr=#B3D997); BORDER-LEFT: #7EBF4F
1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px;
BORDER-BOTTOM: #7EBF4F 1px solid
}
.btn_2k3 {height:35;
BORDER-RIGHT: #002D96 1px solid; PADDING-RIGHT: 2px; BORDER-TOP:
#002D96 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER:
progid:DXImageTransform.Microsoft.Gradient(GradientType=0,
StartColorStr=#FFFFFF, EndColorStr=#9DBCEA); BORDER-LEFT: #002D96
1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px;
BORDER-BOTTOM: #002D96 1px solid
}
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
    function showw(id,name,pass,asta,tt)
    {
        //DISABLED
        var div1=document.getElementById("Layer1");
        div1=document.all.Layer1.style.visibility='';
        document.getElementById("anameup").value=name;	
        document.getElementById("astaup").value=asta;	
        document.getElementById("apassup").value=pass;
        document.getElementById("aidup").value=id;
        document.getElementById("add").type.value=tt;    	   
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
function deletuser(aid)
{
if(confirm("是否删除该用户，确认将彻底删除")==1)
{
	window.location.href="login/userlogin_delete.action?aid="+aid;
}

else {return false;}

}
</script>



<script>
function regist()
{
	//获取页面的第一个表单
	targetForm = document.forms[0];
	//动态修改表单的action属性
	targetForm.action = "login/userlogin_add.action";
}
</script>
	</head>

	<body>
		<div id="Layer1" style="position: absolute; left: 350px;background-repeat:repeat-x;top: 200px; width: 400px; height: 200px; z-index: 1; background-color: #87CEFA; layer-background-color: #CCFFFF; border: 1px none #000000; visibility: hidden">
			<center><br>
				<h3>操作用户信息</h3>
				<form action="login/userlogin_update.action">
				<input type="hidden" id="aidup" name="adminUser.aid">
					<table >
						<tr>
							<td>用户名：</td>
							<td colspan="2">
								<input type="TEXT" id="anameup" name="adminUser.aname">
							</td>
							<td>
								备注
							</td>
						</tr>
						<tr>
							<td>
								密&nbsp;&nbsp;&nbsp;&nbsp;码：
							</td>
							<td colspan="2">
								<input type="TEXT" id="apassup" name="adminUser.apass">
							</td>
							<td>
								备注
							</td>
						</tr>
						<tr>
							<td>状&nbsp;&nbsp;&nbsp;&nbsp;态：</td>
							<td colspan="2">
								<input type="TEXT" id="astaup" name="adminUser.asta">
							</td>
							<td>
								备注
							</td>
						</tr>
						<tr>
							<td></td>
							<td><br>
						&nbsp;&nbsp;&nbsp;&nbsp;<input class=btn type="submit" id="smbtn" value="添  加" onClick="regist();"/>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input class=btn1_mouseout id="update" type="submit" value="修  改">
							</td>
							<td></td>
							<td>
					
							</td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td align="right" width="120"><button class=btn_2k3 onclick=closewin()>关 闭</button></td>
						</tr>
					</table>
					
				</form>
			</center>
		</div>
		<table width="100%" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr>
				<td height="30">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="15" height="30">
								<img src="login/images/tab_03.gif" width="15" height="30" />
							</td>
							<td width="1101" background="login/images/tab_05.gif">
								<img src="login/images/311.gif" width="16" height="16" />
								<span class="STYLE4">服务器用户列表</span>
							</td>
							<td width="281" background="login/images/tab_05.gif">
								<table border="0" align="right" cellpadding="0" cellspacing="0">
									<tr>
										<td width="60">
											<table width="90%" border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td class="STYLE1">
														<div align="center">
															<img src="login/images/001.gif" width="14" height="14" />
														</div>
													</td>
													<td class="STYLE1">
														<div align="center">
															<a onclick=showw("","","","","添加用户")>新增</a>
														</div>
													</td>
												</tr>
											</table>
										</td>
										<td width="112">
											<table width="90%" border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td class="STYLE1">
														<div align="center">
															<img src="login/images/114.gif" width="14" height="14" />
														</div>
													</td>
													<td class="STYLE1">
														<div align="center">
														<% AdminUser user=(AdminUser)request.getSession().getAttribute("sessionuser");
														%>
															<a onclick=showw("<%=user.getAid() %>","<%=user.getAname() %>","<%=user.getApass() %>","<%=user.getAsta()%>")>修改个人资料</a>
														</div>
													</td>
												</tr>
											</table>
										</td>
								</table>
							</td>
							<td width="14">
								<img src="login/images/tab_07.gif" width="14" height="30" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="9" background="images/tab_12.gif">
								&nbsp;
							</td>
							<td bgcolor="#f3ffe3">
								<table width="99%" border="0" align="center" cellpadding="0"
									cellspacing="1" bgcolor="#c0de98" onmouseover="changeto()"
									onmouseout="changeback()">
									<tr>
										<td width="6%" height="26" background="login/images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2 STYLE1">
												选择
											</div>
										</td>
										<td width="8%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2 STYLE1">
												编号
											</div>
										</td>
										<td width="24%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2 STYLE1">
												用户名
											</div>
										</td>
										<td width="10%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2 STYLE1">
												用户密码
											</div>
										</td>
										<td width="14%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2 STYLE1">
												用户状态
											</div>
										</td>
										<td width="7%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2">
												编辑
											</div>
										</td>
										<td width="7%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2">
												删除
											</div>
										</td>
									</tr>
									<s:iterator value="userList" var="c">
										<tr>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center" class="STYLE1">
													<input name="checkbox" type="checkbox" class="STYLE2"
														value="checkbox" />
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF" class="STYLE2">
												<div align="center" class="STYLE2 STYLE1">
													${c.aid}
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center" class="STYLE2 STYLE1">
													${c.aname}
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center" class="STYLE2 STYLE1">
													****
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center" class="STYLE2 STYLE1">
													${c.asta}
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center">
													<img src="<%=basePath %>/login/images/037.gif" width="9" height="9" />
													<span class="STYLE1"> [</span><a onclick=showw("${c.aid}","${c.aname}","${c.apass}","${c.asta}","修改用户")>编辑</a><span
														class="STYLE1">]</span>
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center">
													<span class="STYLE2"><img src="<%=basePath %>/login/images/010.gif"
															width="9" height="9" /> </span><span class="STYLE1">[</span><a
														onClick="return deletuser('${c.aid}')">删除</a><span
														class="STYLE1">]</span>
												</div>
											</td>
										</tr>
									</s:iterator>
								</table>
							</td>
							<td width="9" background="login/images/tab_16.gif">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

	</body>
</html>
