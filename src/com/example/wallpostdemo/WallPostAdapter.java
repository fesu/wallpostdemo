package com.example.wallpostdemo;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WallPostAdapter extends ArrayAdapter<String>{
	
	private final Activity context;	
	private final Integer[] profile_pic;
	private final String[] username;
	private final String[] useridtag;
	private final String[] post_time;
	private final String[] post_title;
	private final Integer[] post_pic;
	private final String[] total_love;
	
	public WallPostAdapter(Activity context, int resource,
			Integer[] profile_pic, String[] username, String[] useridtag, String[] post_time,
			String[] post_title, Integer[] post_pic, String[] total_love) {
		super(context, resource);
		this.context = context;
		this.profile_pic = profile_pic;
		this.username = username;
		this.useridtag = useridtag;
		this.post_time = post_time;
		this.post_title = post_title;
		this.post_pic = post_pic;
		this.total_love = total_love;
	}



@Override
public View getView(int position, View convertView, ViewGroup parent) {

	LayoutInflater inflater = context.getLayoutInflater();
	View v = inflater.inflate(R.layout.wall_post_items, null, true);
	
	RoundedImageView imgprofile = (RoundedImageView)v.findViewById(R.id.iv_profile_pic);
	imgprofile.setImageResource(profile_pic[position]);
	
	TextView tvusername = (TextView)v.findViewById(R.id.tv_username);
	tvusername.setText(username[position]);
	tvusername.setTag(useridtag[position]);
	
	TextView tvposttime = (TextView)v.findViewById(R.id.tv_post_time);
	tvusername.setText(post_time[position]);
	
	TextView tvposttitle = (TextView)v.findViewById(R.id.tv_post_title);
	tvusername.setText(post_title[position]);
	
	ImageView ivpostpic = (ImageView)v.findViewById(R.id.iv_post_pic);
	ivpostpic.setImageResource(post_pic[position]);
	
	TextView tvtotallove = (TextView)v.findViewById(R.id.tv_total_love);
	tvusername.setText(total_love[position]);
	
	
	
		
	return v;
}




}
