app.controller("ContactController", function($scope,$http,$window /*,notificationService  notificationService*/) {
	
	console.log("conttact");
	$scope.contactData={};
	$scope.leadTypeForm = "";
	$scope.jsonData = [];
	$scope.leadTypeId = 0;
	$scope.manufactureId = function(id,leadTypeId){
		console.log(leadTypeId);
		$scope.leadTypeId = leadTypeId;
		$http({method:'GET',url:'/getLeadTypeForm',params:{id:leadTypeId}})
		.success(function(data) {
			console.log(data);
			$scope.leadTypeForm = data;
			$scope.leadTypeForm.jsonData = angular.fromJson($scope.leadTypeForm.jsonData);
		});
		
		
		$scope.productid = id;
	};
	
	$scope.closePopup = function(){
		currentForm.parent().parent().addClass('invalid-form');
		currentForm.parent().parent().removeClass('message-sent');
		
	};
	
	
	$scope.saveLeadType = function(path,isPdf,form){
		console.log(form);
		console.log($("#"+form));
		var isValid = true;
		currentForm = $("#"+form);
		console.log(currentForm);
		currentForm.find('input').each(function() {
			if ($(this).prop('required') && $(this).val().length < 2) {
				$(this).addClass('not-valid');
				isValid = false;
			}
		});
		currentForm.find('textarea').each(function() {
			if ($(this).prop('required') && $(this).val().length < 2) {
				$(this).addClass('not-valid');
				isValid = false;
			}
		});
		console.log(isValid);
		if (!isValid) {
			currentForm.parent().parent().addClass('invalid-form');
			return;
		}
		currentForm.parent().parent().removeClass('invalid-form');
		currentForm.parent().parent().addClass('message-sent');
		
		console.log(isValid);
		if (isValid) {
            
            $scope.customList = [];
    		var url      = window.location.href;
    		var fileName = url.split("#");
    		console.log(fileName[1]);
    		console.log($scope.contact);
    		$scope.contactData.name = $scope.contact.name;
    		$scope.contactData.email = $scope.contact.email;
    		$scope.contactData.phone = $scope.contact.phone;
    		$scope.contactData.message = $scope.contact.message;
    		$scope.contactData.zipcode = $scope.contact.zipcode;
    		$scope.contactData.leadTypeId = $scope.leadTypeId;
    		console.log($scope.contact.customData);
    		console.log($scope.leadTypeForm);
    		if($scope.contact.customData != null){
    			$.each($scope.contact.customData, function(attr, value) {
    				angular.forEach($scope.leadTypeForm.jsonData, function(value1, key) {
    					if(value1.key == attr){
    						$scope.customList.push({
    							fieldId:value1.fieldId,
    			   	  			key:attr,
    			   	  			value:value,
    			   	  			savecrm:value1.savecrm,
    			   	  			displayGrid:value1.displayGrid,
    			   	  		    displayWebsite:value1.displayWebsite,
    			   	  		    component:value1.component,
    			   	  			formName:"Request More Info",
    						});
    					}
    				});
    			});	
    		}
    	
    			
    		$scope.contactData.customData = $scope.customList;
    		if($scope.productid == undefined){
    			$scope.contactData.productid = 0;
    		}else{
    			$scope.contactData.productid = $scope.productid;
    		}
    		$scope.contactData.urlName = fileName[1];
    		console.log($scope.contactData);
    		 console.log("save conttact");
    		 if(isPdf == '1'){
    			 $window.open(path,'_blank');
    		 }
    		 
    		 	//$("#submitDemo").attr("disabled", true);
    			 $http({method:'POST',url:'saveContactDetail',data:$scope.contactData}).success(function(response) {
    				 $scope.contact = {};
    				 console.log(isPdf);
    				 
    				 setTimeout(function() {
    					 $('.m-close').click();
    					 $scope.closePopup();
    					}, 2000);
    			 });
        } else {
        	console.log("ccc");
            $scope.showMsgs = true;
        }
		
	 };
	 
	 $scope.contactus = function(){
		 currentForm = $("#contactPage");
		$scope.contactData.name = $scope.contactUs.name;
 		$scope.contactData.email = $scope.contactUs.email;
 		$scope.contactData.phone = $scope.contactUs.phone;
 		$scope.contactData.message = $scope.contactUs.message;
 		$scope.contactData.zipcode = $scope.contactUs.zipcode;
 		$scope.contactData.leadTypeId = 0;
		$scope.contactData.productid = 0;
		currentForm.parent().parent().removeClass('invalid-form');
		currentForm.parent().parent().addClass('message-sent');
		 $http({method:'POST',url:'saveContactDetail',data:$scope.contactData}).success(function(response) {
			 $scope.contactUs = {};
			
		 });
	 }
	 
});
