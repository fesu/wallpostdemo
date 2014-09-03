package com.example.wallpostdemo;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class WallPostItem {
  
        
    private final Integer profile_pic;
	private final String username;
	private final String useridtag;
	private final String post_time;
	private final String post_title;
	private final String post_pic;
	private final String total_love;
    
    
	public WallPostItem(Integer profile_pic,
			String username, String useridtag, String post_time, String post_title,
			String post_pic, String total_love) {
		super();
		
		this.profile_pic = profile_pic;
		this.username = username;
		this.useridtag = useridtag;
		this.post_time = post_time;
		this.post_title = post_title;
		this.post_pic = post_pic;
		this.total_love = total_love;
	}
	
    

	public String getUseridtag() {
		return useridtag;
	}



	public Integer getProfile_pic() {
		return profile_pic;
	}


	public String getUsername() {
		return username;
	}


	public String getPost_time() {
		return post_time;
	}


	public String getPost_title() {
		return post_title;
	}


	public String getPost_pic() {
		return post_pic;
	}


	public String getTotal_love() {
		return total_love;
	}


	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}

	

	
 	
    
    
}
