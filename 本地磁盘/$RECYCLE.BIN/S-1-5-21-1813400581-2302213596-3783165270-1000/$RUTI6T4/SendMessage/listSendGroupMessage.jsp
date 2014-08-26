<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<%@ include file="../sendMessageBar.jsp"%>
<%@ include file="../title.jsp"%>

<script type="text/javascript">

function del()
{
	if(!confirm("你真的想删除么？"))
	{
		return false;
	}
}

</script>

<table width="97%" align="center">
	<tr>
		<td>
			<input type="button" value="短信群发"
				onclick="javascript:window.location.href='sendGroupSMSView.action'">
		</td>
	</tr>
</table>

<table cellpadding="3" cellspacing="1" border="0" align="center"
	class="table" width="97%">
	<tr class="tr">
		<td align="center" bgcolor="#78A1E6" nowrap="nowrap">
			序号
		</td>
		<td align="center" bgcolor="#78A1E6" nowrap="nowrap">
			姓名
		</td>
		<td align="center" bgcolor="#78A1E6" nowrap="nowrap">
			电话号
		</td>
		<td width="" align="center" bgcolor="#78A1E6" nowrap="nowrap">
			操作
		</td>
	</tr>
	<s:iterator value="pageView.records" id="u" status="st">
		<tr class="tr">
			<td align="center" bgcolor="#E6ECF9">
				<s:property value="#st.count" />
			</td>
			<td align="center" bgcolor="#E6ECF9">
				<s:property value="name" />

			</td>
			<td align="center" bgcolor="#E6ECF9">
				<s:property value="mobile" />
			</td>
			<td align="center" bgcolor="#E6ECF9">
				<s:a
					href="updatePDocumentCatalog.action?id=%{#u.id}&parentId=%{parentId}">更新</s:a>
				&nbsp;|&nbsp;
				<s:a
					href="deleteDocumentCatalog.action?id=%{#u.id}&type=%{#u.type}&parentId=%{parentId}&order=%{#u.order}"
					onclick="return del()">
			删除</s:a>
			</td>
		</tr>
		
<p class="f">
    当前页:第${pageView.currentpage}页 | 总记录数:${pageView.totalrecord}条 | 每页显示:${pageView.maxresult}条 | 总页数:${pageView.totalpage}页　</p>
<c:forEach begin="${pageView.pageindex.startindex}" end="${pageView.pageindex.endindex}" var="wp">
    <c:if test="${pageView.currentpage==wp}"><b style="color: red;">第${wp}页</b></c:if>
    <c:if test="${pageView.currentpage!=wp}"><a href="javascript:topage('${wp}')" class="a03">第${wp}页</a></c:if>

</c:forEach>
	</s:iterator>
</table>

<table width="97%" align="center">
	<tr>
		<td align="right">

		</td>
	</tr>

</table>

<%@ include file="../footer.jsp"%>
