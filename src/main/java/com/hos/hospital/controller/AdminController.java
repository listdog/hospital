package com.hos.hospital.controller;


import com.github.pagehelper.PageInfo;
import com.hos.hospital.entity.Admin;
import com.hos.hospital.entity.Doctor;
import com.hos.hospital.entity.Section;
import com.hos.hospital.service.AdminService;
import com.hos.hospital.service.DoctorService;
import com.hos.hospital.service.MessagesService;
import com.hos.hospital.service.PatientService;
import com.hos.hospital.service.SectionService;
import com.hos.hospital.utils.ExcelInput;
import com.hos.hospital.utils.ExcelUtils;
import com.hos.hospital.utils.StringRandom;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后端管理员控制层
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
  
    @Autowired
    private SectionService sectionService;

    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private  PatientService  patientService;
    
    @Autowired
    private   MessagesService  messagesService;
    
    
    private Integer size  = 6;//每页显示数量
    

    @Value("${fileUrl}") //在配置文件中获取文件的保存路径
    private String filePath;

    
    /**
     * 导入
     * @param file
     * @param response
     * @throws IOException 
     */
    
    @RequestMapping("/excelInput")
    public String  excelInput(MultipartFile file,HttpServletResponse response) throws IOException {
    	String sb = file.getOriginalFilename();
        List<String[]> jieExcel = ExcelInput.jieExcel(file.getInputStream(), sb.substring(sb.indexOf(".")+1));
	
		  for (String[] strings : jieExcel) {
			  System.out.println(Arrays.toString(strings));  
		  }
        return  "redirect:/admin/index";
    }
    
    /**
     	* 导出
     * 
     * @param file
     * @param response
     */
    @RequestMapping("/xiazai")
    public void  excelString(HttpServletRequest request,HttpServletResponse response) {
        try {
        	response.setCharacterEncoding("utf-8");
        	//content-type类型是告诉页面要响应内容的类型，以及字符编码，页面要以什么方式打开
        	response.setContentType("application/force-download");// 设置强制下载不打开
            //Content-Disposition是MIMI协议的扩展，浏览器以什么方式处理wenjian
            response.setHeader("Content-Disposition", "attachment; fileName=exportFile.xlsx");

        	String[] title = new String[]{"姓名","科室id","科室","日期"};
            List<Doctor> list = doctorService.selectByExample(null);
            Workbook   wo     = ExcelUtils.getExcel("xlsx",title,list);
            wo.write(response.getOutputStream());
    		//Files.copy(file, response.getOutputStream());   		
    	} catch (IOException e) {
    		System.out.println("发生异常");
    		e.printStackTrace();
    	}
    
    }
    

    
    
    @RequestMapping("/index")
    public String index(Model model) {
        int  doctor  = doctorService.countByExample(null); //医生总数
        int  section = sectionService.countByExample(null); //科室总数
        int  patient = patientService.countByExample(null); //患者总数
        int  messages = messagesService.countByExample(null); //预约总数
        model.addAttribute("doctor",doctor);
        model.addAttribute("section",section);
        model.addAttribute("patient",patient);
        model.addAttribute("messages",messages);
        PageInfo<Doctor> pageInfo  =  doctorService.selectDoctorList(null,1,4);
         if(pageInfo.getList() != null && pageInfo.getList().size() >0 ) {
       	    List<Doctor> list = pageInfo.getList();
       	    StringBuffer sb = new StringBuffer();
       	    StringBuffer shu = new StringBuffer();
       	         int v = list.size()-1;
	        	 for(int i=0;i<list.size();i++) {
	        		if(v==i) {
	        			 sb.append(list.get(i).getName());
		        		 shu.append(list.get(i).getYipeoples());
	        		}else {
	        			 sb.append(list.get(i).getName()+",");
		        		 shu.append(list.get(i).getYipeoples()+",");
	        		}
	        	 }
	        	   model.addAttribute("name",sb.toString());
	               model.addAttribute("nu",shu.toString());
         }
        return  "admin/index";
    }
 
     


	    /**
	     *  管理员修改密码界面
	     * @return
	     */
    @RequestMapping("/adminUptatePage")
    public String adminUptatePage(Model model) {
        return "admin/adminUptate";
    }
    /**
     *  修改密码 
     */
    @RequestMapping("/adminUptatePassword")
    public String adminUptatePassword(Model model,Admin admin,HttpServletRequest request) {
        HttpSession session = request.getSession();
        Admin ad = (Admin) session.getAttribute("ADMIN");
        if(ad != null && admin.getPassword() != null){
                admin.setId(ad.getId());
                adminService.updateByPrimaryKeySelective(admin);
        }
        return  "redirect:/admin/index";
    }
    
    
    /**
     *  坐诊时间设置界面
     */
    @RequestMapping("/doctorTimePage")
    public String doctorTimePage(Integer id,Model model) {
       if(id !=  null) {
    	   Doctor doctor = doctorService.selectByPrimaryKey(id);
    	   model.addAttribute("doctor",doctor);
       }
        return  "admin/doctorTime";
    }
    
    /**
     *  坐诊时间设置界面
     * @throws ParseException 
     */
    @RequestMapping("/doctorTimeUpdate")
    public String doctorTimeUpdate(Integer id,Model model,String begindate,String enddate,String begintime,String endtime) throws ParseException {
       if(id !=  null) {
    	  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	  SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm"); 
    	   Doctor   doctor = new Doctor();
    	   doctor.setId(id);
    	   doctor.setBegindate(simpleDateFormat.parse(begindate)); 
    	   doctor.setEnddate(simpleDateFormat.parse(enddate));
           doctor.setBegintime(simpleDateFormat2.parse(begintime));
    	   doctor.setEndtime(simpleDateFormat2.parse(endtime));    
    	   doctorService.updateByPrimaryKeySelective(doctor);
       }
        return "redirect:/admin/doctorList";
    } 
    
    	
    
    /**
     *  修改医生信息
     */
    @RequestMapping("/admindoctorUptate")
    public String adminUptatePassword(Doctor doctor,Model model) {
       if(doctor !=  null && doctor.getId() != null) {
    	   if(doctor.getSid() != null) {
    		   Section section = sectionService.selectByPrimaryKey(doctor.getSid());
    		   doctor.setSid(section.getId());
    		   doctor.setSname(section.getName());
    	   }
    	   doctorService.updateByPrimaryKeySelective(doctor);
       }
        return  "redirect:/admin/doctorList";
    }
    
    /**
     *  删除医生信息
     */
    @RequestMapping("/doctorDelect")
    public String doctorDelect(Integer id,Model model) {
       if(id !=  null) {
    	
    	   doctorService.deleteByPrimaryKey(id);
       }
        return  "redirect:/admin/doctorList";
    }
    
    
    /**
     *  医生注册界面
    */
   @RequestMapping("/doctorAddPage")
   public String  doctorAddPage(Model model) {
	   	List<Section> sectionlist2  = null;
	   	Section  se = new  Section();
	   	se.setType(1);
		List<Section> sectionlist = sectionService.selectByExample(se);
	    if(sectionlist.size() > 0 ) {
	    	//科室详情
	    	Section  section = new  Section();
	    	section.setPid(sectionlist.get(0).getId());
	    	section.setType(2);
	    	sectionlist2 = sectionService.selectByExample(section);
	    }
	    model.addAttribute("sectionlist", sectionlist);
	    model.addAttribute("sectionlist2", sectionlist2);
	 	  return    "admin/doctorAdd";
   }
   
    
    
    @RequestMapping("/admindoctorAdd")
    public String admindoctorAdd(Doctor doctor,Model model) {
    	  if(doctor.getId() !=  null){
    	 	   if(doctor.getSid() != null) {
    			   Section selectByPrimaryKey = sectionService.selectByPrimaryKey(doctor.getSid());
    			   doctor.setSname(selectByPrimaryKey.getName());
    		   }
    	       doctorService.updateByPrimaryKeySelective(doctor);
    	   }
        return  "redirect:/admin/doctorList";
    }
    
    
    /**
     * 医生列表
     */
    @RequestMapping("/doctorList")
    public String doctorList(Model model, Doctor doctor, @RequestParam(value="page",defaultValue="1")Integer page) {
    	if(doctor == null) {
    		doctor = new Doctor();
    	}
    	PageInfo<Doctor> pageInfo  =  doctorService.selectDoctorList(doctor,page,size);
    	
    	List<Doctor> list = pageInfo.getList();
        model.addAttribute("doctorList",pageInfo.getList());
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("doctor",doctor);
        return    "admin/doctorList";
    }
    
    
    /**
     *  修改医生信息界面
     * @return
     */
	@RequestMapping("/doctorUptatePage")
	public String doctorUptatePage(Model model,Integer id) {
		if(id != null) {
			Doctor  doctor = doctorService.selectByPrimaryKey(id);
			List<Section> sectionlist2  = null;
			model.addAttribute("doctor",doctor);
			//科室
	    	Section  se = new  Section();
	    	se.setType(1);
		    List<Section> sectionlist = sectionService.selectByExample(se);
		    model.addAttribute("sectionlist", sectionlist);
	    	//科室详情
		 	Section  se1 = sectionService.selectByPrimaryKey(doctor.getSid());
			Section  section = new  Section();
			 if(se1 != null) {
			    	section.setPid(se1.getPid());
			    	section.setType(2);
			    	sectionlist2 = sectionService.selectByExample(section);
			 }else {
				 if(sectionlist.size() >0 ) {
	    			 section.setPid(sectionlist.get(0).getId());	
	    			 section.setType(2);
				     sectionlist2 = sectionService.selectByExample(section);
	    		 }
				 se1 = new   Section();
			 }
		    model.addAttribute("sectionlist2", sectionlist2);
		    model.addAttribute("se1", se1);
		    
		}
	    return "admin/doctorUptate";
	}
	    
    /**
     * 科室列表
     */
    @RequestMapping("/sectionList")
    public String sectionList(Model model, Section section, @RequestParam(value="page",defaultValue="1")Integer page) {
    	if(section == null) {
    		section = new Section();
    	}
    	section.setType(1);//1 科室
    	PageInfo<Section> pageInfo   = sectionService.selectSectionListt(section,page,size);
    	List<Section> list = pageInfo.getList();
    	List<Section> list2 = new ArrayList<Section>();
    	Section cs = new Section();
    	for (Section se : list) {
    		cs.setPid(se.getId());
    		List<Section> selectByExample = sectionService.selectByExample(cs);
    		se.setSlist(selectByExample);
    		list2.add(se);
		
    	}
    	
    	
    	
        model.addAttribute("sectionList",list2);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("section",section);
        return    "admin/sectionList";
    }
    
    /**
     * 科室详情下级列表
     */
    @RequestMapping("/sectionBelowList")
    public String sectionBelowList(Model model, Section section, Integer id) {
    	if(section == null) {
    		section = new Section();
    	}
    	section.setType(2);// 2 科室详情
    	section.setPid(id);
    	Section se = sectionService.selectByPrimaryKey(id);
    	List<Section> list  = sectionService.selectByExample(section);
        model.addAttribute("sectionList",list);
        model.addAttribute("section",section);
        model.addAttribute("se",se);
        return    "admin/sectionBelow";
    }
    
    
   /**
      * 跳转添加科室界面
    */
    @RequestMapping("/sectionAddPage")
    public String zuopinList() {
        return  "admin/sectionAdd";
    }

    /**
     * 跳转添加科室下级界面
   */
   @RequestMapping("/sectionAddBelowPage")
   public String zuopinList(Model model,Integer id) {
	   if(id != null) {
			  Section se = sectionService.selectByPrimaryKey(id);
		      model.addAttribute("se",se);
	   }
       return  "admin/sectionAddBelow";
   }
   
   
   
   /**
    * 跳转修改科室下级界面
  */
  @RequestMapping("/sectionBelowUptatePage")
  public String sectionBelowUptatePage(Model model,Integer id) {
	   if(id != null) {
			  Section se = sectionService.selectByPrimaryKey(id);
			  Section section = sectionService.selectByPrimaryKey(se.getPid());
		      model.addAttribute("se",se);
		      model.addAttribute("sname",section.getName());
	   }
      return  "admin/sectionBelowUptate";
  }
   /**
    * 跳转修改科室界面
  */
  @RequestMapping("/sectionUptatePage")
  public String sectionUptatePage(Model model,Integer id) {
	   if(id != null) {
			  Section se = sectionService.selectByPrimaryKey(id);
			  model.addAttribute("se",se);
	   }
      return  "admin/sectionUptate";
  }
  
    
    /**
     * 添加科室
     */
    @RequestMapping("/sectionAdd")
    @ResponseBody
    public Map<String,String> sectionAdd(String name) {
            Map<String, String> map =  new HashMap<String, String>();
            if(name != null ){
            	Section section = new Section();
            	section.setName(name);
            	section.setType(1);
            	sectionService.insertSelective(section);
                map.put("pan","ok");
            }else{
                map.put("pan","err");
            }
           return    map;
    }


    /**
     * 添加科室下级
     */
    @RequestMapping("/sectionAddBelow")
    public String sectionAddBelow(Section section) {
            	section.setType(2);
            	sectionService.insertSelective(section);
            	//"redirect:/admin/sectionBelowList?id="+section.getPid();
          return "redirect:/admin/sectionList";

    }
    
    /**
     * 修改科室
     */
    @RequestMapping("/sectionUptate")
    public String sectionUptate(Section section) {
          sectionService.updateByPrimaryKeySelective(section);
          return  "redirect:/admin/sectionList";

    }
    /**
     * 修改科室下级
     */
    @RequestMapping("/sectionBelowUptate")
    public String sectionBelowUptate(Section section) {
          sectionService.updateByPrimaryKeySelective(section);
          return "redirect:/admin/sectionBelowList?id="+section.getPid();

    }
    /**
     * 删除科室下级
     */
    @RequestMapping("/sectionBelowDelect")
    public String sectionBelowUptate(Integer id) {
	    	Section section = sectionService.selectByPrimaryKey(id);
	    	Integer pid =  section.getPid();
            sectionService.deleteByPrimaryKey(section.getId());
            return "redirect:/admin/sectionBelowList?id="+pid;

    }
    
    /**
     * 删除科室
     */
    @RequestMapping("/sectionDelect")
    public String sectionDelect(Integer id) {
    	    Section section  = new Section();
    	    section.setPid(id);
    	    section.setType(2);
	    	List<Section> list = sectionService.selectByExample(section);
            sectionService.deleteByPrimaryKey(id);
            for (Section section2 : list) {
            	sectionService.deleteByPrimaryKey(section2.getId());
			}
          return  "redirect:/admin/sectionList";
    }
    

    

}
