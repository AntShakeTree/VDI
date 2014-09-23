/**
 * 
 */
rootApp.controller('user.ctrl', function($scope) {
    var add = function() {
        var modalConfig = {
                type : "dialog",
                title : "Create",
                template : "view/user/create.html",
                width : "600px",
                buttons : {
                	ok : function() {},
                	cancel : function() {}
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
    $scope.usermodel = {
            // 按钮组
            buttonGroup : [{'id' : 'add', 'cls' : 'glyphicon glyphicon-plus', 'val' : 'Create', 'click' : add},
                            {'id' : 'edit', 'cls' : 'glyphicon glyphicon-pencil', 'val' : 'Edit', 'click' : edit},
                            {'id' : 'delete', 'cls' : 'glyphicon glyphicon-trash', 'val' : 'Delete', 'click' : del}]
    };
    // 桌面池列表假数据
    $scope.tableData = [{
        "iduser": 0,
        "idcardtype": "idcard",
        "idcard": "111111111111111111",
        "usertype": 0,
        "domainid": 0,
        "rootadmin": 1,
        "deleted": 0,
        "organizationid": 0,
        "username": "admin",
        "domainname": "",
        "email": "admin@opzoon.com",
        "address": "address",
        "realname": "Admin",
        "notes": "notes",
        "telephone": "13999999999",
        "groups": [],
        "domain": 1,
        "ou": 1,
    }
];
    $scope.demo = {
    	    pageNo : 5,
    	    pageSize : 10,
    	    pageAmount : 20
    	};
    $scope.tableOptions = {
            data : 'tableData',
            method : 'POST',
            page : 'tablePage',
            pageNo : 1,
            pageSize : 10,
            sortKey : 'iduser',
            ascend : 1,
            needSelect : true,
            columns :
                [{
                    field : 'username',
                    displayName : 'User Name',
                    colWidth : ''
                }, {
                    field : 'realname',
                    displayName : 'Name',
                    colWidth : ''
                }, {
                    field : 'email',
                    displayName : 'Email',
                    colWidth : ''
                }, {
                    field : 'rootadmin',
                    displayName : 'Role',
                    colWidth : '',
     				render : function(v){
     					return v == 1 ? "Administrator" :"User";
     				}
                }, {
                    field : 'domain',
                    displayName : 'Domain',
                    colWidth : '',
                    render : function(v){
     					return v == 1 ? "Local domain" :"";
     				}
            	}, {
                    field : 'ou',
                    displayName : 'OU',
                    colWidth : '',
                    render : function(v){
     					return v == 1 ? "Local OU" :"";
     				}
            	}, {
                    field : 'notes',
                    displayName : 'Remark',
                    colWidth : ''
            	}]
        }
});