package springbook.user.domain;

/**
 * 토비의 스프링 1-1장 초난감DAO
 * @author 이경민
 * @since 2020.01.17
 * @history
 *  - 2.3.3 포괄적인 테스트 - 생성자 추가
 * 5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.1 필드추가
 *    - 5.1.5 코드개선
 *   5.4장 메일 서비스 추상화
 *    - 5.4.1 JavaMail을 이용한 메일 발송 기능
 */
public class User {
	
	/**
	 * User의 레벨 업그레이드 작업용 메소드
	 */
	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null) {
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
		}else {
			this.level = nextLevel;
		}
	}
	
	public User(String id, String name, String password, Level level, int login, int recommend, String email) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
		this.email = email;
	}
	
	public User() {
		
	}
	
	private String id;
	private String name;
	private String password;
	private Level level;
	private int login;
	private int recommend;
	private String email;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Level getLevel() {
		return level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
