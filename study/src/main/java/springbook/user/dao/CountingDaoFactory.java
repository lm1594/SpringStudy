package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.01.24
 * @history
 *  - 1.7.4 의존관계 주입의 응용
 */
@Configuration
public class CountingDaoFactory {
	
	@Bean
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realConnectionMaker());
	}
	
	@Bean
	public ConnectionMaker realConnectionMaker() {
		return new DConnectionMaker();
	}
}
