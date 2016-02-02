'use strict';

angular.module('kbconsole', ['ngRoute', 'KBConstants'])
/*
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: '../login/login.html',
    controller: 'LoginController'
  });
}])
*/
.controller('HomeController', [function($scope, KBConstants) {
	alert('home controller invoked');
	$scope.screwoff="SUCK IT";
}]);