package springbook.user.service;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
 *    - 5.1.5 코드 개선
 *    	작성된 코드를 살펴볼 때는 다음과 같은 질문을 해볼 필요가 있다.
 *    		·코드에 중복된 부분은 없는가?
 *    		·코드가 무엇을 하는 것인지 이해하기 불편하지 않은가?
 *    		·코드가 자신이 있어야 할 자리에 있는가?
 *    		·앞으로 변경이 일어난다면 어떤 것이 있을 수 있고, 그 변화에 쉽게 대응할 수 있게 작성되어 있는가?
 *   5.2장 트랜잭션 서비스 추상화
 *    - 5.2.3 트랜잭션 동기화 : 트랜잭션 동기화 적용
 */
public class UserService {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	private UserDao userDao;
	
	public void setUserDao (UserDao userDao) {
		this.userDao = userDao;
	}
	
	// 리스트5-41. 트랜잭션 동기화 방식을 적용한 UserService
	// Connection을 생성할 때 사용할 DataSource를 DI받도록 한다. 
	private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @history
	 * 1. 사용자 레벨 업그레이드 메소드
	 * 2. 기본 작업 흐름만 남겨둔 upgradeLevels메소드
	 * 3. 리스트5-41. 트랜잭션 동기화 방식을 적용한 UserService
	 */
	public void upgradeLevels() throws Exception{
		TransactionSynchronizationManager.initSynchronization(); 	// 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
		
		// DataSourceUtils : DB커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
		Connection c = DataSourceUtils.getConnection(dataSource);	// DB커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
		c.setAutoCommit(false);
		
		try {
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			c.commit(); 		// 정상적으로 작업을 마치면 트랜잭션 커밋
		}catch (Exception e) {
			// 예외가 발생하면 롤백된다.
			c.rollback();
			throw e;
		}finally {
			// DataSourceUtils.releaseConnection : 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
			DataSourceUtils.releaseConnection(c, dataSource);
			
			// 동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
	}
	
	/**
	 * 업그레이드 가능 확인 메소드
	 */
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
			// 레벨별로 구분해서 조건을 판단한다.
			case BASIC	: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER	: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD	: return false;
			
			// 현재로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킨다. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다.
			default		: throw new IllegalArgumentException("Unknown Level: " + currentLevel);	
		}
	}
	
	/**
	 * 레벨 업그레이드 작업 메소드
	 */
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
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
