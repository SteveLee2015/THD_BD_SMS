package thd.bd.sms.myRecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import thd.bd.sms.R;
import thd.bd.sms.bean.BDMsgBean;
import thd.bd.sms.view.LoadingDialog;


public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MainRecyclerAdapter";

    public static final int ITEM_TYPE_RECYCLER_WIDTH = 1000;
    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    public static final int ITEM_TYPE_ACTION_WIDTH_NO_SPRING = 1002;
    public static final int TYPE_FOOT = 1003;
    private List<BDMsgBean> mDatas;
    private Context mContext;
    private int po = -1;
    private boolean isEnd = false;
    //    private ItemTouchHelperExtension mItemTouchHelperExtension;
    private ItemTouchHelper mItemTouchHelperExtension;
    private LoadingDialog myProcessDialog;
    private OnItemClickListener onItemClickListener;

    public MainRecyclerAdapter(Context context, List<BDMsgBean> mDatas) {
        this.mDatas = mDatas;
        mContext = context;
//        LoadingDialog.Builder builder = new LoadingDialog.Builder(mContext);
//        myProcessDialog = builder.create();
//        myProcessDialog.show();
    }

    public MainRecyclerAdapter(Context context, List<?> mDatas, OnItemClickListener onItemClickListener) {
        /*this.mDatas = (List<BDMsgBean>) mDatas;
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;*/
    }

    public void setDatas(List<BDMsgBean> datas) {
//        mDatas.clear();
        mDatas.addAll(datas);
    }

    public void updateData(List<BDMsgBean> datas) {
//        setDatas(datas);
        notifyDataSetChanged();
    }

