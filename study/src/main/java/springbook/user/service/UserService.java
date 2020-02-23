package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.23
 * @history
 *  5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.3 UserService.upgradeLevels()
 *    - 5.1.4 UserService.add()
 */
public class UserService {
	
	private UserDao userDao;
	
	public void setUserDao (UserDao userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * 사용자 레벨 업그레이드 메소드
	 */
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			Boolean changed = null;															// 레벨의 변화가 있는지를 확인하는 플래그
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {					// BASIC레벨 업그레이드 작업
				user.setLevel(Level.SILVER);
				changed = true;
			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {		// SILVER레벨 업그레이드 작업
				user.setLevel(Level.GOLD);
				changed = true;
			}else if (user.getLevel() == Level.GOLD) { 										// GOLD레벨은 변경이 일어나지 않는다.
				changed = false; 
			}else {
				changed = false;
			}
			
			if (changed) {																	// 레벨의 변경이 있는 경우에만 update()호출
				userDao.update(user);
			}
			
		}
	}
	
	/**
	 * 사용자 신규 등록 로직을 담은 add() 메소드
	 */
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
	
}
