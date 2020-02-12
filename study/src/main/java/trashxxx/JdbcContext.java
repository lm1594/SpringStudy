package trashxxx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.4장 컨텍스트와 DI
 */
public class JdbcContext {
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 메소드로 분리한 try/catch/finally 컨텐스트 코드
	 * @param stmt
	 * @throws SQLException
	 */
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = dataSource.getConnection();
			
			ps = stmt.makePreparedStatement(c);
			
			ps.executeUpdate();
		}catch(SQLException e) {
			throw e;
		}finally {
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
	
	/**
	 * 변하지않는 부분을 분리시킨 deleteAll() 메소드
	 */
	public void executeSql(final String query) throws SQLException {
		workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement(query);
				return ps;
			}
		});
	}
}
