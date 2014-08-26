<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>left</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
<style type="text/css">

body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
.STYLE2 {color: #43860c; font-size: 12px; }

a:link {font-size:12px; text-decoration:none; color:#43860c;}
a:visited {font-size:12px; text-decoration:none; color:#43860c;}
a:hover{font-size:12px; text-decoration:none; color:#FF0000;}
p.{font-size:14px; text-decoration:none; color:#43860c;}
img.{width:40px;height:40px;}

</style>


<script type="text/JavaScript">
<!--
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
<!-- 菜单 -->
<script type="text/javascript" src="js/jquery-1.3.1.min.js"></script>
<script type="text/javascript" src="js/iphone-springboard.js"></script>

  </head>
  
<body onload="MM_preloadImages('login/images/main_26_1.gif','login/images/main_29_1.gif','login/images/main_31_1.gif')">

<table width="178" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0" style="table-layout:fixed">
      <tr>
        <td height="26" background="login/images/main_21.gif">&nbsp;</td>
      </tr>
      <tr>
        <td height="80" style="background-image:url(login/images/main_23.gif); background-repeat:repeat-x;"><table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
          <tr>
            <td height="45"><div align="center"><a href="login/userlogin_selectall.action" target="I1"><img src="login/images/main_26.gif" name="Image1" width="40" height="40" border="0" id="Image1" onmouseover="MM_swapImage('Image1','','login/images/main_26_1.gif',1)" onmouseout="MM_swapImgRestore()" /></a></div></td>
            <td><div align="center"><a href="login/keyword_getSt.action" target="I1"><img src="login/images/main_28.gif" name="Image2" width="40" height="40" border="0" id="Image2" onmouseover="MM_swapImage('Image2','','login/images/main_29_1.gif',1)" onmouseout="MM_swapImgRestore()" /></a></div></td>
            <td><div align="center"><a href="login/smsStatistics.action" target="I1"><img src="login/images/main_31.gif" name="Image3" width="40" height="40" border="0" id="Image3" onmouseover="MM_swapImage('Image3','','login/images/main_31_1.gif',1)" onmouseout="MM_swapImgRestore()" /></a></div></td>
          </tr>
          <tr>
            <td height="25"><div align="center" class="STYLE2"><a href="login/userlogin_selectall.action" target="I1">系统管理</a></div></td>
            <td><div align="center" class="STYLE2"><a href="login/keyword_getlist.action" target="I1">关键字管理</a></div></td>
            <td><div align="center" class="STYLE2"><a href="login/smsStatistics.action?startime=20110409&endtime=20100412" target="I1">数据统计</a></div></td>
          </tr>
        </table></td>
      </tr>
      <tr>
        <td  style="line-height:4px; background:url(login/images/main_38.gif)">&nbsp;</td>
      </tr>
      <tr>
        <td style="background-image:url(login/images/main_23.gif); background-repeat:repeat-x;">
         <center>
        <div>
		    <div class="icon"> <a href="login/userlogin_selectall.action" target="I1" title="user" class="ico_btn"><img src="login/images/icon_appstore.png" alt="user" /></a>
		        <p class="ico_txt">用户管理</p>
		      </div>
		      <div class="icon upleft"> <a href="login/addsms.jsp" target="I1" title="Text" class="ico_btn"><img src="login/images/icon_text.png" alt="Text" /></a>
		        <p class="ico_txt">自定义短信</p>
		      </div>
		      <div class="icon upleft"><a href="login/smsbackluser.action" target="I1"title="YouTube" class="ico_btn"><img src="login/images/icon_youtube.png" alt="YouTube" /></a>
		        <p class="ico_txt">黑名单</p>
		      </div>
		      <div class="icon upleft"> <a href="login/smsStatistics.action" target="I1" title="Yahoo! Finance" class="ico_btn"><img src="login/images/icon_stocks.png" alt="Yahoo! Finance" /></a>
		        <p class="ico_txt">短信统计</p>
		      </div>
		
		      <div class="icon downleft"> <a href="login/keyword_getlist.action" target="I1" title="YouTube" class="ico_btn"><img src="login/images/icon_ipod.png" alt="YouTube" /></a>
		        <p class="ico_txt">关键字管理</p>
		      </div>
		      <!-- <div class="icon downleft"> <a href="http://finance.yahoo.com/" title="Yahoo! Finance" class="ico_btn"><img src="login/images/icon_stocks.png" alt="Yahoo! Finance" /></a>
		        <p class="ico_txt">Stocks</p>
		      </div>  -->
		    </div>
		    </center>
        </td>
      </tr>
    </table></td>
  </tr>
</table>
</body>
</html>
