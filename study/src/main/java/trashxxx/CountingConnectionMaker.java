package trashxxx;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.01.24
 * @history
 *  - 1.7.4 의존관계 주입의 응용
 */
public class CountingConnectionMaker implements ConnectionMaker{
	
	int counter = 0;
	private ConnectionMaker realConnectionMaker;
	
	public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
		this.realConnectionMaker = realConnectionMaker;
	}
	
	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		this.counter++;
		return realConnectionMaker.makeConnection();
	}
	
	public int getCounter() {
		return this.counter;
	}
	
}
