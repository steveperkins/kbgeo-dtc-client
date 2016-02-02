'use strict';

// Declare app level module which depends on views, and components
angular.module('kbconsole', [
  'ngRoute',
  'kbconsole.login'/*,
  'myApp.view2',
  'myApp.version'*/
])

.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
	    templateUrl: '../home/index.html',
	    controller: 'HomeController'
	  });
//	  .otherwise({redirectTo: '/'});
}])

// Application-wide constants
.service('KBConstants', function () {
    // default values
    var values = {
      API_ROOT: 'http://localhost:8080/api',
      WEB_ROOT: 'http://localhost:8080/web/console'
    };
    return {
      $get: function () {
        return values;
      }
    };
});