//    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
//        mItemTouchHelperExtension = itemTouchHelperExtension;
//    }

    public void setItemTouchHelperExtension(ItemTouchHelper itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);

        if (viewType == ITEM_TYPE_RECYCLER_WIDTH) {
            return new ItemSwipeWithActionWidthViewHolder(view);
        }

        if (viewType == TYPE_FOOT) {
            View view1 = getLayoutInflater().inflate(R.layout.headerview_moren, parent, false);
            return new FootViewHolder(view1);
        }
        if (viewType == ITEM_TYPE_RECYCLER_WIDTH) {
            view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);
            return new ItemViewHolderWithRecyclerWidth(view);
        }
        return new ItemSwipeWithActionWidthNoSpringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FootViewHolder) {
            return;
        }
        ItemBaseViewHolder baseViewHolder = (ItemBaseViewHolder) holder;
        baseViewHolder.bind(mDatas.get(position));
        baseViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Item Content click: #" + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                onItemClickListener.onItemClick(position);
                po = position;
                /*Intent detailIntent = new Intent(mContext, ZdDetailActivity.class);
                detailIntent.putExtra("proid", mDatas.get(position).getProid());
                mContext.startActivity(detailIntent);*/
            }
        });

        if (holder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) holder;
            viewHolder.mActionViewRefresh.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }
            );
            viewHolder.mActionViewDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*if (mDatas.get(position).getUserid().equals(MySharedPreference.getUserId()+"")) {
                                myProcessDialog.show();
                                deleteMyProject(HttpURLs.URL + HttpURLs.DELETE_PROJECT,
                                        mDatas.get(position).getProid(), MySharedPreference.getToken());
                            } else {
                                MyAlertDialog myDialog = new MyAlertDialog(mContext).builder();
                                myDialog.setMsg(mContext.getResources().getString(R.string.only_delete_yourself_project))
                                        .setTitle("")
                                        .setCancelable(false)
                                        .setPositiveButton(mContext.getResources().getString(R.string.ok), null)
                                        .show();
                            }*/
                        }
                    }
            );
        }
    }

    private void doDelete(int adapterPosition) {
        mDatas.remove(adapterPosition);
//        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

    public void move(int from, int to) {
        BDMsgBean prev = mDatas.remove(from);
        mDatas.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    @Override
    public int getItemViewType(int position) {
//        if (mDatas.get(position).position == 1) {
//        mDatas.get(position).
//        Log.e(TAG, "getItemViewType: mDatas.size() --------- " + mDatas.size());
//        Log.e(TAG, "getItemViewType: position --------- " + position);
        //此处是 为了过滤禁止左滑时增加的类型
        if (isEnd) {
            return ITEM_TYPE_ACTION_WIDTH;
        }
        if (position == mDatas.size() - 1) return TYPE_FOOT;
//        if (!mDatas.get(position).getUserId().equals(MySharedPreference.getUserId())) {//不是自己的不能滑动
//
//            return ITEM_TYPE_RECYCLER_WIDTH;
//
//        } else if (mDatas.get(position).getUserId().equals(MySharedPreference.getUserId()) &&
//                mDatas.get(position).getOperation().equals("0")) {//是自己的但是评估未完成的
//            return ITEM_TYPE_RECYCLER_WIDTH;
//
//        } else
        return ITEM_TYPE_ACTION_WIDTH_NO_SPRING;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ItemBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item_id, item_company, item_time, item_content;
        public View mViewContent;
        public View mActionContainer;

        public ItemBaseViewHolder(View itemView) {
            super(itemView);

            /*item_id = (TextView) itemView.findViewById(R.id.recycleview_item_id);
            item_company = (TextView) itemView.findViewById(R.id.recycleview_item_company);
            item_time = (TextView) itemView.findViewById(R.id.recycleview_item_time);
            item_content = (TextView) itemView.findViewById(R.id.recycleview_item_content);
            mViewContent = itemView.findViewById(R.id.fragment_myproject_all_item);*/
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);
        }

        public void bind(BDMsgBean dataBean) {
            /*if (!TextUtils.isEmpty(dataBean.getProiscomplete())) {
                item_id.setText(dataBean.getProiscomplete());
                item_id.setEms(1);
                switch (dataBean.getProiscomplete()) {
                    case "0":
                        item_id.setBackgroundColor(mContext.getResources().getColor(R.color.doing));
                        item_id.setText(mContext.getResources().getString(R.string.doing));
                        break;

                    case "1":
                        item_id.setBackgroundColor(mContext.getResources().getColor(R.color.done));
                        item_id.setText(mContext.getResources().getString(R.string.done));
                        break;

                    case "2":
                        item_id.setBackgroundColor(mContext.getResources().getColor(R.color.shelve));
                        item_id.setText(mContext.getResources().getString(R.string.shelve));
                        break;

                    case "3":
                        item_id.setBackgroundColor(mContext.getResources().getColor(R.color.not_receive));
                        item_id.setText(mContext.getResources().getString(R.string.not_receive));
                        break;
                }
            }else {
                item_id.setText("");
                item_id.setBackgroundColor(mContext.getResources().getColor(R.color.mn_text));
            }

            if (!TextUtils.isEmpty(dataBean.getProname())) {
                item_content.setText(dataBean.getProname());
            }else {
                item_content.setText(dataBean.getProname());
            }

            if (!TextUtils.isEmpty(dataBean.getProdeveloper())) {
                item_company.setText(dataBean.getProdeveloper());
            }else {
                item_company.setText(dataBean.getProname());
            }

            if (!TextUtils.isEmpty(dataBean.getProjssj())) {
                item_time.setText(dataBean.getProjssj());
            } else {
                item_time.setText("--");
            }*/
        }

        @Override
        public void onClick(View v) {

        }
    }


    public class ItemViewHolderWithRecyclerWidth extends ItemBaseViewHolder {

        View mActionViewDelete;

        public ItemViewHolderWithRecyclerWidth(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
        }

    }

    class ItemSwipeWithActionWidthViewHolder extends ItemBaseViewHolder {

        View mActionViewDelete;
        View mActionViewRefresh;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewRefresh = itemView.findViewById(R.id.view_list_repo_action_update);
        }

    }

    public class ItemSwipeWithActionWidthNoSpringViewHolder extends ItemSwipeWithActionWidthViewHolder {

        public ItemSwipeWithActionWidthNoSpringViewHolder(View itemView) {
            super(itemView);
        }

    }

    //装饰模式
    class FootViewHolder extends ItemBaseViewHolder {
        //加自己的功能
        private ImageView ivFoot;
        private TextView tvTitle;
        private ObjectAnimator mAni;

        public FootViewHolder(View itemView) {
            super(itemView);
            ivFoot = itemView.findViewById(R.id.iv);
            tvTitle = itemView.findViewById(R.id.tv);
            tvTitle.setText("正在加载...");
            startAnimator();
        }

        private void startAnimator() {
            mAni = ObjectAnimator.ofFloat(ivFoot, "rotation", 0, 360).setDuration(300);
            mAni.setInterpolator(new LinearInterpolator());
            mAni.setRepeatCount(ValueAnimator.INFINITE);
            mAni.setRepeatMode(ValueAnimator.RESTART);
            mAni.start();
        }
    }

    public void hideFootView(boolean isEnd) {
        this.isEnd = isEnd;
        notifyDataSetChanged();
    }

    /**
     * 删除重点项目
     *//*
    private void deleteMyProject(String url, String proId, String token) {

        Map<String, String> map = new HashMap<>();
        map.put("proId", proId);
        map.put("token", token);

        final String json = new JSONObject(map).toString();

        HttpUtils.doPost(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myProcessDialog != null && myProcessDialog.isShowing()) {
                            myProcessDialog.dismiss();
                        }
                        Tools.showToast(mContext, mContext.getResources().getString(R.string.load_failure));
                    }
                });
                Log.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, result);

                try {
                    Gson gson = new Gson();
                    final ProjectListBean projectListBean = gson.fromJson(result, ProjectListBean.class);

                    int status = projectListBean.getStatus();

                    if (status == 0) {

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (myProcessDialog != null && myProcessDialog.isShowing()) {
                                    myProcessDialog.dismiss();
                                }
                                doDelete(po);
                            }
                        });

                    } else {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (myProcessDialog != null && myProcessDialog.isShowing()) {
                                    myProcessDialog.dismiss();
                                }
                            }
                        });

                        if (status == 21315) {
                            Tools.againLogin(((Activity) mContext));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    Log.e(TAG, "onResponse:getMessage====== " + e.getMessage());

                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (myProcessDialog != null && myProcessDialog.isShowing()) {
                                myProcessDialog.dismiss();
                            }
                            Tools.showToast(((Activity) mContext), mContext.getResources().getString(R.string.service_exception));
                        }
                    });
                }
            }
        });
    }*/

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
