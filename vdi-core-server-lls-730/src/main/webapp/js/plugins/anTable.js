/**
 * Created by Administrator on 14-8-14.
 */
angular.module('anTable', [])
    .directive('antable', function(anDataService) {
        return {
            template : '<table>'
            +               '<thead anhead></thead>'
            +               '<tbody anbody></tbody>'
            +               '<tfoot anfoot></tfoot>'
            +           '</table>',
            replace : true,
            restrict : 'E',
            scope : false,
            controller : function($scope, $element, $attrs) {
                // 表格的默认配置
                var defaults = {
                    pageNo : 1,
                    pageSizes : [25, 50, 100],
                    sortKey : "id",
                    ascend : 1,
                    needSelect : true
                };
                // 表格的自定义配置与默认配置结合
                var options = this.tableOptions = angular.extend({}, defaults, $scope.$eval($attrs.options));
                // 向后台请求数据
                $scope.tableData =  $scope.$eval(options.data);
                $scope.tablePage =  $scope.$eval(options.page);
                /*anDataService.requestData({
                    method : options.method,
                    url : options.url,
                    params : angular.extend({}, options.params, {
                        pageNo : options.pageNo,
                        pageSize : options.pageSizes[0],
                        sortKey : options.sortKey,
                        ascend : options.ascend
                    })
                }).success(function(data) {
                	$scope.tableData = data.list;
                	$scope.tablePage = data.page;
                });*/
            }
        };
    })
    .directive('anhead', function() {
        return {
            template : '<tr>'
            +               '<th>'
            +                   '<input type="checkbox" class="check-all" ng-model="anhead.selected" ng-change="anhead.checkboxChange()" />'
            +               '</th>'
            +               '<th ng-repeat="title in anhead.titles">{{title.displayName}}</th>'
            +           '</tr>',
            replace : false,
            restrict : 'A',
            scope : false,
            require : '^antable',
            link : function(scope, element, attrs, antableCtrl) {
                scope.anhead = {
                    titles : antableCtrl.tableOptions.columns,
                    selected : false,
                    checkboxChange : function() {
                        scope.$broadcast('allSelected', this.selected);
                    }
                };
            }
        };
    })
    .directive('anbody', function() {
        return {
            template : '<tr ng-repeat="row in tableData">'
            +               '<td>'
            +                   '<input type="checkbox" ng-init="row.selected=false" ng-model="row.selected" />'
            +               '</td>'
            +               '<td ng-repeat="column in anbody.columns">{{row[column.field]}}</td>'
            +           '</tr>',
            replace : false,
            restrict : 'A',
            scope : false,
            require : '^antable',
            link : function(scope, element, attrs, antableCtrl) {
                scope.anbody = {
                    columns : antableCtrl.tableOptions.columns
                };
                scope.$on('allSelected', function(event, selected) {
                    angular.forEach(scope.tableData, function(row, index) {
                        row.selected = selected;
                    });
                });
            }
        };
    })
    .directive('anfoot', function(anDataService) {
        return {
            template : '<tr>'
            +               '<td colspan="12">'
            +                   '<div class="pagination">'
            +                       '共有{{tablePage.pageAmount}}页, 每页 '
            +                       '<select ng-model="anfoot.pageSize" ng-options="size for size in anfoot.sizes" ng-change="anfoot.selectChange(anfoot.pageSize)"></select>'
            +                       '<a href="javascript:void(0)" ng-click="anfoot.firstPage()">«第一页</a>'
            +                       '<a href="javascript:void(0)" ng-click="anfoot.prevPage()">«上一页</a>'
            +                       '<a href="javascript:void(0)" ng-repeat="no in anfoot.showNos" ng-click="anfoot.turnPage(no)" ng-class="{number : true, current : no ==  tablePage.pageNo}">{{no}}</a>'
            +                       '<a href="javascript:void(0)" ng-click="anfoot.nextPage()">下一页 »</a>'
            +                       '<a href="javascript:void(0)" ng-click="anfoot.lastPage()">最后一页 »</a>'
            +                   '</div>'
            +                   '<div class="clear"></div>'
            +               '</td>'
            +           '</tr>',
            replace : false,
            restrict : 'A',
            scope : false,
            require : '^antable',
            link : function(scope, element, attrs, antableCtrl) {
                scope.anfoot = {
                    sizes : antableCtrl.tableOptions.pageSizes,
                    pageSize : antableCtrl.tableOptions.pageSizes[0],
                    showNos : [],
                    turnPage : function(no) {
                        if(no == scope.tablePage.pageNo) {
                            return;
                        }
                        anDataService.requestData({
                        	params : {
                                pageNo : no
                            }
                        });
                    },
                    firstPage : function() {
                        this.turnPage(1);
                    },
                    lastPage : function() {
                        this.turnPage(scope.tablePage.pageAmount);
                    },
                    nextPage : function() {
                        var no = scope.tablePage.pageNo;
                        var amount = scope.tablePage.pageAmount;
                        if(no + 1 <= amount) {
                            this.turnPage(no + 1);
                        }
                    },
                    prevPage : function() {
                        var no = scope.tablePage.pageNo;
                        if(no - 1 > 0) {
                            this.turnPage(no - 1);
                        }
                    },
                    selectChange : function(size) {
                    	anDataService.requestData({
                    		params : {
                                pageSize : size
                            }
                        });
                    }
                };
                // 每次分页信息改变的时候，都要查看一下
                scope.$watch("tablePage", function() {
                	if(!scope.tablePage) {
                		return;
                	}
                    var showNos = scope.anfoot.showNos;
                    var pageNo = scope.tablePage.pageNo;
                    var pageAmount = scope.tablePage.pageAmount;
                    if(showNos.length == 0) {
                        if(pageAmount <= 4) {
                            scope.anfoot.showNos = newArray(1, pageAmount);
                        }else if(pageNo == 1) {
                            scope.anfoot.showNos = newArray(1, 4);
                        }else if(pageNo == pageAmount) {
                            scope.anfoot.showNos = newArray(pageAmount - 3, pageAmount);
                        }else if(pageNo - 1 > 0 && pageNo + 2 <= pageAmount) {
                            scope.anfoot.showNos = newArray(pageNo - 1, pageNo + 2);
                        }else {
                            scope.anfoot.showNos = newArray(pageNo - 2, pageNo + 1);
                        }
                    }else {
                        if(showNos.indexOf(pageNo) == 0) {
                            if(pageNo - 1 > 0) {
                                scope.anfoot.showNos = newArray(pageNo - 1, pageNo + 2);
                            }
                        }else if(showNos.indexOf(pageNo) == 3) {
                            if(pageNo + 1 <= pageAmount) {
                                scope.anfoot.showNos = newArray(pageNo - 2, pageNo + 1);
                            }
                        }
                    }
                });
                //
                var newArray = function(start, end) {
                    if(start > end) {
                        return;
                    }
                    var array = [];
                    for(var i = start; i <= end; i++) {
                        array.push(i);
                    }
                    return array;
                };
            }
        };
    })
    /**
     * 用来请求数据的服务
     */
    .factory('anDataService', function($http) {
        return function() {
        	// 缓存上次的请求信息
            var request = {};
            return {
                // req包括：url, params, pageno, pagesize,
                requestData : function(req) {
                    if(req) {
                        angular.extend(request, req);
                    }
                    return $http(request);
                }
            };
        }();
    });