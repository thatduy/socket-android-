package studio.uit.vdt.socketsendfile.model;

/**
 * Created by ASUS on 31-Mar-18.
 */

public class SendModel {
    private int count = 0;
    private String ip = "";
    private String name = "";
    private String date = "";
    private String path = "";

    public int getCount() {
        return count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SendModel(int count, String ip, String name, String date, String path) {

        this.count = count;
        this.ip = ip;
        this.name = name;
        this.date = date;
        this.path = path;
    }

}
