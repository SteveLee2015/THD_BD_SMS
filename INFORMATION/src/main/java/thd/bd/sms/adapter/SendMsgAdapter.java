package thd.bd.sms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import thd.bd.sms.R;
import thd.bd.sms.bean.Item;
import thd.bd.sms.utils.Utils;

import com.readystatesoftware.viewbadger.BadgeView;

/**
 * 短信的Adapter
 * @author lerry
 */
public class SendMsgAdapter extends BaseAdapter {

	private ViewHolder viewHolder=null;
	private Context mContext=null;
	private LayoutInflater mInflater=null;
	public List<Item> items;
	public List<Item> toRemoveItems;
	public View ll_checked_title;
	
	

	public OnCheckBoxClickLinstener onCheckBoxClickLinstener;
	
	

	public void setOnCheckBoxClickLinstener(OnCheckBoxClickLinstener onCheckBoxClickLinstener) {
		this.onCheckBoxClickLinstener = onCheckBoxClickLinstener;
	}
	
	/**
	 * 构造方法
	 * @param mContext
	 * @param items
	 */
	public SendMsgAdapter(Context mContext, List<Item> items){
          this.mContext=mContext;
          this.items=items;
          this.mInflater=LayoutInflater.from(mContext);
	}
	/**
	 * 获得总长度
	 */
	@Override
	public int getCount() {
		return items.size();
	}
     
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
    /**
     * 每个Item的View
     */
	@Override
	public View getView(final int position, View contentView, ViewGroup parent) {
		
		Item item = items.get(position);
		String flag_not_drawable = item.message_flag_not_drawable;
		
		if(contentView==null){
				viewHolder=new ViewHolder();
				contentView=mInflater.inflate(R.layout.item_bdsend_msg, null);
				viewHolder.sendId=(TextView)contentView.findViewById(R.id.msg_send_id);
				viewHolder.content=(TextView)contentView.findViewById(R.id.msg_send_content);
				viewHolder.date=(TextView)contentView.findViewById(R.id.msg_send_date);
				viewHolder.flag=(TextView)contentView.findViewById(R.id.msg_flag_icon);
				viewHolder.cbx_checked=(CheckBox)contentView.findViewById(R.id.tv_cancel);
				contentView.setTag(viewHolder);
		}else{
				viewHolder=(ViewHolder)contentView.getTag();
		}
		//viewHolder.sendId.setText(String.valueOf(item.send_id));
		Log.d("SendMsgAdapter ","item.send_name="+item.send_name);
		viewHolder.sendId.setText(String.valueOf(item.send_name)+"("+String.valueOf(item.send_id)+")");
		viewHolder.content.setText(String.valueOf(item.send_content));
		viewHolder.date.setText(String.valueOf(item.send_date));
		//不做处理
		//viewHolder.flag.setBackgroundDrawable((Drawable)item.message_flag);
		
		// 1   2   3
		if ("3".equals(flag_not_drawable)) {
			
			BadgeView mBadgeView = new BadgeView(mContext,viewHolder.flag);
			mBadgeView.setTextColor(Color.WHITE);
			mBadgeView.setBackgroundColor(Color.RED);
			mBadgeView.setTextSize(8);
			mBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
			mBadgeView.setText("新消息");
			mBadgeView.show();
			//mBadgeView.hide();
		}
		

		//设置 cbx_checked
		viewHolder.cbx_checked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				items.get(position).checked  =isChecked;
				

				if (onCheckBoxClickLinstener!=null) {
					onCheckBoxClickLinstener.onCheckBoxClicked(position,items);
				}
				
			}
		});
		viewHolder.cbx_checked.setChecked(items.get(position).checked);
//		
		final CheckBox checkbox_xy = (CheckBox)contentView.findViewById(R.id.tv_cancel);
		if (Utils.checkBoxSelect) {
			checkbox_xy.setVisibility(View.VISIBLE);
		}else {
			checkbox_xy.setVisibility(View.GONE);
		}
		return contentView;
	}
	
	/**
	 * 删除List中所有的短信内容
	 */
	public void deleteAllMessageData(){
		if(items!=null){
		   items.clear();
		}
	}
	
	
	public static class ViewHolder{
	    /**
	     * 发送ID
	     */
		TextView sendId;
		/**
		 * 信息大小
		 */
	    TextView size;
		/**
		 * 内容
		 */
		TextView content;
		/**
		 * 日期
		 */
		TextView date;
		 /**
		   * 图标
		   */
		 TextView flag;
		  /**
		   * 向下的图标
		   * */
		  ImageView downIcon;
		  /**
		   * 转发短信
		   */
		  LinearLayout zhuanFaMsg;
		 /**
		  * 删除短信
		  */
		  LinearLayout delMsg;
		  /**
		   * 是否可见
		   */
		  LinearLayout visibleOper;
		  /**
		   * checkedbox
		   */
		  CheckBox cbx_checked;
		  
		  
	}
	
	
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public interface OnCheckBoxClickLinstener {
		
		void onCheckBoxClicked(int positon, List<Item> items);
	}
	

}
