package com.example.administrator.myproject.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.administrator.myproject.R;
import com.example.administrator.myproject.bean.JsonInfo;
import com.example.administrator.myproject.staticc.DefineStatic;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Administrator on 2016/9/28.
 */
public class ItemAdapter extends BaseAdapter {
    public JsonInfo info;
    public Context context;
    LruCache<String,BitmapDrawable> mluCache;
    public ItemAdapter(Context context,JsonInfo info) {
        int MaxMenmory= (int) Runtime.getRuntime().maxMemory();
        int Cache=MaxMenmory/8;
        mluCache=new LruCache<String,BitmapDrawable>(Cache){
            @Override
            protected int sizeOf(String key, BitmapDrawable drawable) {
               // return super.sizeOf(key, drawable);
                return drawable.getBitmap().getByteCount();
            }
        };
        this.info=info;
        this.context=context;
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */

    @Override
    public int getCount() {
        return info.infos.length;
    }
    @Override
    public Object getItem(int position) {
        return info.infos[position];
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder=new ViewHolder();
        if(convertView==null){
          View view= LayoutInflater.from(context).inflate(R.layout.list_item,null);
            convertView=view;
            holder.useriamge= (ImageView) convertView.findViewById(R.id.image);
            holder.username= (TextView) convertView.findViewById(R.id.text);
            holder.content= (TextView) convertView.findViewById(R.id.contenttext);
            holder.contentiamge= (ImageView) convertView.findViewById(R.id.contentImage);
            convertView.setTag(holder);
        }
        ViewHolder viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.content.getPaint().setFakeBoldText(true);
        viewHolder.username.setText(info.infos[position].username);
        viewHolder.useriamge.setImageResource(R.drawable.login_circle);
        viewHolder.content.setText("  " + info.infos[position].contenttext);

    //    viewHolder.contentiamge.setImageResource(R.drawable.login_circle);
        if(mluCache.get(info.infos[position].contentimageurl)!=null){
            viewHolder.contentiamge.setImageBitmap(mluCache.get(info.infos[position].contentimageurl).getBitmap());
        }else{
        MySyctask mySyctask=new MySyctask(viewHolder.contentiamge);
        mySyctask.execute(info.infos[position].contentimageurl);
        }
        return convertView;
    }
    public  class ViewHolder{
        public ImageView useriamge;
        public TextView  username;
        public TextView  content;
        public ImageView contentiamge;
    }




class MySyctask extends AsyncTask<String,Integer,Bitmap>{

   ImageView image;
    private Bitmap bitmap;
    private String link;
    public MySyctask(ImageView image) {
        this.image = image;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        BitmapDrawable drawable;
       link=params[0];
        link= DefineStatic.ip+"/MyProject"+link;
        Log.d("xxxxxxx", link);
        HttpURLConnection conn=null;
        try {
            URL url=new URL(link);
            conn= (HttpURLConnection) url.openConnection();
            Log.d("xxxxxxxx",""+conn.getResponseCode());
            if(conn.getResponseCode()==conn.HTTP_OK){
             bitmap= BitmapFactory.decodeStream(conn.getInputStream());
                drawable=new BitmapDrawable(bitmap);
                addBitMaoToCache(link,drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(conn!=null) {
                conn.disconnect();
            }
        }
        return bitmap;
    }
    private void addBitMaoToCache(String link, BitmapDrawable drawable) {
        mluCache.put(link,drawable);
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    //    super.onPostExecute(bitmap);
        image.setImageBitmap(bitmap);

    }
}

}