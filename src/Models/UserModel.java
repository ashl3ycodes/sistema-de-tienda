package Models;

public class UserModel {
	private String username;
	private final String role;

	public UserModel(String username, String role) {
		this.username = username;
		this.role = role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAdmin() {
		return "ADMIN".equals(role);
	}
}