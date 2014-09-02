/**
 * 
 */
rootApp.controller('desktoppool.ctrl', function($scope) {
    var add = function() {
        var modalConfig = {
                type : "dialog",
                title : "Create",
                template : "view/desktoppool/create.html",
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
    $scope.desktoppoolmodel = {
        // 按钮组
        buttonGroup : [{'id' : 'add', 'cls' : 'button-add', 'val' : '创建', 'click' : add},
                        {'id' : 'edit', 'cls' : 'button-edit', 'val' : '修改', 'click' : edit},
                        {'id' : 'delete', 'cls' : 'button-del', 'val' : '删除', 'click' : del}]
    };
    // 桌面池列表假数据
    $scope.tableData = [{
        "strategyid": 1,
        "connecteddesktops": 0,
        "maxdesktops": 1,
        "abnormaldesktops": 0,
        "templatename": "mb12_Win732_iscsi_vl36",
        "sparingdesktops": 0,
        "status": 0,
        "unassignmentdelay": 0,
        "templateid": "/opzoon/administrator/74ee0ffa-6a33-4feb-9ec0-1332060c3bb6",
        "domainid": 0,
        "poolname": "test",
        "strategyname": "allow_all",
        "sparedesktops": 0,
        "vmsource": 0,
        "assignment": 1,
        "cloudmanagerid": 2,
        "cloudname": "V03",
        "availableprotocols": 819,
        "iddesktoppool": 11,
        "notes": "",
        "vmnamepatterrn": "test",
        "computernamepattern": "test"
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
            sortKey : 'iddesktoppool',
            ascend : 1,
            needSelect : true,
            columns :
                [{
                    field : 'computename',
                    displayName : 'Desktop Pool Name',
                    colWidth : ''
                }, {
                    field : 'status',
                    displayName : 'Status',
                    colWidth : '',
                    render:function(v){
                    	var res=null;
                    	switch(v){
                    		case 1:res="正常使用"; break;
                    		case 3:res="主机添加中";break;
                    		case 4:res="主机移除中";break;
                    		case 501:res="创建中";break;
                    		case 502:res="删除中";break;
                    		case 505:res="卸载中";break;
                    		case 506:res="装载中";break;
                    		case 509:res="恢复中";break;
                    	}
                    	return res;
                    }
                }, {
                    field : 'cpuamount',
                    displayName : 'Total CPU',
                    colWidth : ''
                }, {
                    field : 'cpurest',
                    displayName : 'Remaining CPU',
                    colWidth : ''
                }, {
                    field : 'memoryamount',
                    displayName : 'Total memory',
                    colWidth : ''
                }, {
                    field : 'memoryrest',
                    displayName : 'Remaining memory',
                    colWidth : ''
                }, {
                    field : 'createtime',
                    displayName : 'Create time',
                    colWidth : ''
                }, {
                    field : 'hosts',
                    displayName : 'Host',
                    colWidth : '',
                    render:function(r){
                    	if(r && 0 < r.length){
                    		return r.length;
                    	}else{
                    		return "No host";
                    	}
                    }
                }, {
                    field : 'note',
                    displayName : 'Remark',
                    colWidth : ''
                }]
        }
});