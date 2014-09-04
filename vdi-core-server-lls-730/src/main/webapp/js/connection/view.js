/**
 * 
 */
rootApp.controller('connection.ctrl', function($scope) {
    var add = function() {
//        var modalConfig = {
//                type : "dialog",
//                title : "Create",
//                template : "view/user/create.html",
//                width : "600px",
//                buttons : {
//                	ok : function() {},
//                	cancel : function() {}
//                }
//            };
//        $scope.root.createModal(modalConfig);
    };
    var edit = function() {
        return null;
    };
    var disconnect = function() {
        return null;
    };
    // 数据模型
    $scope.connectionmodel = {
        // 按钮组
        buttonGroup : [{'id' : 'disconnect', 'cls' : 'button-del', 'val' : 'Disconnect', 'click' : disconnect}]
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
        "groups": []
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
                    field : 'strategyname',
                    displayName : 'Group',
                    colWidth : '',
     				renderer : function(v){
     					var res = "";
     					$.each(v, function(idx, g){
     						if(res == ""){
     							res = g.groupname;
     						}else{
     							res = res + "," + g.groupname;
     						}
     					});
     					return res;
     				}
            	}]
        }
});