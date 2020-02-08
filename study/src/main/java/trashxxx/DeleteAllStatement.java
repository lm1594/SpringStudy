package trashxxx;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import springbook.user.dao.StatementStrategy;


/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.03
 * @history
 * 3장 템플릿
 *  3.2장 변하는 것과 변하지 않는 것 
 *   - 3.2.2 분리와 재사용을 위한 디자인 패턴 적용
 */
public class DeleteAllStatement implements StatementStrategy {

	@Override
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("delete from users");
		return ps;
	}

}