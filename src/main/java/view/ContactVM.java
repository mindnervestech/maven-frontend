package view;

import java.util.List;

public class ContactVM {

	public String name;
	public String email;
	public String message;
	public String phone;
	public String urlName;
	public String zipcode;
	public String productid;
	public String leadTypeId;
	public Long locations_id;
	public List<KeyValueDataVM> customData;
	
	
	
	
	public String getProductid() {
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public String getUrlName() {
		return urlName;
	}
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getLocations_id() {
		return locations_id;
	}
	public void setLocations_id(Long locations_id) {
		this.locations_id = locations_id;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public List<KeyValueDataVM> getCustomData() {
		return customData;
	}
	public void setCustomData(List<KeyValueDataVM> customData) {
		this.customData = customData;
	}
	
}
