package springbook.user.dao;

import java.util.List;

import javax.sql.DataSource;

import springbook.user.domain.User;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.13
 * @history
 *  4장 예외
 *   4.2장 예외전환
 *    - 4.2.4 기술에 독립적인 UserDao만들기
 */
public interface UserDao {
	public void setDataSource(DataSource dataSource);
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
}
