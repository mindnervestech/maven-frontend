package pl.codeleak.demos.sbt.home;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import view.CollectionVM;
import view.ContactVM;
import view.MainCollectionVM;
import view.WebAnalyticsVM;

import com.planetj.servlet.filter.compression.CompressingFilter;

@Controller
class HomeController {

	@Bean
	public Filter compressingFilter() {
	    CompressingFilter compressingFilter = new CompressingFilter();
	    return compressingFilter;
	}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	HomeService homeService;
	
	 @Value("${spring.datasource.imagesserver}")
	   public String imagesserver;
	 
	 @Value("${String.datasource.rootPath}")
	 public String rootPath;
	
	
    @RequestMapping("/")
    String index(Model model) {
    	//List<MainCollectionVM> mList = homeService.getCollectionInfo();
    	//List<CollectionVM> collectionListAll = homeService.getAllCollection();
    	//List<CollectionVM> mListAllData = homeService.getCollectionAllData();
    	Map mListAllData = homeService.getCollectionAllData();
    	//List<CollectionVM> mListAll = homeService.getManufacturersInfoAll();
    	WebAnalyticsVM webAna = homeService.getWebAnalytics();
    	model.addAttribute("manufacturersList", mListAllData.get("menuList"));
    	model.addAttribute("allCollectionList", mListAllData.get("artial"));
    	
    	//model.addAttribute("manufacturersDataList", mListAll);
    	//model.addAttribute("collectionList", collectionListAll);
    	model.addAttribute("imagesserver", imagesserver);
    	//model.addAttribute("imagesserver", rootPath);
    	
    	model.addAttribute("webAnalytics", webAna);
    	
    	return "index";
    }
    //file:///D:/SOFTWARES/apache-tomcat-7.0.56/webapps/MavenImg/images/14-336920109/Sunset.jpg
    	//file:///D:/SOFTWARES/apache-tomcat-7.0.56/webapps/MavenImg/images/MavenImg/images/93-336920109/Waterlilies.jpg	
    
    @RequestMapping(value="/saveContactDetail",method=RequestMethod.POST) 
	public @ResponseBody void saveContactDetail(@RequestBody ContactVM vm) {
		
		System.out.println("In Request..");
		homeService.getContactInfoDetail(vm);
	
	}
        
}
