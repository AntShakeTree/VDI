<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="rootApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Virtual Desktop Infrastructure</title>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>lib/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>css/reset.css" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>css/style.css" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>css/invalid.css" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>css/main.css" />
</head>
<body ng-controller="rootCtrl">
	<div id="sidebar" class="outer-west" ng-controller="rootCtrl.frameCtrl">
		<div id="sidebar-wrapper">
			<h1 id="sidebar-title">
				<a href="#">Simpla Admin</a>
			</h1>
			<a href="#"><img id="logo" src="images/logo.png"
				alt="Simpla Admin logo" /></a>
			<div id="profile-links">
				Hello, <a href="#" title="Edit your profile">865171</a>, you have <a
					href="#messages" rel="modal" title="3 Messages">3 Messages</a><br />
				<br /> <a href="#" title="View the Site">View the Site</a> | <a
					href="#" title="Sign Out">Sign Out</a>
			</div>
			<frame.menu id="main-nav"></frame.menu>
		</div>
	</div>
	<div class="outer-center">
		<div ng-view></div>
	</div>
	<script type="text/javascript" src="lib/jquery/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="lib/angular/angular.js"></script>
	<script type="text/javascript" src="lib/jquery/jquery-ui-1.9.2.custom.js"></script>
	<script type="text/javascript" src="lib/jquery/jquery.layout-latest.js"></script>
	<script type="text/javascript" src="lib/bootstrap/js/bootstrap.js"></script>
	<script type="text/javascript" src="lib/bootstrap/ui-bootstrap-tpls-0.11.0.js"></script>
	<script type="text/javascript" src="lib/angular/angular-animate.js"></script>
	<script type="text/javascript" src="lib/angular/angular-route.js"></script>
	<script type="text/javascript" src="lib/analytics.js"></script>
	<script type="text/javascript" src="lib/d3/d3.v3.min.js"></script>
	<script type="text/javascript" src="js/lang/lang_zh_CN.js"></script>
    <script type="text/javascript" src="js/plugins/constant.js"></script>
	<script type="text/javascript" src="js/app.js"></script>
	<script type="text/javascript" src="js/plugins/anTable.js"></script>
	<script type="text/javascript" src="js/plugins/anButton.js"></script>
	<script type="text/javascript" src="js/plugins/menuData.js"></script>
	<script type="text/javascript" src="js/frame.js"></script>
</body>
</html>