app.controller("ContactController", function($scope,$http,$window /*,notificationService  notificationService*/) {
	
	console.log("conttact");
	$scope.contactData={};
	$scope.contactus = function(){
		var url      = window.location.href;
		var fileName = url.split("#");
		console.log(fileName[1]);
		$scope.contactData.name = $scope.contact.name;
		$scope.contactData.email = $scope.contact.email;
		$scope.contactData.phone = $scope.contact.phone;
		$scope.contactData.message = $scope.contact.message;
		$scope.contactData.urlName = fileName[1];
		console.log($scope.contactData);
		 console.log("save conttact");
		 	$("#submitDemo").attr("disabled", true);
			 $http({method:'POST',url:'saveContactDetail',data:$scope.contactData}).success(function(response) {
				//notificationService.success("Contact Us Submit Successfully");
				 $scope.contact = {};
			 /*}).error(function(){
					console.log("Error.................");*/
			 });
	 };
});