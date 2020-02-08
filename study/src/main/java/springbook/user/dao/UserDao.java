package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;


/**
 * 토비의 스프링 1장
 * @author 이경민
 * @since 2020.01.17
 * @history
 * 1-1장 초난감DAO
 * 1-2장 DAO의 분리
 *  - 중복 코드의 메소드 추출 : Connection을 가져오는 중복 코드 분리 									-> 리팩토링-메소드추출기법★
 *  - DB 커넥션 만들기의 독립 / 상속을 통한 확장 : 추상메소드나 오버라이딩이 가능한 메소드로 만든 뒤 구현해서 사용하는 방법 	-> 템플릿 메소드 패턴&팩토리 메소드 패턴★
 * 1-3장 DAO의 확장
 *  - 1.3.1 클래스의 분리 : 두 개의 관심사를 본격적으로 독립시키면서 동시에 손쉽게 확장할 수 있는 방법
 *  - 1.3.2 인터페이스의 도입 : 두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만들어주는 것
 *  - 1.3.3 관계설정 책임의 분리 : serDaotest는 UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존관계를 설정하는 책임을 담당 -> UserDao에 있으면 안되는 다른 관심사항, 책임을 떠넘기는 작업
 * 1-7장 의존관계 주입(DI)
 *  - 1.7.5 메소드를 이용한 의존관계 주입
 * 1.8장 XML을 이용한 설정
 *  - 1.8.3 DataSource 인터페이스로 변환
 * 2.3장 개발자를 위한 테스팅 프레임워크 JUnit
 *  - 2.3.2 테스트 결과의 일관성
 *  - 2.3.3 포괄적인 테스트
 *3장 템플릿
 * 3.1장 다시보는 초난감DAO
 *  - 3.1.1 예외처리 기능을 갖춘DAO 
 * 3.2장 변하는 것과 변하지 않는 것
 *  - 3.2.2 분리와 재사용을 위한 디자인 패턴 적용
 * 3.3장 JDBC 전략 패턴의 최적화
 *  - 3.3.1 전략 클래스의 추가 정보 : add()의 User객체
 *  - 3.3.2 전략과 클라이언트의 동거 : 두가지 문제(1.클래스 파일의 개수가 늘어난다 / 2.추가정보가 필요할 경우 오브젝트를 전달받는 생성자와 이를 저장할 인스턴스 변수를 번거롭게 만들어야 한다.) 해결(로컬 클래스, 익명 내부 클래스)
 * 3.4장 컨텍스트와 DI
 *  3.4.1 JdbcContext의 분리
 *  3.4.2 JdbcContext의 특별한 DI : 코드를 이용하는 DI
 *  	- 장점 : JdbcContext가 UserDao의 내부에서 만들어지고 사용되면서 그 관계를 외부에는 드러내지 않는다	<-> 빈을 이용한 DI는 오브젝트 사이의 실제 의존관계가 설정파일에 명확하게 드러난다.
 *  	- 단점 : JdbcContext를 여러 오브젝트가 사용하더라도 싱글톤으로 만들 수 없고 DI 작업을 위한 부가적인 코드가 필요하다. <-> 빈을 이용한 DI는 DI의 근본적인 원칙에 부합하지 않는 구체적인 클래스와의 관계가 설정에 직접 노출된다
 */
public class UserDao {
	
	private DataSource dataSource;
	private JdbcContext jdbcContext;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource);
		
		this.dataSource = dataSource;
	}
	
	/**
	 * 사용자 생성
	 * @param user
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void add(User user) throws ClassNotFoundException, SQLException{
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
				@Override
				public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				
				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				
				return ps;
			}
		});
	}
	
	/**
	 * 사용자 조회
	 * @param id
	 * @return User
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		User user = null;
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		
		rs.close();
		ps.close();
		c.close();
		
		if(user == null) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return user;
	}
	
	/**
	 * 모든 데이터 삭제
	 * @throws SQLException
	 */
	public void deleteAll() throws SQLException {
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("delete from users");
				return ps;
			}
		});
	}
	
	/**
	 * 카운트 가져오기
	 * @throws SQLException
	 */
	public int getCount() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
		
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}catch(SQLException e) {
			throw e;
		}finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(SQLException e) {
				
				}
			}
			
			if(ps != null) {
				try {
					ps.close();
				}catch(SQLException e) {
				
				}
			}
			
			if(c != null) {
				try {
					c.close();
				}catch(SQLException e) {
				
				}
			}
		}
		
		
	}
	
}
