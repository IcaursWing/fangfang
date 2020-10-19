package MusicUtil;

public class Yin {
    int pitch;
    double start;
    double end;
    int velocity;

    public Yin(int pitch, double start, double end, int velocity) {
        this.pitch = pitch;
        this.start = start;
        this.end = end;
        this.velocity = velocity;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
