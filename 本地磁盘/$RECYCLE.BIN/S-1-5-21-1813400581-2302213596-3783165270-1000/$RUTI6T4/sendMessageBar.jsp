<%@ page import="java.util.*,java.util.regex.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html> 
<head>
<Meta http-equiv="progma" content="no-cache">
<title>浪曦OA与工作流系统</title>
<meta http-equiv="content-type" content="text/html; charset=GBK">
<%@ include file="css/css_all.css" %>
</head>

<body bgcolor="#ffffff" style="margin:0px" text="#000000" link="#0000ff" vlink="#0000ff" alink="#6699cc">

<table width="100%" height="100%" cellpadding="0" cellspacing="0">
<tr>
	<td height="77" colspan="2">
		<%@ include file="header.jsp" %>
	</td>
</tr>
<tr>
<td width="157" valign="top">

<table width="100%" align="center" cellpadding="0" cellspacing="0">
<tr><td>

	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<font size="-1" color="#003399"><b>目录列表</b></font>
	
	<br>


	<div align="left" width="141">
		<table cellpadding="0" cellspacing="0" border="0" width="141" >

			<tr>
				<td>
					<font >
						<a href="<%=request.getContextPath()%>/SendMessage/">
						&nbsp;&nbsp;节日短信群发
						</a>
				   </font>
				</td>
			</tr>
				
				
			<tr>
				<td>
					<font size="-1">
						<a href="/DocumentCataLog/listDocumentCataLog.do?start=0&range=10&type=3&parentId=-1">
							&nbsp;&nbsp;部门文档
						</a></font>
				</td>
			</tr>
				
				
			<tr>
				<td>
					<font size="-1">
						<a href="/DocumentCataLog/listDocumentCataLog.do?start=0&range=10&type=5&parentId=-1">
							&nbsp;&nbsp;协作文档
						</a></font>
				</td>
			</tr>	
				
				
			<tr>
				<td>
					<font size="-1">
						<a href="/DocumentCataLog/listDocumentCataLog.do?start=0&range=10&type=2&parentId=-1">
							&nbsp;&nbsp;共享文档
						</a></font>
						
				</td>
			</tr>
			
			<tr>
				<td>
					<font size="-1">
						<a href="/DocumentCataLog/listDocumentCataLog.do?start=0&range=10&type=4&parentId=-1">
							&nbsp;&nbsp;培训文档
						</a></font>
				</td>
			</tr>

			
					
			<tr>
				<td><hr width="80%" align="left">
					<font size="-1"> 
						<a href="/DocumentItem/documentCataLogGraph.jsp?type=1">
							&nbsp;&nbsp;公司文档 图
						</a></font>
				</td>
			</tr>
			
			<tr>
				<td>
					<font size="-1"> 
						<a href="/DocumentItem/documentCataLogGraph.jsp?type=2">
							&nbsp;&nbsp;部门文档 图
						</a></font>
				</td>
			</tr>
			<tr>
				<td>
					<font size="-1"> 
						<a href="/DocumentItem/documentCataLogGraph.jsp?type=3">
							&nbsp;&nbsp;协作文档 图
						</a></font>
				</td>
			</tr>
			<tr>
				<td>
					<font size="-1"> 
						<a href="/DocumentItem/documentCataLogGraph.jsp?type=4">
							&nbsp;&nbsp;共享文档 图
						</a></font>
				</td>
			</tr>
			<tr>
				<td>
					<font size="-1"> 
						<a href="/DocumentItem/documentCataLogGraph.jsp?type=5">
							&nbsp;&nbsp;培训文档 图
						</a></font>
				</td>
			</tr>
			
			<tr>
				<td>
				<hr width="80%" align="left">
					<font size="-1"> 
						<a href="/DocumentItem/searchDocumentItem.jsp">
							&nbsp;&nbsp;搜索
						</a></font>
				</td>
			</tr>
		
			
		</table>
	</div>
	<br>

	
	
</td></tr>
</table>
	
</td>

<td valign="top" width="90%">
