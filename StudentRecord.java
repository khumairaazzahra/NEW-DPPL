
public class StudentRecord {
    private String nim;
    private String name;
    private String status;

    public StudentRecord(String nim, String name) {
        this.nim = nim;
        this.name = name;
        this.status = "Belum Hadir";
    }

    public String getNim() { return nim; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
