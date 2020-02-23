package springbook.user.domain;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.15
 * @history
 *  5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.1 필드추가
 *    - 5.1.5 코드 개선
 */
public enum Level {
	// 이늄 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가한다.
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);			// 세 개의 이늄 오브젝트 정의
	
	private final int value;
	private final Level next;									// 다음 단계의 레벨 정보를 스스로 갖고 있도록 Level 타입의 next 변수를 추가한다.
	
	Level(int value, Level next) {								// DB에 저장할 값을 넣어줄 생성자를 만들어둔다.
		this.value 	= value;
		this.next 	= next;
	}
	
	public int intValue() {										// 값을 가져오는 메소드
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
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
