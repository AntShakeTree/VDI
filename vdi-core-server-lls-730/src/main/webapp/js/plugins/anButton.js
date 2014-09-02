var buttonGroup = angular.module('buttonGroup', []);
    buttonGroup.directive('buttonGroup', function() {
        return {
            template : '<p>'
            +				'<an-buttons ng-repeat="button in data"></an-buttons>'
            +			'</p>',
            replace : true,
            restrict : 'E',
            scope : false,
            transclude : true,
            controller : function($scope, $element, $attrs)
            {
            	$scope.data = $scope.$eval($attrs.model).buttonGroup;
            }
        };
    })
    .directive('anButtons', function() {
        return {
        	template : '<div class="vdi-btn-group" >'
        		+ '<button type="button" id={{button.id}} class="btn {{button.cls}} btn-lg btn-block vdi-btn-default" ng-click="button.click()" >{{button.val}}</button>'
        		+ '</div>',
            replace : true,
            restrict : 'E',
            scope : false,
            transclude : true
        };
    });
    
    