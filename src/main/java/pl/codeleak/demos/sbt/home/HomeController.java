package pl.codeleak.demos.sbt.home;

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

import view.ContactVM;

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
    	return "index";
    }
    
    @RequestMapping(value="/saveContactDetail",method=RequestMethod.POST) 
	public @ResponseBody void saveContactDetail(@RequestBody ContactVM vm) {
		
		System.out.println("In Request..");
		homeService.getContactInfoDetail(vm);
	
	}
        
}
