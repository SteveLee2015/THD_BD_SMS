package thd.bd.sms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.utils.Config;

/**
 * 友邻位置的Adapter
 * @author steve
 */
public class MsgUsalWordAdapter extends BaseAdapter {
	
	private ViewHolder viewHolder=null;
	private Context mContext=null;
	public List<Map<String,Object>> list=null;
	private LayoutInflater mInflater=null;
	int type;
	
	/**
	 * 构造方法
	 * @param mContext
	 * @param list
	 */
	public MsgUsalWordAdapter(Context mContext, List<Map<String,Object>> list, int type){
		this.mContext=mContext;
		this.list=list;
		this.type = type;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		if(contentView==null){
			viewHolder=new ViewHolder();
			mInflater=LayoutInflater.from(mContext);
			switch (type){
				case Config.FLAG_STATUS_CODE:{
					contentView=mInflater.inflate(R.layout.item_status_code_word, null);
					viewHolder.order_text=(TextView)contentView.findViewById(R.id.messge_order_text);
					break;
				}
				case Config.FLAG_USUAL_WORD:{
				contentView=mInflater.inflate(R.layout.item_message_word, null);
					break;
				}
				default:{
					contentView=mInflater.inflate(R.layout.item_message_word, null);
					break;
				}
			}
			viewHolder.word_text=(TextView)contentView.findViewById(R.id.messge_word_text);
			contentView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)contentView.getTag();
		}
		if(list!=null){
			viewHolder.word_text.setText(String.valueOf(list.get(position).get("MESSAGE_WORD_TEXT")));
			switch (type){
				case Config.FLAG_STATUS_CODE:{
				viewHolder.order_text.setText(String.valueOf(list.get(position).get("MESSAGE_WORD_TEXT_ORDER")));
					break;
				}
				case Config.FLAG_USUAL_WORD:{
					break;
				}
			}
		}
		return contentView;
	}
	
	public static class ViewHolder{
		TextView word_text;
		TextView order_text;
	}
}
