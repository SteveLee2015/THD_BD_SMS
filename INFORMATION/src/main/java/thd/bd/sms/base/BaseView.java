package thd.bd.sms.base;

/**
 * @author lerry
 */
public interface BaseView {

    /**
     * 显示加载中
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 数据获取失败
     * @param throwable
     */
    void onError(Throwable throwable);


}
