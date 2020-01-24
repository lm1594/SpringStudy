package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.01.21
 * @history
 *  - 1.4.1 오브젝트 팩토리
 *  - 1.4.2 오브젝트 팩토리의 활용 : new DConnectionMaker()가 중복되서 나오게 될 경우에 대한 해결
 *  - 1.5 스프링의 IoC
 *  	- 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
 * 1-7장 의존관계 주입(DI)
 *  - 1.7.5 메소드를 이용한 의존관계 주입
 */
@Configuration
public class DaoFactory {
	
	@Bean
	public UserDao userDao() {
		UserDao userDao = new UserDao();
		userDao.setConnectionMaker(connectionMaker());
		return userDao;
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
}
