<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ include file="../sendMessageBar.jsp"%>
<script type="text/javascript" src="../js/validate.js"></script>
<script type="text/javascript" src="../js/newPage.js"></script>

<script type="text/javascript">

function addMore(){
	newPage("addMoreSMS.jsp");
}



function validate()
{

	
	return true;
}


</script>

<%@ include file="../title.jsp"%>

<div align="center" style="width: 90%">
	<s:actionerror cssStyle="color: red" />
</div>
	
<s:form name="submitInfo" onsubmit="return validate()"
	action="addSendGroupSMS.action" theme="simple">

	<table cellpadding="3" cellspacing="1" border="0" align="center"
		class="table" width="90%">
	<tr>
	<td colspan="2">
		<input type="button" value="添加更多" onclick="addMore()">
	</td>
	</tr>
		<tr >
			<td >
			选择用户：<s:checkboxlist  list="list"   listKey="id" listValue="name" name="setSMS" >
				
					</s:checkboxlist>
			</td>
		</tr>
		<tr>
			<td >
			发送内容：<s:textarea cols="50" rows="4" id="setCustomer" name="content"></s:textarea>
			</td>
		</tr>
		<tr class="tr">
			<td>
				<input type="submit" value="提交">
		
				<input type="reset" value="重置">
			</td>
		</tr>
	</table>

</s:form>

<%@ include file="../footer.jsp"%>