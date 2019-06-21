package thd.bd.sms.bean;

/**
 * 报告设置
 * @author llg
 */
public class ReportSet {
	
	public static final String REPORTSET_STATE = "0";
	public static final String REPORTSET_RN = "1";
	public static final String REPORTSET_RD = "2";
	public static final String REPORTSET_SOS = "3";



	//默认频率
	public static final String REPORTSET_DEFAULT_REPORT_HZ = "0";
	//默认天线高
	public static final String REPORTSET_DEFAULT_TIANXIANVALUE = "40";
	
	private int id;
	
	/**
	 * 报告类型  0状态报告  1rn位置报告  2rd位置报告
	 */
	private String reportType;
	
	/**
	 * 报告平台号
	 */
	private String reportNnm;

	public String getReportSOSContent() {
		return reportSOSContent;
	}

	public void setReportSOSContent(String reportSOSContent) {
		this.reportSOSContent = reportSOSContent;
	}

	/**
	 * 报告内容
	 */
	private String reportSOSContent;

	/**
	 * 报告频度
	 */
	private String reportHz;
    
	/**
	 * 天线高度
	 */
	private String tianxianValue;

	private int statusCode = 0;
	
	public ReportSet() {
		
	}
	
	public ReportSet(String reportType, String reportNnm, String reportHz,
                     String tianxianValue) {
		super();
		this.reportType = reportType;
		this.reportNnm = reportNnm;
		this.reportHz = reportHz;
		this.tianxianValue = tianxianValue;
		this.statusCode = 0;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getId() {
		return id;
	}

	public String getReportType() {
		return reportType;
	}

	public String getReportNnm() {
		return reportNnm;
	}

	public String getReportHz() {
		return reportHz;
	}

	public String getTianxianValue() {
		return tianxianValue;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setReportNnm(String reportNnm) {
		this.reportNnm = reportNnm;
	}

	public void setReportHz(String reportHz) {
		this.reportHz = reportHz;
	}

	public void setTianxianValue(String tianxianValue) {
		this.tianxianValue = tianxianValue;
	}

	@Override
	public String toString() {
		return "ReportSet [id=" + id + ", reportType=" + reportType
				+ ", reportNnm=" + reportNnm + ", reportHz=" + reportHz
				+ ", tianxianValue=" + tianxianValue + "]";
	}
}
