var buttonGroup = angular.module('buttonGroup', []);
buttonGroup
		.directive(
				'buttonGroup',
				function() {
					return {
						template : '<p>'
								+ '<an-buttons ng-repeat="button in data"></an-buttons>'
								+ '</p>',
						replace : true,
						restrict : 'E',
						scope : false,
						transclude : true,
						controller : function($scope, $element, $attrs) {
							$scope.data = $scope.$eval($attrs.model).buttonGroup;
						}
					};
				})
		.directive(
				'anButtons',
				function() {
					return {
						template : '<div class="vdi-btn-group" >'
								+ '<button type="button" id={{button.id}} style="width:120px;" class="btn btn-lg vdi-btn-default" ng-click="button.click()" ><span class={{button.cls}}></span>  {{button.val}}</button>'
								+ '</div>',
						replace : true,
						restrict : 'E',
						scope : false,
						transclude : true
					};
				});
