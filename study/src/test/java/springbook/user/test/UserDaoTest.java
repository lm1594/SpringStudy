package springbook.user.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

/**
 * 토비의 스프링 1장
 * @author 이경민
 * @since 2020.01.19
 * @history
 *  - 1.3.3 관계설정 책임의 분리 : UserDaotest는 UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존관계를 설정하는 책임을 담당 -> UserDao에 있으면 안되는 다른 관심사항, 책임을 떠넘기는 작업
 *  - 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
 *  - 1.8.2 XML을 이용하는 애플리케이션 컨텍스트
 * 2장 테스트
 *  - 2.1.3 UserDaoTest의 문제점 두가지
 * 		(1) 수동 확인 작업의 번거로움
 * 		(2) 실행 작업의 번거로움
 *  - 2.2.1 테스트 검증의 자동화
 *  - 2.2.2 테스트의 효율적인 수행과 결과 관리
 */
public class UserDaoTest {

	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {
		
		/**
		 * 1.8.2 XML을 이용하는 애플리케이션 컨텍스트
		 */
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		User user = new User();
		user.setId("kyounmgmin");
		user.setName("이경민");
		user.setPassword("123456");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.test.UserDaoTest");
	}

}
