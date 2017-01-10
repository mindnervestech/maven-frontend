package pl.codeleak.demos.sbt.home;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.fabric.xmlrpc.base.Array;

import view.CollectionVM;
import view.ContactVM;
import view.CustomForm;
import view.LeadTypeVM;
import view.MainCollectionVM;
import view.ManufacturersImgVM;
import view.WebAnalyticsVM;

@Controller
class HomeService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	 @Value("${spring.datasource.imagesserver}")
	   public String imagesserver1;

	 
	   public String otherEmailId = "info@flexformsf.com";
	   public String otherEmailpassword = "Miesiit3360";
	 
	 @Value("${spring.datasource.emailId}")
	   public String emailId;
	 
	 @Value("${spring.datasource.emailpassword}")
	   public String emailpassword;
	 
	
	static int userId = 1;


	public void getContactInfoDetail(ContactVM vm) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:dd");
		DateFormat timeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> managerId = jdbcTemplate.queryForList("select * from auth_user where location_id = '"+16+"' and role = '"+"Manager"+"'");
		//List<Map<String, Object>> productId = jdbcTemplate.queryForList("select * from add_collection where");
		jdbcTemplate.update("INSERT INTO request_more_info(product_id,name,email,message,phone,section,locations_id,is_contactus_type,request_date,request_time,confirm_date,confirm_time,premium_flag,assigned_to_id) VALUES('"+vm.productid+"','"+vm.name+"','"+vm.email+"','"+vm.message+"','"+vm.phone+"','"+vm.urlName+"','"+16+"','"+1+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+1+"','"+managerId.get(0).get("id")+"')");
		
	}
	
	
	
	public void AddCollectionData(Map mapSub,CollectionVM cVm,List<CollectionVM> coll) {
		String title = (String) mapSub.get("title");
		cVm.id = (Long) mapSub.get("id");
		cVm.title = (String) mapSub.get("title");
		if(title != null){
			cVm.hrefTitle = title.replaceAll(" ", "_");
		}
		cVm.description = (String) mapSub.get("description");
		cVm.externalUrlLink = (String) mapSub.get("external_url_link");
		List<Map<String, Object>> rows1 = jdbcTemplate.queryForList("select * from product_images where product_id = '"+cVm.id+"'");
		List<ManufacturersImgVM> mVms = new ArrayList<ManufacturersImgVM>();
		for(Map map1 : rows1) {
			ManufacturersImgVM mVm = new ManufacturersImgVM();
			mVm.id = (Long) map1.get("id");
			mVm.path = (String) map1.get("path");
			mVm.thumbPath = (String) map1.get("thumb_path");
			mVm.name = (String) map1.get("image_name");
			mVm.srNumber = (Integer) mapSub.get("srNumber");
			mVm.title = (String) map1.get("title");
			mVm.description = (String) map1.get("description");
			mVms.add(mVm);
			
		}
		cVm.imgs = mVms;
		coll.add(cVm);
		
	}

	public WebAnalyticsVM getWebAnalytics(){
		WebAnalyticsVM web = new WebAnalyticsVM();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from web_analytics");
		for(Map map : rows) {
			web.id = (Long) map.get("id");
			web.trackingCode = (String) map.get("tracking_code");
		}
		return web;
	}
	
	
public Map getCollectionAllData() {
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from inventory_setting where status is null and hide_website = 0");
		List<CollectionVM> manufacturersUrls = new ArrayList<CollectionVM>();
		List<MainCollectionVM> maList = new ArrayList<MainCollectionVM>();
		
		for(Map mapMain : rows) {
			MainCollectionVM mCollectionVM = new MainCollectionVM();
			CollectionVM vmMain = new CollectionVM();
			
			String collection = (String) mapMain.get("collection");
			vmMain.title = collection;
			vmMain.hrefTitle = collection.replaceAll(" ", "_");
			if(mapMain.get("path") != null){
				vmMain.logoPath = (String) mapMain.get("path");
			}
			vmMain.menuType = "MainCollection";
			
			mCollectionVM.collection = collection;
			mCollectionVM.hrefCollection = collection.replaceAll(" ", "_");
			
			List<CollectionVM> coll = new ArrayList<CollectionVM>();
			
			manufacturersUrls.add(vmMain);
			
			List<Map<String, Object>> rowsSub = jdbcTemplate.queryForList("select * from add_collection where public_status = 'publish' and main_collection_id = '"+(Long) mapMain.get("id")+"' and parent_id IS NULL and hide_website = 0");
			for(Map mapSub : rowsSub) {
				List<CollectionVM> collChild = new ArrayList<CollectionVM>();
				List<ManufacturersImgVM> manufacturersimgUrls = new ArrayList<ManufacturersImgVM>();
				CollectionVM vmSub = new CollectionVM();
				CollectionVM cVm = new CollectionVM();
				AddCollectionData(mapSub,cVm,coll);
				AddCollectionDataList(manufacturersUrls,manufacturersimgUrls,vmSub,mapSub);
				List<Map<String, Object>> rowsChild = jdbcTemplate.queryForList("select * from add_collection where public_status = 'publish' and parent_id = '"+vmSub.id+"' and hide_website = 0");
				for(Map mapChild:rowsChild){
					List<ManufacturersImgVM> collectionImgUrls = new ArrayList<ManufacturersImgVM>();
					CollectionVM vmColl = new CollectionVM();
					
					CollectionVM cVmChild = new CollectionVM();
					AddCollectionData(mapChild,cVmChild,collChild);
					AddCollectionDataList(manufacturersUrls,collectionImgUrls,vmColl,mapChild);
				}
				cVm.count = "0";
				if(collChild.size() > 0){
					cVm.count = "1";
				}
				cVm.childCollection = collChild;
			}
			mCollectionVM.subCollection = coll;
			maList.add(mCollectionVM);
		}
		Map<String,Object> listMap = new HashMap<String,Object>();
		listMap.put("menuList", maList);
		listMap.put("artial", manufacturersUrls);
		return listMap;
		
	}

