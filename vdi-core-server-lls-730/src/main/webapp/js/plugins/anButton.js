var buttonGroup = angular.module('buttonGroup', []);
    buttonGroup.directive('buttonGroup', function() {
        return {
            template : '<ul class="shortcut-buttons-set">'
            +				'<an-buttons ng-repeat="button in data"></an-buttons>'
            +			'</ul>',
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
            template : '<li class="head-content">'
            +			'<a class="shortcut-button" id={{button.id}} ng-click="button.click()" href="javascript:void(0)">'
            +				'<span>'
            +					'<div class={{button.cls}} />'
            +						'<br />'
            +							'{{button.val}}'
            +				'</span>'
            +			'</a>'
            +			'</li>',
            replace : true,
            restrict : 'E',
            scope : false,
            transclude : true
        };
    });
    
    