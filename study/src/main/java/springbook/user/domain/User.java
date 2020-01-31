package springbook.user.domain;

/**
 * 토비의 스프링 1-1장 초난감DAO
 * @author 이경민
 * @since 2020.01.17
 * @history
 *  - 2.3.3 포괄적인 테스트 - 생성자 추가
 */
public class User {
	
	public User(String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public User() {
		
	}
	
	private String id;
	private String name;
	private String password;
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
	
}
