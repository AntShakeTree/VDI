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
    function showw(id,name,pass,asta)
    {
    	alert(id+name+pass+asta)
        var div1=document.getElementById("Layer1");
        div1=document.all.Layer1.style.visibility='';
        document.getElementById("anameup").value=name;	
        document.getElementById("astaup").value=asta;	
        document.getElementById("apassup").value=pass;
        document.getElementById("aidup").value=id;
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
    function showwadd()
    {
        var div1=document.getElementById("addbody");
        div1=document.all.Layer1.style.visibility='';
		
    }
</script>
		<script language="javascript">

    function closewinadd()
    {
        var div1=document.getElementById("addbody");
        div1=document.all.Layer1.style.visibility='hidden';
    }
</script>
		<script language="javascript">
function deletuser(aid)
{
if(confirm("是否删除该用户，确认将彻底删除")==1)
{
	alert(aid);
	window.location.href="login/userlogin_delete.action?aid="+aid;
}

else {return false;}

}
</script>
	</head>

	<body>
		<div id="addbody"
			style="position: absolute; left: 200px; top: 50px; width: 400px; height: 300px; z-index:8; background-color: #CCFFFF; layer-background-color: #CCFFFF; border: 1px none #000000; visibility: hidden">
			<center>
				<h3>
					添加用户信息
				</h3>
				<br>
				<button onclick="closewinadd()">
					关 闭
				</button>
				<form action="login/userlogin_add.action">
					<table>
						<tr>
							<td>
								用户名：
							</td>
							<td colspan="2">
								<input type="TEXT" name="adminUser.aname">
							</td>
						</tr>
						<tr>
							<td>
								密码：
							</td>
							<td colspan="2">
								<input type="TEXT" name="adminUser.apass">
							</td>
						</tr>
						<tr>
							<td>
								状态：
							</td>
							<td colspan="2">
								<input type="TEXT" name="adminUser.asta">
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input type="submit" value="修改">
							</td>
							<td></td>
						</tr>
					</table>
				</form>
			</center>
		</div>

		<div id="Layer1"
			style="position: absolute; left: 200px; top: 50px; width: 400px; height: 300px; z-index: 1; background-color: #CCFFFF; layer-background-color: #CCFFFF; border: 1px none #000000; visibility: hidden">
			<center>
				<h3>
					用户修改
				</h3>
				<br>
				<button onclick=closewin()>
					关 闭
				</button>
				<form action="login/userlogin_update.action">
					<table>
						<tr>
							<td>
								编号：
							</td>
							<td colspan="2">
								<input type="TEXT" id="aidup" name="adminUser.aid">
							</td>
						</tr>
						<tr>
							<td>
								用户名：
							</td>
							<td colspan="2">
								<input type="TEXT" id="anameup" name="adminUser.aname">
							</td>
						</tr>
						<tr>
							<td>
								密码：
							</td>
							<td colspan="2">
								<input type="TEXT" id="apassup" name="adminUser.apass">
							</td>
						</tr>
						<tr>
							<td>
								状态：
							</td>
							<td colspan="2">
								<input type="TEXT" id="astaup" name="adminUser.asta">
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input type="submit" value="修改">
							</td>
							<td></td>
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
								<span class="STYLE4">服务器进程配置列表</span>
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
															<a onclick="showwadd()">新增</a>
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
														<div align="center">div messge
													</div>
													</td>
												</tr>
											</table>
										</td>
								</table>
							</td>
							<td width="14">
								<img src="images/tab_07.gif" width="14" height="30" />
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
										<td width="6%" height="26" background="images/tab_14.gif"
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
										<td width="24%" height="18" background="images/tab_14.gif"
											class="STYLE1">
											<div align="center" class="STYLE2">
												服务器进程配置
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
													${c.apass}
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center" class="STYLE2 STYLE1">
													${c.asta}
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center">
													<a href="#">服务器进程配置</a>
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center">
													<img src="images/037.gif" width="9" height="9" />
													<span class="STYLE1"> [</span><a onclick=showw("${c.aid}","${c.aname}","${c.apass}","${c.asta}")>编辑</a><span
														class="STYLE1">]</span>
												</div>
											</td>
											<td height="18" bgcolor="#FFFFFF">
												<div align="center">
													<span class="STYLE2"><img src="images/010.gif"
															width="9" height="9" /> </span><span class="STYLE1">[</span><a
														onClick="return deletuser('${c.aid}')">删除</a><span
														class="STYLE1">]</span>
												</div>
											</td>
										</tr>
									</s:iterator>
								</table>
							</td>
							<td width="9" background="images/tab_16.gif">
								&nbsp;
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

	</body>
</html>
