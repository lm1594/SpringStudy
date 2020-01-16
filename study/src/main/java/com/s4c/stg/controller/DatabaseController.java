package com.s4c.stg.controller;

import java.util.List;
import java.util.Locale;
 
import javax.annotation.Resource;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
import com.s4c.stg.service.UserService;
import com.s4c.stg.user.UserVO;
 
 
@Controller
public class DatabaseController{
     
private static final Logger logger =  LoggerFactory.getLogger(DatabaseController.class);
     
    @Resource(name="userService")
    private UserService service;
     
    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/db", method = RequestMethod.GET)
    public String home(Locale locale, Model model) throws Exception{
  
        logger.info("db");
 
        List<UserVO> userList = service.selectUser();
        model.addAttribute("userList", userList);
 
        return "db";
    }
 
}
