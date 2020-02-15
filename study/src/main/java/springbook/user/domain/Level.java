package springbook.user.domain;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.15
 * @history
 *  5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.1 필드추가
 */
public enum Level {
	BASIC(1), SILVER(2), GOLD(3);			// 세 개의 이늄 오브젝트 정의
	
	private final int value;
	
	Level(int value) {						// DB에 저장할 값을 넣어줄 생성자를 만들어둔다.
		this.value = value;
	}
	
	public int intValue() {					// 값을 가져오는 메소드
		return value;
	}
	
	public static Level valueOf(int value) {
		switch(value) {
		case 1: return BASIC;
		case 2: return SILVER;
		case 3: return GOLD;
		default:throw new AssertionError("Unknown value : " + value);
		}
 	}
}
