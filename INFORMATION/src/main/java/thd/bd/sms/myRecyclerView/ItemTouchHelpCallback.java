package thd.bd.sms.myRecyclerView;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

public class ItemTouchHelpCallback extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlag = MyRecyclerView.ItemTouchHelper.UP|
//          MyRecyclerView.ItemTouchHelper.DOWN;
        //此处是处理滑动类型，如：LEFT是左滑

        int swipeFlag = ItemTouchHelper.LEFT;
        //此处判断是过滤掉类型为  ItemViewHolderWithRecyclerWidth   的viewholder
        /*if (viewHolder instanceof MyProjectRecyclerAdapter.ItemViewHolderWithRecyclerWidth) {
            return makeMovementFlags(0, swipeFlag);
        } */
        return makeMovementFlags(0, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        MyProjectRecyclerAdapter adapter = (MyProjectRecyclerAdapter) recyclerView.getAdapter();
//        adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dY != 0 && dX == 0) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        if (viewHolder instanceof MainRecyclerAdapter.ItemSwipeWithActionWidthNoSpringViewHolder) {
            MainRecyclerAdapter.ItemBaseViewHolder holder = (MainRecyclerAdapter.ItemBaseViewHolder) viewHolder;
            if (dX < -holder.mActionContainer.getWidth()) {
                dX = -holder.mActionContainer.getWidth();
            }
            holder.mViewContent.setTranslationX(dX);
//        } else if (viewHolder instanceof GeneralTableRecyclerAdapter.ItemBaseViewHolder) {
//            GeneralTableRecyclerAdapter.ItemBaseViewHolder holder = (GeneralTableRecyclerAdapter.ItemBaseViewHolder) viewHolder;
//            if (dX < -holder.mActionContainer.getWidth()) {
//                dX = -holder.mActionContainer.getWidth();
//            }
//            holder.mViewContent.setTranslationX(dX);
//        } else if (viewHolder instanceof MainRecyclerAdapter.ItemBaseViewHolder) {
//            MainRecyclerAdapter.ItemBaseViewHolder holder = (MainRecyclerAdapter.ItemBaseViewHolder) viewHolder;
//            if (dX < -holder.mActionContainer.getWidth()) {
//                dX = -holder.mActionContainer.getWidth();
//            }
//            holder.mViewContent.setTranslationX(dX);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

}
