app.controller("ContactController", function($scope,$http,$window /*,notificationService  notificationService*/) {
	
	console.log("conttact");
	$scope.contactData={};
	$scope.manufactureId = function(id){
		console.log("????????",id);
		$scope.productid = id;
	};
	$scope.contactus = function(){
		var url      = window.location.href;
		var fileName = url.split("#");
		console.log(fileName[1]);
		$scope.contactData.name = $scope.contact.name;
		$scope.contactData.email = $scope.contact.email;
		$scope.contactData.phone = $scope.contact.phone;
		$scope.contactData.message = $scope.contact.message;
		$scope.contactData.productid = $scope.productid;
		if($scope.productid == undefined){
			$scope.contactData.productid = 0;
		}
		$scope.contactData.urlName = fileName[1];
		console.log($scope.contactData);
		 console.log("save conttact");
		 	$("#submitDemo").attr("disabled", true);
			 $http({method:'POST',url:'saveContactDetail',data:$scope.contactData}).success(function(response) {
				 $scope.contact = {};
			 
			 });
	 };
	 
});