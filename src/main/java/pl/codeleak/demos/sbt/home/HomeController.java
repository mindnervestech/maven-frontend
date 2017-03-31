package pl.codeleak.demos.sbt.home;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import view.ContactVM;
import view.CustomizationFormVm;
import view.SliderImagesVM;
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
    	//List<CollectionVM> mListAll = homeService.getManufacturersInfoAll();
    	
    	Map mListAllData = homeService.getCollectionAllData();
    	WebAnalyticsVM webAna = homeService.getWebAnalytics();
    	List<SliderImagesVM> sliderimage = homeService.getAllSliderImages();
    	model.addAttribute("manufacturersList", mListAllData.get("menuList"));
    	model.addAttribute("allCollectionList", mListAllData.get("artial"));
    	
    	model.addAttribute("sliderImg",sliderimage);
    	model.addAttribute("imagesserver", imagesserver);
    	model.addAttribute("webAnalytics", webAna);
    	//model.addAttribute("imagesserver", rootPath);
    	//model.addAttribute("manufacturersDataList", mListAll);
    	//model.addAttribute("collectionList", collectionListAll);
    	
    	
    	return "index";
    }
    
    @RequestMapping(value="/saveContactDetail",method=RequestMethod.POST) 
	public @ResponseBody void saveContactDetail(@RequestBody ContactVM vm) {
		
		System.out.println("In Request..");
		homeService.getContactInfoDetail(vm);
	
	}
    
    @RequestMapping(value = "/getLeadTypeForm", method = RequestMethod.GET)
	@ResponseBody
	public CustomizationFormVm getLeadTypeForm(HttpServletRequest request,@RequestParam("id") Long id) {
    	System.out.println("page..........");
		System.out.println(id);
		CustomizationFormVm cust = homeService.getLeadForm(id);
		return cust;
	}
    
  /*  @RequestMapping(value = "/downloadStatusFile", method = RequestMethod.POST)
	public @ResponseBody FileSystemResource getattchfile(final HttpServletResponse response)
	{
		String file = null;
		response.setHeader("Content-Type", "application/pdf;charset=UTF-8");
        //response.setHeader("Content-Transfer-Encoding", "binary"); 
        response.setHeader("Content-Disposition","inline;filename="+"OFSBRANDS.pdf");
		file = homeService.getSinglePdf(6L,imagesserver);
		 File files = new File(file);
         //return new FileSystemResource(file);
		ProductVM prodVm = homeService.getSingleProduct(attchId);
		if(prodVm.filePath !=null){
				file[0] = imagesserver+"furnitureImg/images"+ prodVm.filePath.replace("#","%23");
		}else{
			file[0] = null;
		}if(prodVm.cadFilePath !=null){
				file[1] = imagesserver+"furnitureImg/images"+prodVm.cadFilePath.replace("#","%23");
		}else{
			file[1] = null;
		}
		 return new FileSystemResource(files);
        // return file;
	}*/
    
}
