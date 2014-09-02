/*
 * menu data 
 */
var menuData = [ {
	title : "Data Center",
	content : [ {
		title : "Computing Pool",
		url : "#/computingpool"
	}, {
		title : "Storage",
		url : "#/storage"
	}, {
		title : "Network",
		url : "#/network"
	} ]
}, {
	title : " Virtual Desktop",
	content : [ {
		title : "Connection",
		url : "#"
	}, {
		title : "Desktop Pool",
		url : "#"+STATIC_PAGE.DESKTOPPOOL.NAME
	}, {
		title : "Template(and VM)",
		url : "#"
	}, {
		title : "Policy",
		url : "#"
	} ]
}, {
	title : "User",
	content : [ {
		title : "User",
		url : "#"+STATIC_PAGE.USER.NAME
	}, {
		title : "Delivery Group",
		url : "#"
	}, {
		title : "LDAP Provider",
		url : "#"
	} ]
}, {
	title : "Infrastructure",
	content : [ {
		title : "Core(Cluster,Power)",
		url : "#"
	}, {
		title : "Gateway",
		url : "#"
	}, {
		title : "Center",
		url : '#' + STATIC_PAGE.CENTER.NAME
	}, {
		title : "Host",
		url : "#/host"
	}, {
		title : "Database",
		url : "#"
	} ]
}, {
	title : "Configuration",
	content : [ {
		title : "License",
		url : "#"
	}, {
		title : "Log",
		url : "#"
	} ]
} ];