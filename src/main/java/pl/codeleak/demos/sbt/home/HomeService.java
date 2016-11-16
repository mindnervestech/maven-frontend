package pl.codeleak.demos.sbt.home;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import view.ContactVM;
import view.ManufacturersChildVM;
import view.ManufacturersImgVM;
import view.ManufacturersVM;
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
		System.out.println("managerId ===="+managerId);
		System.out.println("managerId.get(0).get ===="+managerId.get(0).get("id"));
		jdbcTemplate.update("INSERT INTO request_more_info(name,email,message,phone,section,locations_id,is_contactus_type,request_date,request_time,confirm_date,confirm_time,premium_flag,assigned_to_id) VALUES('"+vm.name+"','"+vm.email+"','"+vm.message+"','"+vm.phone+"','"+vm.urlName+"','"+16+"','"+1+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+1+"','"+managerId.get(0).get("id")+"')");
		
	}
	
	public List<ManufacturersVM> getManufacturersInfo() {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from add_product where public_status = 'publish' and parent_id is null and hide_website = 0");
		List<ManufacturersVM> manufacturersUrls = new ArrayList<ManufacturersVM>();
		
		for(Map map : rows) {
			List<ManufacturersChildVM> manufacturersChildList = new ArrayList<ManufacturersChildVM>();
			List<ManufacturersImgVM> manufacturersimgUrls = new ArrayList<ManufacturersImgVM>();
			ManufacturersVM vm = new ManufacturersVM();
			String title = (String) map.get("title");
			vm.id = (Long) map.get("id");
			vm.title = (String) map.get("title");
			vm.hrefTitle = title.replaceAll(" ", "_");
			vm.description = (String) map.get("description");
			vm.externalUrlLink = (String) map.get("external_url_link");
			List<Map<String, Object>> rows1 = jdbcTemplate.queryForList("select * from product_images where product_id = '"+vm.id+"'");
			for(Map map1 : rows1) {
				ManufacturersImgVM mVm = new ManufacturersImgVM();
				mVm.id = (Long) map1.get("id");
				mVm.path = (String) map1.get("path");
				mVm.thumbPath = (String) map1.get("thumb_path");
				mVm.name = (String) map1.get("image_name");
				manufacturersimgUrls.add(mVm);
				
			}
			vm.imgs = manufacturersimgUrls;
			
			List<Map<String, Object>> rowsSub = jdbcTemplate.queryForList("select * from add_product where public_status = 'publish' and parent_id = '"+vm.id+"' and hide_website = 0");
			for(Map mapSub : rowsSub) {
				List<ManufacturersImgVM> manufacturersimgSub = new ArrayList<ManufacturersImgVM>();
				ManufacturersChildVM mVmC = new ManufacturersChildVM();
				title = (String) mapSub.get("title");
				mVmC.id = (Long) mapSub.get("id");
				mVmC.title = (String) mapSub.get("title");
				mVmC.hrefTitle = title.replaceAll(" ", "_");
				vm.externalUrlLink = (String) map.get("external_url_link");
				mVmC.description = (String) mapSub.get("description");
				List<Map<String, Object>> rowsSubImg = jdbcTemplate.queryForList("select * from product_images where product_id = '"+mVmC.id+"'");
				for(Map mapSubImg : rowsSubImg) {
					ManufacturersImgVM mVm1 = new ManufacturersImgVM();
					mVm1.id = (Long) mapSubImg.get("id");
					mVm1.path = (String) mapSubImg.get("path");
					mVm1.thumbPath = (String) mapSubImg.get("thumb_path");
					mVm1.name = (String) mapSubImg.get("image_name");
					manufacturersimgSub.add(mVm1);
					
				}
				mVmC.imgs = manufacturersimgSub;
				manufacturersChildList.add(mVmC);
			}
			vm.submenu = manufacturersChildList;
			manufacturersUrls.add(vm);
		}
		return manufacturersUrls;
		
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
	
	public List<ManufacturersVM> getManufacturersInfoAll() {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from add_product where public_status = 'publish' and hide_website = 0");
		List<ManufacturersVM> manufacturersUrls = new ArrayList<ManufacturersVM>();
		
		for(Map map : rows) {
			List<ManufacturersImgVM> manufacturersimgUrls = new ArrayList<ManufacturersImgVM>();
			ManufacturersVM vm = new ManufacturersVM();
			String title = (String) map.get("title");
			vm.id = (Long) map.get("id");
			vm.title = (String) map.get("title");
			vm.hrefTitle = title.replaceAll(" ", "_");
			vm.externalUrlLink = (String) map.get("external_url_link");
			
			vm.description = (String) map.get("description");
			vm.logoPath = (String) map.get("file_path");
			
			List<Map<String, Object>> rows1 = jdbcTemplate.queryForList("select * from product_images where product_id = '"+vm.id+"'");
			for(Map map1 : rows1) {
				ManufacturersImgVM mVm = new ManufacturersImgVM();
				mVm.id = (Long) map1.get("id");
				mVm.path = (String) map1.get("path");
				mVm.thumbPath = (String) map1.get("thumb_path");
				mVm.name = (String) map1.get("image_name");
				manufacturersimgUrls.add(mVm);
				
			}
			vm.imgs = manufacturersimgUrls;
			
			
			manufacturersUrls.add(vm);
		}
		return manufacturersUrls;
		
	}
	
	
	
}