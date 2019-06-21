package thd.bd.sms.bean;

public class MyLocationBean {
    /*time : 2019-05-15 14:27:15
    locType : 161
    locType description : NetWork location successful!
    latitude : 40.087227
    longitude : 116.24794
    radius : 188.0
    CountryCode : 0
    Country : 中国
    citycode : 131
    city : 北京市
    District : 海淀区
    Street : 丰德东路
    addr : 中国北京市海淀区丰德东路19号
    UserIndoorState: -1
    Direction(not all devices have value): -1.0
    locationdescribe: 在激光显示产业园附近
    Poi: 激光显示产业园;永丰产业园;北京智慧农夫休闲农业规划设计院;北京爱博精电科技有限公司;龙苑大厦;
    operationers : 0
    describe : 网络定位成功*/

    private long time;
    private int locType;
    private String describe;
    private double latitude;
    private double longitude;
    private double radius;
    private String address;
    private double altitude;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double lontitude) {
        this.longitude = lontitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
