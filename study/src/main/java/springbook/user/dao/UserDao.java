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
 *  5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.2 사용자 수정 기능 추가 
 */
public interface UserDao {
	public void setDataSource(DataSource dataSource);
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	public void update(User user);
}
