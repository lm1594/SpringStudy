package springbook.user.dao;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.01.21
 * @history
 *  - 1.4.1 오브젝트 팩토리
 *  - 1.4.2 오브젝트 팩토리의 활용 : new DConnectionMaker()가 중복되서 나오게 될 경우에 대한 해결
 */
public class DaoFactory {
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}
	
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
}
