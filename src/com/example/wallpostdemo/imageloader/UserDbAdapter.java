package com.example.wallpostdemo.imageloader;

import java.math.BigInteger;

public class UserDbAdapter {
	
	private String user_id;		
	private String username;
	private String password;
	private String email;
	private String birthdate;
	private String gender;
	private String contact_number;
	private String profile_pic;
	private String about_me;
	private String country;
	private String love_rank;
	private String pic_count;
	private String total_love_count;
	private int Islogedin;
	
	

	public UserDbAdapter()
	{}
	
	public UserDbAdapter(String user_id, String username, String password,
			String email, String birthdate, String gender,
			String contact_number, String profile_pic, String about_me,
			String country, String love_rank, String pic_count,int Islogedin,
			String total_love_count) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.birthdate = birthdate;
		this.gender = gender;
		this.contact_number = contact_number;
		this.profile_pic = profile_pic;
		this.about_me = about_me;
		this.country = country;
		this.love_rank = love_rank;
		this.pic_count = pic_count;
		this.Islogedin = Islogedin;
		this.total_love_count = total_love_count;
	}

	
	public int getIslogedin() {
		return Islogedin;
	}

	public void setIslogedin(int islogedin) {
		Islogedin = islogedin;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getContact_number() {
		return contact_number;
	}

	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}

	public String getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}

	public String getAbout_me() {
		return about_me;
	}

	public void setAbout_me(String about_me) {
		this.about_me = about_me;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLove_rank() {
		return love_rank;
	}

	public void setLove_rank(String love_rank) {
		this.love_rank = love_rank;
	}

	public String getPic_count() {
		return pic_count;
	}

	public void setPic_count(String pic_count) {
		this.pic_count = pic_count;
	}

	public String getTotal_love_count() {
		return total_love_count;
	}

	public void setTotal_love_count(String total_love_count) {
		this.total_love_count = total_love_count;
	}

	
	

}
