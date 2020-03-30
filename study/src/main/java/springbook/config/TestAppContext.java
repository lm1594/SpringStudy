package springbook.config;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;

import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.service.DummyMailSender;
import springbook.user.service.TestUserService;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 * 	7장 스프링 핵심 기술의 응용
 *   7.6장 스프링3.1의 DI
 *    - 7.6.3 컨텍스트 분리와 @Import
 */
@Configuration
public class TestAppContext {
	@Bean
	public UserService testUserService () {
		return new TestUserService();
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
}
