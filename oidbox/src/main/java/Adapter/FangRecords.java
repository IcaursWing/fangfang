package Adapter;

public class FangRecords {
    public FangRecords(String id, String time, String record) {
        this.id = id;
        this.time = time;
        this.record = "成绩:" + Float.valueOf(record) / 10 + "秒";
    }

    public String id;
    public String time;
    public String record;
}
