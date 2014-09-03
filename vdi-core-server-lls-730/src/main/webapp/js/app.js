/**
 * Created by Administrator on 14-7-25.
 */
var rootApp = angular.module('rootApp', [ 'ngAnimate', 'ngRoute',
		'buttonGroup', 'ui.bootstrap', 'anTable' ]);
rootApp.config(
		function($controllerProvider, $compileProvider, $provide,
				$filterProvider, $routeProvider) {
			rootApp._controller = rootApp.controller;
			rootApp._directive = rootApp.directive;
			rootApp._factory = rootApp.factory;
			rootApp._filter = rootApp.filter;

			rootApp.controller = function(name, constructor) {
				$controllerProvider.register(name, constructor);
				return this;
			};
			rootApp.directive = function(name, factory) {
				$compileProvider.directive(name, factory);
				return this;
			};
			rootApp.factory = function(name, factory) {
				$provide.factory(name, factory);
				return this;
			};
			rootApp.filter = function(name, factory) {
				$filterProvider.register(name, factory);
				return this;
			};
			$routeProvider.when(STATIC_PAGE.COMPUTINGPOOL.NAME, {
				templateUrl : STATIC_PAGE.COMPUTINGPOOL.URL
			}).when(STATIC_PAGE.HOST.NAME, {
				templateUrl : STATIC_PAGE.HOST.URL
			}).when(STATIC_PAGE.NETWORK.NAME, {
				templateUrl : STATIC_PAGE.NETWORK.URL
			}).when(STATIC_PAGE.STORAGE.NAME, {
				templateUrl : STATIC_PAGE.STORAGE.URL
			}).when(STATIC_PAGE.TEMPLATE.NAME, {
				templateUrl : STATIC_PAGE.TEMPLATE.URL
			}).when(STATIC_PAGE.CENTER.NAME, {
				templateUrl : STATIC_PAGE.CENTER.URL
			}).when(STATIC_PAGE.DESKTOPPOOL.NAME, {
				templateUrl : STATIC_PAGE.DESKTOPPOOL.URL
			}).when(STATIC_PAGE.USER.NAME, {
				templateUrl : STATIC_PAGE.USER.URL
			}).otherwise({
				redirectTo : STATIC_PAGE.COMPUTINGPOOL.NAME
			});
		}).controller('rootCtrl',
		function($scope, $modal, $log, $templateCache) {
			$scope.root = {
				/**
				 * modalConfig中type有三种类型：dialog/warning/prompt
				 * 
				 * @param modalConfig
				 */
				createModal : function(modalConfig) {
					var type = modalConfig.type;
					var url = "";
					switch (type) {
					case "dialog":
						url = "view/base/dialogTemplate.html";
						break;
					case "warning":
						url = "view/base/warningTemplate.html";
						break;
					case "prompt":
						url = "view/base/promptTemplate.html";
						break;
					}
					;
					var modalInstance = $modal.open({
						templateUrl : url,
						controller : function($scope, $modalInstance) {
							$scope.modal = {
								title : modalConfig.title,
								template : modalConfig.template,
								buttons : {
									ok : function() {
										modalConfig.buttons.ok();
										$modalInstance.close();
									},
									cancel : function() {
										modalConfig.buttons.cancel();
										$modalInstance.dismiss();
									}
								}
							};
						},
						size : "",
						width : modalConfig.width
					});
					modalInstance.result.then(function() {

					}, function() {

					});
				}
			};
		});