<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Virtual Desktop Infrastructure</title>
<%
    String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>">
<link type="text/css" rel="stylesheet" href="<%=basePath%>lib/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>css/main.css" />
<script type="text/javascript">
var basePath = '<%=basePath%>';
</script>
</head>
<body id="login">
	<div id="login-wrapper" class="png_bg">
		<div id="login-top">
			<h1>Virtual Desktop Infrastructure</h1>
		</div>
		<div id="login-content">
			<form class="form-horizontal" role="form" action="frame.jsp">
				<div class="form-group">
					<label for="inputEmail3" class="col-sm-2 control-label">Username</label>
					<div class="col-sm-4">
						<input type="text" class="form-control" id="inputEmail3" placeholder="Username" />
					</div>
				</div>
				<div class="form-group">
					<label for="inputPassword3" class="col-sm-2 control-label">Password</label>
					<div class="col-sm-4">
						<input type="password" class="form-control" id="inputPassword3" placeholder="Password" />
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<div class="checkbox">
							<label> <input type="checkbox"> Remember me
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<button type="submit" class="btn btn-default vdi-btn-default">Sign in</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>