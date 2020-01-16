package com.s4c.stg.user.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.s4c.stg.user.UserVO;
 
@Repository("userDAO")
public class UserDAO {
 
    @Autowired
    private SqlSession sqlSession;
    private String Namespace = "com.s4c.stg.user.userMapper";
         
    public List<UserVO> selectUser() throws Exception {
        return sqlSession.selectList(Namespace+".selectUser");
    }
}