package trashxxx;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 * 1-3장 DAO의 확장
 * - 1.3.2 인터페이스의 도입 : 두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만들어주는 것
 */
public interface ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException, SQLException;
	
}
