/**
 * Created by Administrator on 14-7-25.
 */
var rootApp = angular.module('rootApp', ['ngAnimate', 'ngRoute', 'buttonGroup', 'ui.bootstrap', 'ngTouch']);
rootApp.controller('rootCtrl', function($scope, $modal, $log) {
    $scope.root = {
        createModal : function(modalConfig) {
            var modalInstance = $modal.open({
                templateUrl : modalConfig.templateUrl,
                controller : function($scope, $modalInstance) {
                    $scope.ok = function() {
                        $modalInstance.close();
                    };
                    $scope.cancel = function() {
                        $modalInstance.dismiss();
                    };
                },
                size : modalConfig.size,
                width : modalConfig.width
            });
            modalInstance.result.then(function() {

            }, function() {

            });
        }
    };
});