public void AddCollectionDataList(List<CollectionVM> manufacturersUrls,
		List<ManufacturersImgVM> manufacturersimgUrls, CollectionVM vmSub,Map mapSub) {
	String title = (String) mapSub.get("title");
	vmSub.id = (Long) mapSub.get("id");
	vmSub.title = (String) mapSub.get("title");
	vmSub.hrefTitle = title.replaceAll(" ", "_");
	vmSub.externalUrlLink = (String) mapSub.get("external_url_link");
	vmSub.menuType = "subCollection";
	vmSub.description = (String) mapSub.get("description");
	vmSub.logoPath = (String) mapSub.get("file_path");
	if(mapSub.get("file_type") != null){
		vmSub.fileType = (String) mapSub.get("file_type");
	}else{
		vmSub.fileType = "png";
	}
	List<Map<String, Object>> rowsLead = jdbcTemplate.queryForList("select * from lead_type where deleted = 0");
	List<LeadTypeVM> vmList = new ArrayList<LeadTypeVM>();
	int count = 0;
	for(Map mapLead : rowsLead) {
		if("Request More Info".equals((String) mapLead.get("lead_name"))){
			addLeadInfo(mapLead,vmList,count);
		}
	}
	for(Map mapLead : rowsLead) {
		if(!"Request More Info".equals((String) mapLead.get("lead_name"))){
			addLeadInfo(mapLead,vmList,count);
		}
		if(count == 5){
			break;
		}
	}
	vmSub.leadType = vmList;
	vmSub.leadCount = vmList.size();
	List<Map<String, Object>> rows1 = jdbcTemplate.queryForList("select * from collection_images where collection_id = '"+vmSub.id+"'");
	for(Map map1 : rows1) {
		ManufacturersImgVM mVm = new ManufacturersImgVM();
		mVm.id = (Long) map1.get("id");
		mVm.path = (String) map1.get("path");
		if(map1.get("sr_number") != null){
			mVm.srNumber = (Integer) map1.get("sr_number");
		}
		
		mVm.thumbPath = (String) map1.get("thumb_path");
		mVm.name = (String) map1.get("image_name");
		mVm.title = (String) map1.get("title");
		mVm.description = (String) map1.get("description");
		manufacturersimgUrls.add(mVm);
		
	}
	vmSub.imgs = manufacturersimgUrls;
	//9028746476
	
	manufacturersUrls.add(vmSub);
	
}



