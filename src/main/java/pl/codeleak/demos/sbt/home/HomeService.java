package pl.codeleak.demos.sbt.home;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.DefaultEditorKit.CutAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import view.CollectionVM;
import view.ContactVM;
import view.CustomForm;
import view.CustomizationFormVm;
import view.KeyValueDataVM;
import view.LeadTypeVM;
import view.MainCollectionVM;
import view.ManufacturersImgVM;
import view.WebAnalyticsVM;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
class HomeService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	 @Value("${spring.datasource.imagesserver}")
	   public String imagesserver1;
	
	 @Value("${String.datasource.rootPath}")
	 public String rootPath;
	 
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
		List<Map<String, Object>> whichUser = jdbcTemplate.queryForList("select * from customer_request where location_id = '"+16+"'");
		Long userValue = 0L;
		if(whichUser.get(0).get("person_value").equals("Myself")){
			userValue = Long.parseLong(whichUser.get(0).get("users_id").toString());
		}else if(whichUser.get(0).get("person_value").equals("Sales Person(s)")){
			userValue = Long.parseLong(whichUser.get(0).get("users_id").toString());
		}else if(whichUser.get(0).get("person_value").equals("Me and all Sales people")){
			userValue = null;
		}else if(whichUser.get(0).get("redirect_value").equals("Automatically redirect an online customer requests based on")){
			userValue = null;
		}else{
			userValue = Long.parseLong(managerId.get(0).get("id").toString());
		}
		//List<Map<String, Object>> productId = jdbcTemplate.queryForList("select * from add_collection where");
		jdbcTemplate.update("INSERT INTO request_more_info(product_id,name,email,cust_zip_code,message,phone,section,locations_id,is_contactus_type,request_date,request_time,confirm_date,confirm_time,premium_flag,assigned_to_id,online_or_offline_leads) VALUES('"+vm.productid+"','"+vm.name+"','"+vm.email+"','"+vm.zipcode+"','"+vm.message+"','"+vm.phone+"','"+vm.urlName+"','"+16+"','"+Long.parseLong(vm.leadTypeId)+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+dateFormat.format(date)+"','"+timeDate.format(date)+"','"+0+"','"+userValue+"','"+1+"')");
		Long id = (long) jdbcTemplate.queryForInt("select max(id) from request_more_info");
		List<Map<String, Object>> leadIdData = jdbcTemplate.queryForList("select * from lead_type where id ='"+Long.parseLong(vm.leadTypeId)+"'");
		if(leadIdData.get(0).get("action_outcomes") != null){
			String[] parts = leadIdData.get(0).get("action_outcomes").toString().split(",");
			for(int i=0;i<parts.length;i++){
				if(parts[i].equals("Automatically add to CRM")){
					jdbcTemplate.update("INSERT INTO contacts(type,first_name,email,phone,groups_id) VALUES('Online','"+vm.name+"','"+vm.email+"','"+vm.phone+"','"+18+"')");
					Long maxId = (long) jdbcTemplate.queryForInt("select max(contact_id) from contacts");
					jdbcTemplate.update("INSERT INTO customization_crm(key_value,value,display_grid,form_name,crm_id,field_id,locations_id) VALUES('Nt_crm_group','Form Submission contacts','"+true+"','New Contact','"+maxId+"','"+14800902841L+"','"+16L+"')");
				}
				
			}
		}
		createPdf(vm, leadIdData);
		saveCustomData(id,vm.customData,Long.parseLong(vm.leadTypeId),jdbcTemplate);
	}
	
	public static void createDir(String pdfRootDir,Long locationId, int lastId) {
        File file = new File(pdfRootDir +"/"+ locationId +"/"+ "OnlineLead"+"/"+lastId);
        if (!file.exists()) {
                file.mkdirs();
        }
	}
	
	 private void createPdf(ContactVM vm ,List<Map<String, Object>> leadIdData) {
		 String filepath = null,findpath = null;
		 int lastId = jdbcTemplate.queryForInt("select MAX(id) from request_more_info");
			try {
				
				Date date = new Date();
				DateFormat timeFormat = new SimpleDateFormat("HH:mm:dd");
				DateFormat timeDate = new SimpleDateFormat("yyyy-MM");
				String productName = "";
				List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from add_collection where id='"+Long.parseLong(vm.productid)+"'");
				for(Map map : rows) {
					productName = (String) map.get("title");
				}
				
	            Document document = new Document();
	            createDir(rootPath, 16L, lastId);
	            filepath = rootPath + File.separator+ 16 +File.separator+ "OnlineLead"+File.separator+ lastId + File.separator + "onlineLead.pdf";
	            findpath = "/" + 16L +"/"+ "OnlineLead"+"/"+ lastId + "/" + "onlineLead.pdf";
	            //UPDATE table_name
	            //SET column1=value1,column2=value2,...
	            //WHERE some_column=some_value;
	            //jdbcTemplate.update("UPDATE trade_in  SET pdf_path='"+findpath+"' where id='"+4+"'");
	            
	            PdfWriter pdfWriter = 
	            PdfWriter.getInstance(document, new FileOutputStream(filepath));
	             
	            // Properties
	            document.addAuthor("Celinio");
	            document.addCreator("Celinio");
	            document.addSubject("iText with Maven");
	                        document.addTitle("Online Lead");
	                        document.addKeywords("iText, Maven, Java");
	             
	            document.open();
	             
	           /* Chunk chunk = new Chunk("Fourth tutorial");*/
	                        Font font = new Font();
	                        font.setStyle(Font.UNDERLINE);
	                        font.setStyle(Font.ITALIC);
	                       /* chunk.setFont(font);*/
	                      //  chunk.setBackground(Color.BLACK);
	                       /* document.add(chunk);*/
	                        
	                        Font font1 = new Font(FontFamily.HELVETICA, 8, Font.NORMAL,
	            					BaseColor.BLACK);
	            			Font font2 = new Font(FontFamily.HELVETICA, 8, Font.BOLD,
	            					BaseColor.BLACK);            
	            			
	            			
	            			
	            			PdfPTable Titlemain = new PdfPTable(1);
	            			Titlemain.setWidthPercentage(100);
	            			float[] TitlemainWidth = {2f};
	            			Titlemain.setWidths(TitlemainWidth);
	            			
	            			PdfPCell title = new PdfPCell(new Phrase("Online Lead"));
	            			title.setBorderColor(BaseColor.WHITE);
	            			title.setBackgroundColor(new BaseColor(255, 255, 255));
	            			Titlemain.addCell(title);
	            			
	            			PdfPTable contactInfo = new PdfPTable(4);
	            			contactInfo.setWidthPercentage(100);
	            			float[] contactInfoWidth = {2f,2f,2f,2f};
	            			contactInfo.setWidths(contactInfoWidth);
	            			
	            			PdfPCell firstname = new PdfPCell(new Phrase("Name:",font1));
	            			firstname.setBorderColor(BaseColor.WHITE);
	            			firstname.setBackgroundColor(new BaseColor(255, 255, 255));
	            			contactInfo.addCell(firstname);
	            			            			
	            			PdfPCell firstnameValue = new PdfPCell(new Paragraph(vm.name,font2));
	            			firstnameValue.setBorderColor(BaseColor.WHITE);
	            			firstnameValue.setBorderWidth(1f);
	          			    contactInfo.addCell(firstnameValue);
	            			
	            			            			
	            			PdfPCell phone = new PdfPCell(new Phrase("Phone:",font1));
	            			phone.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(phone);
	            			            			
	            			PdfPCell phoneValue = new PdfPCell(new Paragraph(vm.phone,font2));
	            			phoneValue.setBorderColor(BaseColor.WHITE);
	            			phoneValue.setBorderWidth(1f);
	            			contactInfo.addCell(phoneValue);
	            			
	            			PdfPCell workPhone = new PdfPCell(new Phrase("Email:",font1));
	            			workPhone.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(workPhone);
	            			            			
	            			PdfPCell workPhoneValue = new PdfPCell(new Paragraph(vm.email,font2));
	            			workPhoneValue.setBorderColor(BaseColor.WHITE);
	            			workPhoneValue.setBorderWidth(1f);
	            			contactInfo.addCell(workPhoneValue);
	            			
	            			PdfPCell dateV = new PdfPCell(new Phrase("Date",font1));
	            			dateV.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(dateV);
	            			
	            			PdfPCell dateValue = new PdfPCell(new Paragraph(timeDate.format(date),font2));
	            			dateValue.setBorderColor(BaseColor.WHITE);
	            			dateValue.setBorderWidth(1f);
	            			contactInfo.addCell(dateValue);
	            			
	            			PdfPCell msgTitle = new PdfPCell(new Phrase("Message",font1));
	            			msgTitle.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(msgTitle);
	            			
	            			PdfPCell msgValue = new PdfPCell(new Paragraph(vm.message,font2));
	            			msgValue.setBorderColor(BaseColor.WHITE);
	            			msgValue.setBorderWidth(1f);
	            			contactInfo.addCell(msgValue);
	            			
	            			PdfPCell zipTitle = new PdfPCell(new Phrase("ZipCode",font1));
	            			zipTitle.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(zipTitle);
	            			
	            			PdfPCell zipValue = new PdfPCell(new Paragraph(vm.zipcode,font2));
	            			zipValue.setBorderColor(BaseColor.WHITE);
	            			zipValue.setBorderWidth(1f);
	            			contactInfo.addCell(zipValue);
	            			
	            			PdfPCell collTitle = new PdfPCell(new Phrase("Collection",font1));
	            			collTitle.setBorderColor(BaseColor.WHITE);
	            			contactInfo.addCell(collTitle);
	            			
	            			PdfPCell collValue = new PdfPCell(new Paragraph(productName,font2));
	            			collValue.setBorderColor(BaseColor.WHITE);
	            			collValue.setBorderWidth(1f);
	            			contactInfo.addCell(collValue);
	            			
	            			
	            			List<Map<String, Object>> custForm = jdbcTemplate.queryForList("select * from customization_form where data_type = '"+leadIdData.get(0).get("lead_name")+"'");
	            			if(custForm.size() > 0){
	            				JsonParser parser = new JsonParser();
	            				JsonArray json = (JsonArray) parser.parse(custForm.get(0).get("json_data").toString());
	            				for(int i = 0; i < json.size(); i++)
	            				{
	            				      JsonObject objects = (JsonObject) json.get(i);
	            				      for(KeyValueDataVM custom:vm.customData){
	            				    	  if(objects.getAsJsonObject().get("key").getAsString().equals(custom.key)){
	            				    		    PdfPCell dymTitle = new PdfPCell(new Phrase(objects.getAsJsonObject().get("label").getAsString(),font1));
	            				    		    dymTitle.setBorderColor(BaseColor.WHITE);
	            		            			contactInfo.addCell(dymTitle);
	            		            			
	            		            			PdfPCell dymValue = new PdfPCell(new Paragraph(custom.value,font2));
	            		            			dymValue.setBorderColor(BaseColor.WHITE);
	            		            			dymValue.setBorderWidth(1f);
	            		            			contactInfo.addCell(dymValue);
		            				      }
	            				      }
	            				      
	            				     /* frm.label = objects.getAsJsonObject().get("label").getAsString();
	            				      frm.key = objects.getAsJsonObject().get("key").getAsString();*/
	            				}    
	            				if(json.size() % 2 != 0){
	            					PdfPCell blankTitle = new PdfPCell(new Phrase(".",font1));
		            				blankTitle.setBorderColor(BaseColor.WHITE);
			            			contactInfo.addCell(blankTitle);
			            			
			            			PdfPCell blankValue = new PdfPCell(new Paragraph("",font2));
			            			blankValue.setBorderColor(BaseColor.WHITE);
			            			blankValue.setBorderWidth(1f);
			            			contactInfo.addCell(blankValue);
	            				}
	            			}else{
	            				PdfPCell blankTitle = new PdfPCell(new Phrase(".",font1));
	            				blankTitle.setBorderColor(BaseColor.WHITE);
		            			contactInfo.addCell(blankTitle);
		            			
		            			PdfPCell blankValue = new PdfPCell(new Paragraph("",font2));
		            			blankValue.setBorderColor(BaseColor.WHITE);
		            			blankValue.setBorderWidth(1f);
		            			contactInfo.addCell(blankValue);
	            			}
	            			
	            			//--------------Vehicle Information
	            			
	            			/*PdfPTable vehicleInformationTitle = new PdfPTable(1);
	            			vehicleInformationTitle.setWidthPercentage(100);
	            			float[] vehicleInformationTitleWidth = {2f};
	            			vehicleInformationTitle.setWidths(vehicleInformationTitleWidth);
	            			
	            			PdfPCell vehicleInformationTitleValue = new PdfPCell(new Phrase("Collection Information"));
	            			vehicleInformationTitleValue.setBorderColor(BaseColor.WHITE);
	            			vehicleInformationTitleValue.setBackgroundColor(new BaseColor(255, 255, 255));
	            			vehicleInformationTitle.addCell(vehicleInformationTitleValue);*/
	            			
	            			
	            			//----------sub main Table----------	
	            			
	            			PdfPTable AddAllTableInMainTable = new PdfPTable(1);
	            			AddAllTableInMainTable.setWidthPercentage(100);
	            			float[] AddAllTableInMainTableWidth = {2f};
	            			AddAllTableInMainTable.setWidths(AddAllTableInMainTableWidth);
	            		
	            			PdfPCell hotelVoucherTitlemain1 = new PdfPCell(Titlemain);
	            			hotelVoucherTitlemain1.setBorder(Rectangle.NO_BORDER);
	            			AddAllTableInMainTable.addCell(hotelVoucherTitlemain1);
	            			
	            			PdfPCell contactInfoData = new PdfPCell(contactInfo);
	            			contactInfoData.setBorder(Rectangle.NO_BORDER);
	            			AddAllTableInMainTable.addCell(contactInfoData);
	            			
	            			/*PdfPCell vehicaleInfoTitle = new PdfPCell(vehicleInformationTitle);
	            			vehicaleInfoTitle.setBorder(Rectangle.NO_BORDER);
	            			AddAllTableInMainTable.addCell(vehicaleInfoTitle);*/
	            			
	            		
	            			
	            		//----------main Table----------	
	            		
	            			PdfPTable AddMainTable = new PdfPTable(1);
	            			AddMainTable.setWidthPercentage(100);
	            			float[] AddMainTableWidth = {2f};
	            			AddMainTable.setWidths(AddMainTableWidth);
	            		
	            			PdfPCell AddAllTableInMainTable1 = new PdfPCell(AddAllTableInMainTable);
	            			AddAllTableInMainTable1.setPadding(10);
	            			AddAllTableInMainTable1.setBorderWidth(1f);
	            			AddMainTable.addCell(AddAllTableInMainTable1);		
	            			
	            			
	            			document.add(AddMainTable);
	            			
	            			           			
	            			
	 
	            document.close();
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	}

	private static void saveCustomData(Long infoId,List<KeyValueDataVM> customData,Long leadtype,JdbcTemplate jdbcTemplate) {
	    	for(KeyValueDataVM custom:customData){
	    		String saveCrm = null;
	    		String displayGrid = null;
	    		String displayWebsite = null;
	    		if(custom.savecrm == null){
    				saveCrm = "false";
    			}else{
    				saveCrm = custom.savecrm;
    			}
    			
    			if(custom.displayGrid == null){
    				displayGrid = "false";
    			}else{
    				displayGrid = custom.displayGrid;
    			}
    			
    			if(custom.displayWebsite == null){
    				displayWebsite = "false";
    			}else{
    				displayWebsite = custom.displayWebsite;
    			}
	    		
	    		jdbcTemplate.update("INSERT INTO customization_data_value(key_value,value,save_crm,display_grid,display_website,form_name,lead_type,lead_id,field_id,locations_id) VALUES('"+custom.key+"','"+custom.value+"','"+saveCrm+"','"+displayGrid+"','"+displayWebsite+"','"+custom.formName+"','"+leadtype+"','"+infoId+"','"+custom.fieldId+"','"+16L+"')");
			}
	    	
			
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
	List<Map<String, Object>> rowsLead = jdbcTemplate.queryForList("select * from lead_type where deleted = 0 and shows = 1");
	List<LeadTypeVM> vmList = new ArrayList<LeadTypeVM>();
	int count = 0;
	for(Map mapLead : rowsLead) {
		if(mapLead.get("profile") == null){
			if("Request More Info".equals((String) mapLead.get("lead_name"))){
				addLeadInfo(mapLead,vmList,count);
			}
		}else if(mapLead.get("profile").toString().equals("All Collections")){
			if("Request More Info".equals((String) mapLead.get("lead_name"))){
				addLeadInfo(mapLead,vmList,count);
			}
		}else if(mapLead.get("profile").toString().equals("Select Collections Manually")){
			if("Request More Info".equals((String) mapLead.get("lead_name"))){
				String[] collId = mapLead.get("maunfacturers_ids").toString().split(",");
				for(int i=0;i < collId.length;i++){
					if(collId[i].equals(mapSub.get("id").toString())){
						addLeadInfo(mapLead,vmList,count);
					}
				}
			}
			
		}
		
	}
	for(Map mapLead : rowsLead) {
			/*if(!"Request More Info".equals((String) mapLead.get("lead_name"))){
				addLeadInfo(mapLead,vmList,count);
			}*/
			
			if(mapLead.get("profile") == null){
				if(!"Request More Info".equals((String) mapLead.get("lead_name"))){
					addLeadInfo(mapLead,vmList,count);
				}
			}else if(mapLead.get("profile").toString().equals("All Collections")){
				if(!"Request More Info".equals((String) mapLead.get("lead_name"))){
					addLeadInfo(mapLead,vmList,count);
				}
			}else if(mapLead.get("profile").toString().equals("Select Collections Manually")){
				if(!"Request More Info".equals((String) mapLead.get("lead_name"))){
					String[] collId = mapLead.get("maunfacturers_ids").toString().split(",");
					for(int i=0;i < collId.length;i++){
						if(collId[i].equals(mapSub.get("id").toString())){
							addLeadInfo(mapLead,vmList,count);
						}
					}
				}
				
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
		if(map1.get("title") == null){
			//mVm.title = "Main title";
		}else{
			mVm.title = (String) map1.get("title");
		}
		
		if(map1.get("description") == null){
			//mVm.description = "Lorem ipsum dolor sit amet.";
		}else{
			mVm.description = (String) map1.get("description");
		}
		
		manufacturersimgUrls.add(mVm);
		
	}
	vmSub.imgs = manufacturersimgUrls;
	manufacturersUrls.add(vmSub);
	
}

public CustomizationFormVm getLeadForm(Long id){
	List<Map<String, Object>> leadType = jdbcTemplate.queryForList("select * from lead_type where id = '"+id+"'");
	String leadT = null;
	if(leadType.size() > 0){
		leadT =(String) leadType.get(0).get("lead_name");
	}
	
	CustomizationFormVm custF = new CustomizationFormVm();
	List<Map<String, Object>> custForm = jdbcTemplate.queryForList("select * from customization_form where data_type = '"+leadT+"'");
	if(custForm.size() > 0){
		
		if(custForm.get(0).get("json_data") != null){
			custF.jsonData = (String) custForm.get(0).get("json_data");
		}
		if(custForm.get(0).get("json_data_add") != null){
			custF.jsonDataAdd = (String) custForm.get(0).get("json_data_add");
		}
		/*if(custForm.get(0).get("additional_data") != null){
			custF.additionalData = (Boolean) custForm.get(0).get("additional_data");
		}*/
		if(custForm.get(0).get("data_type") != null){
			custF.dataType = (String) custForm.get(0).get("data_type");
		}
		
		
		
	}
	
	return custF;
}


public void addLeadInfo(Map mapLead,List<LeadTypeVM> vmList,int count) {
	LeadTypeVM lVm = new LeadTypeVM();
	lVm.id = (Long) mapLead.get("id");
	lVm.leadType = (String) mapLead.get("lead_name");
	if(mapLead.get("action_title") != null){
		lVm.actionTitle = (String) mapLead.get("action_title");
	}else{
		lVm.actionTitle = (String) mapLead.get("lead_name");
	}
	if(mapLead.get("confirmation_msg") != null){
		lVm.confirmationMsg = (String) mapLead.get("confirmation_msg");
	}else{
		lVm.confirmationMsg = "Thank you for submitting your request, our representative will contact you shortly";
	}
	
	lVm.pdfDownload = "0";
	if(mapLead.get("action_outcomes") != null){
		String[] parts = mapLead.get("action_outcomes").toString().split(",");
		for(int i=0;i<parts.length;i++){
			if(parts[i].equals("Client downloads PDF file")){
				lVm.pdfDownload = "1";
				if(mapLead.get("dowpdf_ids") != null){
					List<Map<String, Object>> custPdf = jdbcTemplate.queryForList("select * from customer_pdf where id = '"+(Long) mapLead.get("dowpdf_ids")+"'");
					lVm.pdfId = (Long) custPdf.get(0).get("id");
					lVm.pdfPath = (String) custPdf.get(0).get("pdf_path");
				}
			}
		}
	}
	
	
	if(mapLead.get("hide_website") != null){
		lVm.showOnWebsite = (Boolean) mapLead.get("hide_website");
	}
	List<Map<String, Object>> custForm = jdbcTemplate.queryForList("select * from customization_form where data_type = '"+lVm.leadType+"'");
	if(custForm.size() > 0){
		JsonParser parser = new JsonParser();
		JsonParser parser1 = new JsonParser();
		JsonArray json = (JsonArray) parser.parse(custForm.get(0).get("json_data").toString());
		List<CustomForm> frmList = new ArrayList<>();
		for(int i = 0; i < json.size(); i++)
		{
		      JsonObject objects = (JsonObject) json.get(i);
		      CustomForm frm = new CustomForm();
		      frm.component = objects.getAsJsonObject().get("component").getAsString();
		      frm.label = objects.getAsJsonObject().get("label").getAsString();
		      if( objects.getAsJsonObject().get("required") != null){
		    	  if(objects.getAsJsonObject().get("required").getAsBoolean() == true){
		    		  frm.required = "required";
		    	  }else{
		    		  frm.required = "norequired";
		    	  }
		      }
		      //frm.required = objects.getAsJsonObject().get("required").getAsBoolean();
		      frm.key = objects.getAsJsonObject().get("key").getAsString();
		      frm.index = objects.getAsJsonObject().get("index").getAsLong();
		      frm.editable = objects.getAsJsonObject().get("editable").getAsBoolean();
		      JsonArray options =((JsonArray) parser1.parse(objects.getAsJsonObject().get("options").toString()));
		      List<String> optionArray = new ArrayList<String>();
		      for(int j = 0; j < options.size(); j++)
				{
		    	  optionArray.add(options.get(j).toString().replace("\"",""));
				}
		      frm.options = optionArray;
		      frmList.add(frm);
		}    

	      lVm.custForm = frmList;
	      System.out.println(lVm.custForm.size());
	}
	vmList.add(lVm);
	count++;
	
	
}
/*
public String getSinglePdf(Long id,String imagesserver){
	String file = null;
	List<Map<String, Object>> custForm = jdbcTemplate.queryForList("select * from customer_pdf");
	if(custForm.size() > 0){
		
		if(custForm.get(0).get("pdf_path") != null){
				file = "http://45.33.50.143:8080/"+"MavenImg/images/"+ custForm.get(0).get("pdf_path").toString().replace("#","%23");
		}
	}	
	return file;
}*/




/*@RequestMapping(value = "/downloadStatusFile/{prodId}", method = RequestMethod.GET)
@ResponseBody
public String getattchfile(final HttpServletResponse response, @PathVariable("prodId") Long attchId)
{
	
	ProductVM prodVm = homeService.getSingleProduct(attchId);
	
	 //response.setContentType("application/pdf;charset=UTF-8");
	 response.setHeader("Content-Type", "application/pdf;charset=UTF-8");
     //response.setHeader("Content-Transfer-Encoding", "binary"); 
     response.setHeader("Content-Disposition","inline;filename="+prodVm.fileName);
	 String path = rootPath+prodVm.filePath;
     File file = new File(rootPath+prodVm.filePath);
     System.out.println(path);
     return path;
     //return new FileSystemResource(file);
	
}*/



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