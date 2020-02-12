package trashxxx;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import springbook.user.domain.User;


/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.03
 * @history
 * 3장 템플릿
 *  3.2장 변하는 것과 변하지 않는 것 
 *   - 3.2.2 분리와 재사용을 위한 디자인 패턴 적용
 *  3.3장 JDBC 전략 패턴의 최적화
 */
public class AddStatement implements StatementStrategy {

	User user;
	
	public AddStatement(User user) {
		this.user = user;
	}
	
	@Override
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		return ps;
	}

}
