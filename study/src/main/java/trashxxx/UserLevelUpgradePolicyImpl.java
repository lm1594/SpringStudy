package trashxxx;

import org.springframework.beans.factory.annotation.Autowired;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

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
 */
public class UserLevelUpgradePolicyImpl implements UserLevelUpgradePolicy {
	
	/**
	 * 업그레이드 정책 인터페이스
	 *  - 레벨을 업그레이드 하는 정책을 유연하게 변경할 수 있도록 개선
	 *  - ex) 연말 이벤트나 새로운 서비스 홍보기간 중에는 레벨 업그레이드 정책을 다르게 적용할 필요가 있을 수 있음.
	 *  그럴 때마다 중요한 사용자 관리 로직을 담은 UserService의 코드를 직접 수정했다가 이벤트 기간이 끝나면 다시 이전 코드로 수정한다는 것은 상당히 번거롭고 위험한 방법이다.
	 *  - 이런 경우 사용자 업그레이드 정책을 UserService에서 분리하는 방법을 고려해볼 수 있다.
	 *  분리된 업그레이드 정책을 담은 오브젝트는 DI를 통해 UserService에 주입한다. 스프링 설정을 통해서 평상시 정책을 구현한 클래스를 UserService에서 사용하게 하다가,
	 *  이벤트 때는 새로운 업그레이드 정책을 담은 클래스를 따로 만들어서 DI 해주면 된다. 이벤트가 끝나면 기존 업그레이드 정책 클래스로 다시 변경해준다.
	 */

	@Autowired
	private UserDao userDao;
	
	
	@Override
	public boolean canUpgradeLevel(User user) {
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

	@Override
	public void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
	}

}
