package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
 *    - 5.2.4 트랜잭션 서비스 추상화 : 스프링의 트랜잭션 서비스 추상화, 트랜잭션 기술 설정의 분리
 *   5.4장 메일 서비스 추상화
 *    - 5.4.1 JavaMail을 이용한 메일 발송 기능
 *    - 5.4.3 테스트를 위한 서비스 추상화
 */
public class UserService {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	private UserDao userDao;
	public void setUserDao (UserDao userDao) {
		this.userDao = userDao;
	}
	
	// 리스트5-46 트랜잭션 매니저를 빈으로 분리시킨 UserService
	private PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {	// 프로퍼티 이름은 관례를 따라 transactionManager라고 만드는 것이 편리하다.
		this.transactionManager = transactionManager;
	}
	
	//리스트 5-53 메일 전송 기능을 가진 오브젝트를 DI 받도록 수정한 UserService
	private MailSender mailSender;
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @history
	 * 1. 사용자 레벨 업그레이드 메소드
	 * 2. 기본 작업 흐름만 남겨둔 upgradeLevels메소드
	 * 3. 리스트5-41. 트랜잭션 동기화 방식을 적용한 UserService
	 * 4. 리스트5-45. 스프링의 트랜잭션 추상화 API를 적용한 upgradeLevels()
	 */
	public void upgradeLevels() {
		
		// 트랜잭션 시작
		TransactionStatus status = 
					this.transactionManager.getTransaction(new DefaultTransactionDefinition());	// DI 받은 트랜잭션 매니저를 공유해서 사용한다. 멀티스레드 환경에서도 안전하다.
		
		try {
			// 트랜잭션안에서 진행되는 작업
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			this.transactionManager.commit(status);					// 트랜잭션 커밋
		}catch (RuntimeException e) {
			this.transactionManager.rollback(status);				// 트랜잭션 롤백
			throw e;
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
		// 리스트 5-49 레벨 업그레이드 작업 메소드 수정
		sendUpgradeEMail(user);
	}
	
	/**
	 * 1. 리스트 5-50 JavaMail을 이용한 메일 발송 메소드
	 * 2. 리스트 5-52 스프링의 MailSender를 이용한 메일 발송 메소드
	 * @param user
	 */
	private void sendUpgradeEMail(User user) {
		// MailMessage 인터페이스의 구현 클래스 오브젝트를 만들어 메일 내용을 작성한ㄷ.
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
	
		mailSender.send(mailMessage);
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
