package com.s4c.stg.service;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.s4c.stg.user.UserVO;
import com.s4c.stg.user.dao.UserDAO;

@Service("userService")
public class UserServiceImpl implements UserService {
 
   @Resource(name="userDAO")
   private UserDAO userDAO;
    
   @Override
   public List<UserVO> selectUser() throws Exception {
       return userDAO.selectUser();
   }
}