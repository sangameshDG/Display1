package admin;

import javafx.beans.property.SimpleStringProperty;

public class User {

	private final SimpleStringProperty id;
	private final SimpleStringProperty fullName;
	private final SimpleStringProperty username;
	private final SimpleStringProperty password;
	private final SimpleStringProperty type;

	public User() {
		this(null, null, null, null, null);
	}

	public User(String id, String fullName, String username, String password,
			String type) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.id = new SimpleStringProperty(id);
		this.fullName = new SimpleStringProperty(fullName);
		this.type = new SimpleStringProperty(type);
	}

	public String getId() {
		return id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}

	public String getFullName() {
		return fullName.get();
	}

	public void setFullName(String name) {
		this.fullName.set(name);
	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	@Override
	public String toString() {
		return "User: " + id + "\t" + fullName + "\t" + username + "\t"
				+ password + "\t" + type;
	}

}
