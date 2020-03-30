package springbook.user.config;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.TestUserService;
import springbook.user.service.UserService;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 * 	7장 스프링 핵심 기술의 응용
 *   7.6장 스프링3.1의 DI
 *    - 7.6.1 자바 코드를 이용한 빈 설정
 *    - 7.6.4 프로파일
 *    - 7.6.5 프로퍼티 소스
 */
@Configuration
@EnableTransactionManagement
@ImportResource("/test-applicationContext.xml")
@ComponentScan(basePackages = "springbook.user")
@Import(SqlServiceContext.class)
public class AppContext {

	// @Autowired : 필드의 타입을 기준으로, @Resource : 필드의 이름을 기준으로
	
	//@Autowired Environment env;
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}")			String url;	
	@Value("${db.username}")	String username;
	@Value("${db.password}")	String password;
	
	//----------------------------------------------------- 
	// DB 연결과 트랜잭션
	//-----------------------------------------------------
	@Bean
	@Primary
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		System.out.println("driverClass : " + driverClass);
		System.out.println("url : " + url);
		System.out.println("username : " + username);
		System.out.println("password : " + password);
		
		dataSource.setDriverClass(this.driverClass);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		
		return transactionManager;
	}
	
	/**
	 * 토비의 스프링
	 * @author 이경민
	 * @history
	 * 	7장 스프링 핵심 기술의 응용
	 *   7.6장 스프링3.1의 DI
	 *    - 7.6.4 프로파일
	 */
	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("localhost");
			return mailSender;
		}
	}
	
	/**
	 * 토비의 스프링
	 * @author 이경민
	 * @history
	 * 	7장 스프링 핵심 기술의 응용
	 *   7.6장 스프링3.1의 DI
	 *    - 7.6.3 컨텍스트 분리와 @Import
	 *    - 7.6.4 프로파일
	 */
	@Configuration
	@Profile("test")
	public static class TestAppContext {
		@Bean
		public UserService testUserService () {
			return new TestUserService();
		}
		
		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}
}
