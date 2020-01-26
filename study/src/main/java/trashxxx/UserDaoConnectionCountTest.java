package trashxxx;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.dao.UserDao;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.01.24
 * @history
 *  - 1.7.4 의존관계 주입의 응용
 */
public class UserDaoConnectionCountTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		//
		// DAO 사용코드
		//
		CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
		System.out.println("Connection counter : " + ccm.getCounter());
		
	}
}
