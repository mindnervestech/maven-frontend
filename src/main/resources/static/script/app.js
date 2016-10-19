var app = angular.module('gliderApp', []);
app.factory('MyHttpInterceptor', function ($q) {
    return {
      request: function (config) {
    	  $('#loading-id').show();
          return config || $q.when(config);           
      },
      requestError: function (rejection) {
          $('#loading-id').hide();
          return $q.reject(rejection);
      },
      response: function (response) {
          $('#loading-id').hide();
          return response || $q.when(response);
      },
      responseError: function (rejection) {
          $('#loading-id').hide();
          return $q.reject(rejection);
      }
    };
});
app.config(function ($httpProvider) {
	$httpProvider.interceptors.push('MyHttpInterceptor');  
});
