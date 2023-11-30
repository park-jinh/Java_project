package data;

import java.io.Serializable;

public class LoginData implements Serializable {

	public static final long serialVersionUID = 8518123841125L;
	
	private String id;
	private String password;
	private boolean isLogin;	// [true] login 시도, [false] 회원가입 시도
	
	public LoginData() {
	}
	public LoginData(String id, String password, boolean isLogin) {
		super();
		this.id = id;
		this.password = password;
		this.isLogin = isLogin;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	@Override
	public String toString() {
		return "LoginData [id=" + id + ", password=" + password + ", isLogin=" + isLogin + "]";
	}
	
}
