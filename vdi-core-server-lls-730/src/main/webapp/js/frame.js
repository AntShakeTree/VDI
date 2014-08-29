/**
 * Created by Administrator on 14-7-25.
 */
$(document).bind("selectstart", function(){return false;});
$(document).ready(function() {
    $('body').layout({
        west__paneSelector : ".outer-west",
        center__paneSelector : ".outer-center",
        west__size : 230
    });
});
rootApp.controller('rootCtrl.frameCtrl', function($scope) {
    $scope.frame = {
        menuData : menuData
    };
})
.directive('frame.menu', function() {
    return {
        template : '<ul>'
        +               '<li ng-repeat="main in frame.menuData">'
        +                   '<a href="javascript:void(0)" class="nav-top-item no-submenu" ng-click="menu.mainClick(main)" ng-class="{current : main.selected}" ng-init="main.selected = $first">{{main.title}}</a>'
        +                   '<ul ng-show="main.selected" class="animate animate-{{main.content.length}}">'
        +                       '<li ng-repeat="sub in main.content">'
        +                           '<a href="{{sub.url}}" ng-click="menu.subClick(sub)" ng-class="{current : sub.selected}" ng-init="sub.selected = $first&&main.selected">{{sub.title}}</a>'
        +                       '</li>'
        +                   '</ul>'
        +               '</li>'
        +           '</ul>',
        replace : true,
        restrict : 'E',
        scope : true,
        link : function(scope, element, attrs) {
            scope.menu = {
                mainClick : function(obj) {
                    angular.forEach(scope.frame.menuData, function(main, index) {
                        main.selected = false;
                    });
                    obj.selected = true;
                },
                subClick : function(obj) {
                    angular.forEach(scope.frame.menuData, function(main, index) {
                        if(main.content) {
                            angular.forEach(main.content, function(sub, index) {
                                sub.selected = false;
                            });
                        }
                    });
                    obj.selected = true;
                }
            };
        }
    };
})
.config(function($routeProvider) { //配置menu的跳转
    $routeProvider
        .when('/computingpool', {
            templateUrl: 'template/computingpool.html'
        })
        .when('/host', {
            templateUrl: 'template/host.html'
        })
        .when('/network', {
            templateUrl: 'template/network.html'
        })
        .when('/storage', {
            templateUrl: 'template/storage.html'
        })
        .when('/template', {
            templateUrl: 'template/template.html'
        })
        .otherwise({
            redirectTo: '#'
        });
});