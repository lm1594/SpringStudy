package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 */
public abstract class UserDao {
	
	/**
	 * 1.3.1 클래스의 분리 : 두 개의 관심사를 본격적으로 독립시키면서 동시에 손쉽게 확장할 수 있는 방법
	 */
	private SimpleConnectionMaker simpleConnectionMaker;
	
	public UserDao() {
		simpleConnectionMaker = new SimpleConnectionMaker();
	}
	
	/**
	 * 사용자 생성
	 * @param user
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void add(User user) throws ClassNotFoundException, SQLException{
		Connection c = simpleConnectionMaker.makeNewConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	/**
	 * 사용자 조회
	 * @param id
	 * @return User
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = simpleConnectionMaker.makeNewConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
	/**
	 * 중복 코드의 메소드 추출 : Connection을 가져오는 중복 코드 분리 -> 리팩토링-메소드추출기법★
	 * @return Connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	/*private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook?serverTimezone=UTC", "spring", "book");
		
		return c;
	}*/
	
	/**
	* 1.2.3 DB 커넥션 만들기의 독립 / 상속을 통한 확장
	* @return
	* @throws ClassNotFoundException
	* @throws SQLException
	*/
	//public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	
}
