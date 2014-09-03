package com.example.wallpostdemo.imageloader;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHandler extends SQLiteOpenHelper  {

	//Database Info
		private static final String DB_NAME =  "iloveitdb";
		private static final String TABLE_NAME = "user_details";
		private static final int DB_VERSION = 1;
		
		//Table Keys
		public String user_id="user_id";		
		public String username="username";
		public String password="password";
		public String email="email";
		public String birthdate="birthdate";
		public String gender="gender";
		public String contact_number="contact_number";
		public String profile_pic="profile_pic";
		public String about_me="about_me";
		public String country="country";
		public String love_rank="love_rank";
		public String pic_count="pic_count";
		public String Islogedin="Islogedin";
		public String total_love_count="total_love_count";		
		
		
		
		public UserDBHandler(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + 
					user_id + " TEXT PRIMARY KEY, "+
					username +" TEXT NOT NULL, "+
					password + " TEXT, "+
					email + " TEXT, "+
					birthdate + " TEXT, "+
					gender + " TEXT, "+
					contact_number + " TEXT, "+
					profile_pic + " TEXT, "+
					about_me + " TEXT, "+
					country + " TEXT, "+
					love_rank + " TEXT, "+
					pic_count + " TEXT, "+
					Islogedin + " INTEGER, "+
					total_love_count + " TEXT);"	
								
			);
			
		}



		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			onCreate(db);
		}
		
		
		public void AddUserData(UserDbAdapter s)
		{						
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(user_id,s.getUser_id());
			values.put(username, s.getUsername());
			values.put(password, s.getPassword());
			values.put(email, s.getEmail());
			values.put(birthdate, s.getBirthdate());
			values.put(gender,s.getGender());
			values.put(contact_number,s.getContact_number());
			values.put(profile_pic,s.getProfile_pic());
			values.put(about_me,s.getAbout_me());
			values.put(country,s.getCountry());
			values.put(love_rank,s.getLove_rank());
			values.put(pic_count,s.getPic_count());
			values.put(total_love_count,s.getTotal_love_count());
			values.put(Islogedin,s.getIslogedin());
			
			
			db.insert(TABLE_NAME, null , values);
			db.close();
		}
		
		public List<UserDbAdapter> GetUserDetails(){
			
			List<UserDbAdapter> alluserdata =new ArrayList<UserDbAdapter>();
			String sql = "SELECT * FROM "+ TABLE_NAME;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			
			if(cursor.moveToFirst()){
				do {
					UserDbAdapter user = new UserDbAdapter();					
					
					user.setUser_id(cursor.getString(0));
					user.setUsername(cursor.getString(1));
					user.setPassword(cursor.getString(2));
					user.setEmail(cursor.getString(3));
					user.setBirthdate(cursor.getString(4));
					user.setGender(cursor.getString(5));
					user.setContact_number(cursor.getString(6));
					user.setProfile_pic(cursor.getString(7));
					user.setAbout_me(cursor.getString(8));
					user.setCountry(cursor.getString(9));
					user.setLove_rank(cursor.getString(10));
					user.setPic_count(cursor.getString(11));
					user.setIslogedin(cursor.getInt(12));
					user.setTotal_love_count(cursor.getString(13));
										
					alluserdata.add(user);
				}
				while(cursor.moveToNext());
			}
			
			return alluserdata;
		}
		
		public int deleteuser(int id) {
			
			SQLiteDatabase db = this.getWritableDatabase();
			return db.delete(TABLE_NAME,user_id + " = " + id , null);
		}
		
		
		public int UpdateIslogedIn(String uname,int islogedin)
		{
			SQLiteDatabase db =this.getWritableDatabase();
			ContentValues values =new ContentValues();
			values.put(Islogedin, islogedin);
			
			return db.update(TABLE_NAME, values, uname + " = ? ", new String[] {uname});
			
		}
		
		
		
		
		
		
}
