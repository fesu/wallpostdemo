package com.example.wallpostdemo;

import java.io.InputStream;
import java.text.BreakIterator;
import java.util.List;

import com.example.wallpostdemo.imageloader.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
 
public class WallCustomListViewAdapter extends ArrayAdapter<WallPostItem> {
 
    Context context;
    public ImageLoader imageLoader;
    
 // For Listview Animation
    private int lastPosition = -1;
    
 
    public WallCustomListViewAdapter(Context context, int resourceId,
            List<WallPostItem> items) {
        super(context, resourceId, items);
        this.context = context;
        imageLoader=new ImageLoader(context);
    }
 
    /*private view holder class*/
    private class ViewHolder {        
        
    	RoundedImageView imgprofile;    	
    	TextView tvusername;    	
    	TextView tvposttime;    	
    	TextView tvposttitle;   	
    	ImageView ivpostpic;    	
    	TextView tvtotallove;
    	
        
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        
        WallPostItem wallItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wall_post_items, null);
            holder = new ViewHolder();
     
            
            holder.imgprofile = (RoundedImageView)convertView.findViewById(R.id.iv_profile_pic);
        	        	
            holder.tvusername = (TextView)convertView.findViewById(R.id.tv_username);
        	        	
            holder.tvposttime = (TextView)convertView.findViewById(R.id.tv_post_time);
        	        	
            holder.tvposttitle = (TextView)convertView.findViewById(R.id.tv_post_title);
        	        	
            holder.ivpostpic = (ImageView)convertView.findViewById(R.id.iv_post_pic);
        	        	
            holder.tvtotallove = (TextView)convertView.findViewById(R.id.tv_total_love);
        	
        	
            
            
            
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
            
    	
    	holder.imgprofile.setImageResource(wallItem.getProfile_pic());
    	//holder.ivpostpic.setImageResource(wallItem.getPost_pic());
    	/*new DownloadImageTask((ImageView)convertView.findViewById(R.id.iv_post_pic))
        .execute(wallItem.getPost_pic());*/
    	imageLoader.DisplayImage(wallItem.getPost_pic(), (ImageView)convertView.findViewById(R.id.iv_post_pic));
    	holder.ivpostpic.setTag(wallItem.getPost_pic());
    	
    	holder.tvusername.setText(wallItem.getUsername());
    	holder.tvusername.setTag(wallItem.getUseridtag());
    	holder.tvposttime.setText(wallItem.getPost_time());
    	holder.tvposttitle.setText(wallItem.getPost_title());
    	holder.tvtotallove.setText(wallItem.getTotal_love());
    	
    	
    	// animate the item
        /*TranslateAnimation animation = null;
        if (position == lastPosition) {
            animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(600);
            convertView.startAnimation(animation);
            lastPosition = position;
        }*/

    	
    	// Animate Listview Items
    	/*Animation animation;
    	if (position != lastPosition)
    	{
    		animation = AnimationUtils.loadAnimation(convertView.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
    	    convertView.startAnimation(animation);
    	    lastPosition = position;    	    
    	}  */
    	
    	
    	// On Click listner of Wall Items...
    	final Button btnloveit = (Button)convertView.findViewById(R.id.btn_loveit);
    	ImageView ivpic = (ImageView)convertView.findViewById(R.id.iv_post_pic);
    	final String postid = ((TextView)convertView.findViewById(R.id.tv_username)).getTag().toString();
    	
    	btnloveit.setOnClickListener(new View.OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			if(btnloveit.getTag() == null)
    			{
    				btnloveit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loveit_enable24, 0, 0, 0);
    				btnloveit.setTag("loved");
    			}
    			else
    			{
    				btnloveit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loveit_disabled24, 0, 0, 0);
    				btnloveit.setTag(null);
    			}
    			
    		}
    	});
	    
    	
		ivpic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intimageview = new Intent(getContext(), ImageViewerActivity.class);
				intimageview.putExtra("postid",postid);
				
				getContext().startActivity(intimageview);
				
			}
		});
 
        return convertView;
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