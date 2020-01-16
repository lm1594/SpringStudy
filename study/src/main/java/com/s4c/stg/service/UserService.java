package com.s4c.stg.service;

import java.util.List;

import com.s4c.stg.user.UserVO;
public interface UserService {
    public List<UserVO> selectUser() throws Exception;
}
