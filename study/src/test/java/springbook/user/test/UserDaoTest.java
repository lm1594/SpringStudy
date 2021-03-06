package springbook.user.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.config.AppContext;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
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
 *  - 2.3.2 테스트 결과의 일관성
 *  - 2.3.3 포괄적인 테스트
 *  - 2.3.5 테스트코드 개선
 *  	-> jUnit은 각 테스트마다 새로운 오브젝트를 만들어 냄 - 인스턴스 변수 초기화 됌 (독립성 보장)
 *  	-> 픽스쳐 fixture : 테스트를 수행하는 데 필요한 정보나 오브젝트 ex) dao, user 
 *  2.4장 스프링 테스트 적용
 *  - 2.4.1 테스트를 위한 애플리케이션 컨텍스트 관리
 *  - 2.4.2 DI와 테스트
 *  	-> 테스트 코드의 의한 DI
 *  	-> 테스트를 위한 별도의 DI 설정
 *  	-> 컨테이너 없는 DI 테스트 : UserDao가 스프링의 API에 의존하지 않고 자신의 관심에만 집중해서 깔끔하게 만들어진 코드이기 때문에 어떤 테스트 방법도 완벽하게 통과한다.
 *  3장 템플릿
 *   -3.6.4 query() : getAll()에 대한 테스트
 * 4장 예외  
 *  4.2장 예외전환
 *    - 4.2.4 기술에 독립적인 UserDao만들기
 * 5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.1 필드추가
 *    - 5.1.2 사용자 수정 기능 추가 
 *  7장 스프링 핵심 기술의 응용
 *   7.6 스프링 3.1의 DI
 *    - 7.6.1 자바 코드를 이용한 빈 설정
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes=AppContext.class)
public class UserDaoTest {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() {
		user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "lm1595@naver.com");
		user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "lm1595@naver.com");
		user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "lm1595@naver.com");
		
//		dao = new UserDaoJdbc();
//		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC", "spring", "book", true);
//		dao.setDataSource(dataSource);
	}
	
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
		
	}
	
	@Test
	public void count() throws ClassNotFoundException, SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
	}
	
	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() throws ClassNotFoundException, SQLException {

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() throws ClassNotFoundException, SQLException{
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	/**
	 * User 오브젝트의 내용을 비교하는 검증 코드, 테스트에서 반복적으로 사용되므로 분리해놓았다.
	 * @param user1
	 * @param user2
	 */
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
		assertThat(user1.getEmail(), is(user2.getEmail()));
	}
	
	/**
	 * 4.2.4 기술에 독립적인 UserDao만들기
	 *  - 예외 확인 테스트
	 */
//	@Test(expected=DataAccessException.class)
	@Test
	public void duplicateKey() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		}catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);									// 수정할 사용자
		dao.add(user2);									// 수정하지 않을 사용자
		
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
//		IDE의 자동수정 기능과 테스트 코드 작성
//		단축키 : Ctrl + 1
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	@Autowired DefaultListableBeanFactory bf;
	@Test
	public void beans() {
		for (String n : bf.getBeanDefinitionNames()) {
			System.out.println(n + " / " + bf.getBean(n).getClass().getName());
		}
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.test.UserDaoTest");
	}
}
