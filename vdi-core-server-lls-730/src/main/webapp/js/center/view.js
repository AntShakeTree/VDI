rootApp.controller('center.ctrl', function($scope) {
	var add = function() {
		var modalConfig = {
			type : "dialog",
			title : "Create",
			template : "view/center/create.html",
			width : "600px",
			buttons : {
				ok : function() {
				},
				cancel : function() {
				}
			}
		};
		$scope.root.createModal(modalConfig);
	};
	var edit = function() {
		return null;
	};
	var del = function() {
		return null;
	};
	// 数据模型
	$scope.centermodel = {
		// 按钮组
		buttonGroup : [ {
			'id' : 'add',
			'cls' : 'button-add',
			'val' : 'Create',
			'click' : add
		}, {
			'id' : 'edit',
			'cls' : 'button-edit',
			'val' : 'Edit',
			'click' : edit
		}, {
			'id' : 'delete',
			'cls' : 'button-del',
			'val' : 'Delete',
			'click' : del
		} ]
	};
	$scope.tableData = [ {
		centername : "1",
		address : "20.2.2.1",
		status : 509
	} ];
	$scope.tablePage = {
		pageNo : 5,
		pageSize : 10,
		pageAmount : 20
	};
	$scope.tableOptions = {
		url:'http://localhost:8080/vdi-core-server-lls/services/listCenters',
		data : {},
		dataKey:'body.list',
		method : 'POST',
		page : 'tablePage',
		pageNo : 1,
		pageSize : 10,
		sortKey : 'idcenter',
		ascend : 1,
		needSelect : true,
		columns : [ {
			field : 'centername',
			displayName : 'Name',
			colWidth : ''
		},{
			field : 'address',
			displayName : 'Address',
			colWidth : ''
		} ]
	}
});