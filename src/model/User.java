package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;

import sql.DbManager;

public class User {

	private int id;
	private String username;
	private String email;
	private String password;
	private String salt;
	private int person_group_id;
	
	public User(){
		
	}
	
	public User(String username, String email, String password, int person_group_id){
		this.id = 0;
		setUsername(username).
		setEmail(email).
		setPassword(password).
		setPersonGroupId(person_group_id);
	}
	
	public User setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public User setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public User setPassword(String password) {
		this.salt = BCrypt.gensalt();
		this.password = BCrypt.hashpw(password, salt);
		return this;
	}
	public int getId() {
		return id;
	}
	public User setPersonGroupId(int id){
		this.person_group_id = id;
		return this;
	}
	public int getPersonGroupId(){
		return this.person_group_id;
	}
	@Override
	public String toString(){
		return "id: "+this.id+" username: "+this.username+" email:"+this.email+" password:" + this.password;
	}
	
	
	// non-static DB methods
	public void saveToDB(){
		if(this.id==0){
			try {
				String generatedColumns[] = { "ID" };
				PreparedStatement stmt = DbManager.getPreparedStatement("INSERT INTO users(username,email,password,salt) VALUES (?,?,?,?)",generatedColumns);
				stmt.setString(1, this.username);
				stmt.setString(2, this.email);
				stmt.setString(3, this.password);
				stmt.setString(4, this.salt);
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys(); 
				if (rs.next()) {
					this.id = rs.getInt(1);
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}else{
			try{
				PreparedStatement stmt = DbManager.getPreparedStatement("UPADTE users SET username = ?, email = ?, password = ?, salt = ? WHERE id = ?");
				stmt.setString(1, this.username);
				stmt.setString(2, this.email);
				stmt.setString(3, this.password);
				stmt.setString(4, this.salt);
				stmt.setInt(5, this.id);
				stmt.executeUpdate();
			}catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	public void delete(){
		String sql = "DELETE FROM users WHERE id= ?";
		try{
			if(this.id!=0){
				PreparedStatement stmt = DbManager.getPreparedStatement(sql);
				stmt.setInt(1, this.id); 
				stmt.executeUpdate();
				this.id=0;
			}
		}catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	// static DB methods
	public static ArrayList<User> loadAll(){
		try {
			ArrayList<User> users = new ArrayList<User>();
			String sql = "SELECT * FROM users"; 
			PreparedStatement stmt = DbManager.getPreparedStatement(sql); 
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				User loadedUser = new User();
				loadedUser.id = resultSet.getInt("id"); 
				loadedUser.username = resultSet.getString("username"); 
				loadedUser.password = resultSet.getString("password"); 
				loadedUser.email = resultSet.getString("email"); 
				loadedUser.salt = resultSet.getString("salt"); 
				users.add(loadedUser);
			}
			return users;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} 
		
		return null;
	}
	public static User loadById(int id){
		try { 
			String sql = "SELECT * FROM users where id=?";
			PreparedStatement stmt = DbManager.getPreparedStatement(sql); 
			stmt.setInt(1, id);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				User loadedUser = new User();
				loadedUser.id = resultSet.getInt("id"); 
				loadedUser.username = resultSet.getString("username"); 
				loadedUser.password = resultSet.getString("password"); 
				loadedUser.email = resultSet.getString("email"); 
				loadedUser.salt = resultSet.getString("salt"); 
				return loadedUser;
			}
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} 
		
		return null;
	}
	
}
