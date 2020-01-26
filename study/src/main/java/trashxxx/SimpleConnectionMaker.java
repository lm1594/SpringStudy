package trashxxx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * 토비의 스프링 1장
 * @author 이경민
 * @since 2020.01.19
 * @history
 * 1-3장 DAO의 확장
 * - 1.3.1 클래스의 분리 : 두 개의 관심사를 본격적으로 독립시키면서 동시에 손쉽게 확장할 수 있는 방법
 */
public class SimpleConnectionMaker {
	
	public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook?serverTimezone=UTC", "spring", "book");
		
		return c;
	}
	
}
