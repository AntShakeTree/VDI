<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.tibetone.ztkjhim.bean.AdminUser"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
.STYLE1 {
	color: #43860c;
	font-size: 12px;
}
-->
</style>
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
<table width="100%" border="0" cellspacing="0" cellpadding="0" style="table-layout:fixed;">
  <tr>
    <td height="9" style="line-height:9px; background-image:url(login/images/main_04.gif)"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="97" height="9" background="login/images/main_01.gif">&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="47" background="login/images/main_09.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="38" height="47" background="login/images/main_06.gif">&nbsp;</td>
        <td width="59"><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="29" background="login/images/main_07.gif">&nbsp;</td>
          </tr>
          <tr>
            <td height="18" background="login/images/main_14.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0" style="table-layout:fixed;">
              <tr>
                <td  style="width:1px;">&nbsp;</td>
                <td ><span class="STYLE1">${adminusername}</span></td>
              </tr>
            </table></td>
          </tr>
        </table></td>
        <td width="155" background="login/images/main_08.gif">&nbsp;</td>
        <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="23" valign="bottom">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="login/userlogin_exit.action" target="_top" > <img src="login/images/tc.jpg" width="59" height="22" border="0"" /></a></td>
          </tr>
        </table></td>
        <td width="200" background="login/images/*.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="11%" height="23">&nbsp;</td>
            <td width="89%" valign="bottom"><span class="STYLE1">&nbsp;</span></td>
          </tr>
        </table></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="5" style="line-height:5px; background-image:url(login/images/main_18.gif)"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="180" background="login/images/main_16.gif"  style="line-height:5px;">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table></td>
  </tr>
</table>
</body>
</html>
