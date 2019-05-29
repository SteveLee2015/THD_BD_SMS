package thd.bd.sms.bean;

/**
 * 定位设置
 * @author llg
 */
public class LocationSet {

	public static final int LOCATIONSET_TYPE_ONLY_ONE = 0;//单次定位
	public static final int LOCATIONSET_TYPE_CONTINUE = 1;//连续定位

	public static final String LOCATIONSET_STATUS_USING = "LOCATIONSET_STATUS_USING";//使用中ing
	public static final String LOCATIONSET_STATUS_NOT_USING = "LOCATIONSET_STATUS_NOT_USING";//没有使用

	private int id;


	/**
	 * 定位类型 单次/连续
	 */
	private int type;

	/**
	 * 状态 是否处于用户设置状态
	 */
	private String status;

	/**
	 * 定位频度
	 */
	private String locationFeq;
	/**
	 * 高程类型  0有测高  1无测高  2测高一  3测高二
	 */
	private String heightType;
    /**
     * 高程数据
     */
	private String heightValue;
	
	private String tianxianValue;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public String getTianxianValue() {
		return tianxianValue;
	}
	public void setTianxianValue(String tianxianValue) {
		this.tianxianValue = tianxianValue;
	}
	public String getLocationFeq() {
		return locationFeq;
	}
	public void setLocationFeq(String locationFeq) {
		this.locationFeq = locationFeq;
	}
	public String getHeightType() {
		return heightType;
	}
	public void setHeightType(String heightType) {this.heightType = heightType;}
	public String getHeightValue() {
		return heightValue;
	}
	public void setHeightValue(String heightValue) {
		this.heightValue = heightValue;
	}
}
