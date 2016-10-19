package pl.codeleak.demos.sbt.home;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import view.ContactVM;

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
		jdbcTemplate.update("INSERT INTO request_more_info(name,email,message,phone,section,locations_id,is_contactus_type,request_date,request_time) VALUES('"+vm.name+"','"+vm.email+"','"+vm.message+"','"+vm.phone+"','"+vm.urlName+"','"+16+"','"+1+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"')");
		
	}
	
}