public void addLeadInfo(Map mapLead,List<LeadTypeVM> vmList,int count) {
	LeadTypeVM lVm = new LeadTypeVM();
	lVm.id = (Long) mapLead.get("id");
	lVm.leadType = (String) mapLead.get("lead_name");
	if(mapLead.get("hide_website") != null){
		lVm.showOnWebsite = (Boolean) mapLead.get("hide_website");
	}
	List<Map<String, Object>> custForm = jdbcTemplate.queryForList("select * from customization_form where data_type = '"+lVm.leadType+"'");
	if(custForm.size() > 0){
		JsonParser parser = new JsonParser();
		JsonArray json = (JsonArray) parser.parse(custForm.get(0).get("json_data").toString());
		List<CustomForm> frmList = new ArrayList<>();
		for(int i = 0; i < json.size(); i++)
		{
		      JsonObject objects = (JsonObject) json.get(i);
		      CustomForm frm = new CustomForm();
		      frm.component = objects.getAsJsonObject().get("component").getAsString();
		      frm.label = objects.getAsJsonObject().get("label").getAsString();
		      frm.required = objects.getAsJsonObject().get("required").getAsBoolean();
		      frm.key = objects.getAsJsonObject().get("key").getAsString();
		      frm.index = objects.getAsJsonObject().get("index").getAsLong();
		      frm.editable = objects.getAsJsonObject().get("editable").getAsBoolean();
		      frmList.add(frm);
		}    

	      lVm.custForm = frmList;
	      System.out.println(lVm.custForm.size());
	}
	vmList.add(lVm);
	count++;
	
	
}


	/*public List<CollectionVM> getAllCollection() {
	
	
	List<CollectionVM> manufacturersUrls = new ArrayList<CollectionVM>();
	
	List<Map<String, Object>> rowsSub = jdbcTemplate.queryForList("select * from add_collection where public_status = 'publish' and hide_website = 0");
	for(Map map : rowsSub) {
		List<ManufacturersImgVM> manufacturersimgUrls = new ArrayList<ManufacturersImgVM>();
		CollectionVM vm = new CollectionVM();
		String title = (String) map.get("title");
		vm.id = (Long) map.get("id");
		vm.title = (String) map.get("title");
		vm.hrefTitle = title.replaceAll(" ", "_");
		vm.externalUrlLink = (String) map.get("external_url_link");
		
		vm.description = (String) map.get("description");
		vm.logoPath = (String) map.get("file_path");
		if(map.get("file_type") != null){
			vm.fileType = (String) map.get("file_type");
		}else{
			vm.fileType = "png";
		}
		List<Map<String, Object>> rowsLead = jdbcTemplate.queryForList("select * from lead_type where deleted = 0");
		List<LeadTypeVM> vmList = new ArrayList<LeadTypeVM>();
		int count = 0;
		for(Map mapLead : rowsLead) {
			LeadTypeVM lVm = new LeadTypeVM();
			lVm.id = (Long) mapLead.get("id");
			lVm.leadType = (String) mapLead.get("lead_name");
			if(mapLead.get("hide_website") != null){
				lVm.showOnWebsite = (Boolean) mapLead.get("hide_website");
			}
			vmList.add(lVm);
			count++;
			if(count == 5){
				vm.leadType = vmList;
				break;
			}
		}
		
		List<Map<String, Object>> rows1 = jdbcTemplate.queryForList("select * from collection_images where collection_id = '"+vm.id+"'");
		for(Map map1 : rows1) {
			ManufacturersImgVM mVm = new ManufacturersImgVM();
			mVm.id = (Long) map1.get("id");
			mVm.path = (String) map1.get("path");
			mVm.thumbPath = (String) map1.get("thumb_path");
			mVm.name = (String) map1.get("image_name");
			mVm.title = (String) map1.get("title");
			mVm.description = (String) map1.get("description");
			manufacturersimgUrls.add(mVm);
			
		}
		vm.imgs = manufacturersimgUrls;
		
		
		manufacturersUrls.add(vm);
	}
	return manufacturersUrls;
	}
	
	public List<CollectionVM> getManufacturersInfoAll() {
	
	List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from inventory_setting where hide_website = 0");
	List<CollectionVM> manufacturersUrls = new ArrayList<CollectionVM>();
	
	for(Map map : rows) {
		CollectionVM vm = new CollectionVM();
		String collection = (String) map.get("collection");
		vm.title = collection;
		vm.hrefTitle = collection.replaceAll(" ", "_");
		if(map.get("path") != null){
			vm.logoPath = (String) map.get("path");
		}
		manufacturersUrls.add(vm);
	}
	
	return manufacturersUrls;
	
	}
	*/

	/*public List<MainCollectionVM> getCollectionInfo() {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from inventory_setting where hide_website = 0");
		List<MainCollectionVM> maList = new ArrayList<MainCollectionVM>();
		
		
		
		for(Map map : rows) {
			MainCollectionVM mCollectionVM = new MainCollectionVM();
			String collection = (String) map.get("collection");
			mCollectionVM.collection = collection;
			mCollectionVM.hrefCollection = collection.replaceAll(" ", "_");
			
			List<CollectionVM> coll = new ArrayList<CollectionVM>();
			
			List<Map<String, Object>> rowsSub = jdbcTemplate.queryForList("select * from add_collection where public_status = 'publish' and main_collection_id = '"+(Long) map.get("id")+"' and parent_id IS NULL and hide_website = 0");
			for(Map mapSub : rowsSub) {
				List<CollectionVM> collChild = new ArrayList<CollectionVM>();
				CollectionVM cVm = new CollectionVM();
				AddCollectionData(mapSub,cVm,coll);
				List<Map<String, Object>> rowsChild = jdbcTemplate.queryForList("select * from add_collection where public_status = 'publish' and parent_id = '"+(Long) mapSub.get("id")+"' and hide_website = 0");
				for(Map mapChild : rowsChild) {
					CollectionVM cVmChild = new CollectionVM();
					AddCollectionData(mapChild,cVmChild,collChild);
				}
				cVm.count = "0";
				if(collChild.size() > 0){
					cVm.count = "1";
				}
				
				cVm.childCollection = collChild;
			}
			mCollectionVM.subCollection = coll;
			maList.add(mCollectionVM);
			
		}
	
		return maList;
		
	}*/
	
}