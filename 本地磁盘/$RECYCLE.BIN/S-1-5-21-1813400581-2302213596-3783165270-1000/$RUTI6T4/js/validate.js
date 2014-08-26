function isNotEmpty(element,message){
	if(0==element.value.length){
		alert(message);
		element.focus();
		return false;
	}
	return true;
}