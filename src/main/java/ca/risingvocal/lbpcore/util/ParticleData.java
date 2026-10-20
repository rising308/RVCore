package ca.risingvocal.lbpcore.util;

public class ParticleData {

    private int count;
    private double yOffset;

    private int r,g,b;
    private int r1,g1,b1;
    private double scale;

    private String name;

    private boolean enabled;

    public ParticleData(int r, int g, int b, int r1, int g1, int b1, int count, double yOffset, double scale, String name, boolean enabled) {
        this.count = count; this.yOffset = yOffset;

        this.r = r; this.g = g; this.b = b;
        this.r1 = r1; this.g1 = g1; this.b1 = b1;
        this.scale = scale;

        this.name = name;

        this.enabled = enabled;
    }

    public int getCount() { return count; }
    public double getYOffset() { return yOffset; }

    public int getR() { return r; }
    public int getG() { return g; }
    public int getB() { return b; }
    public int getR1() { return r1; }
    public int getG1() { return g1; }
    public int getB1() { return b1; }
    public double getScale() { return scale; }

    public String getName() { return name; }

    public boolean getEnabled() { return enabled; }

    public void setCount(int count) { this.count = count; }
    public void setYOffset(double yOffset) { this.yOffset = yOffset; }

    public void setR(int r) { this.r = r; }
    public void setG(int g) { this.g = g; }
    public void setB(int b) { this.b = b; }
    public void setR1(int r1) { this.r1 = r1; }
    public void setG1(int g1) { this.g1 = g1; }
    public void setB1(int b1) { this.b1 = b1; }
    public void setScale(double scale) { this.scale = scale; }

    public void setName(String name) { this.name = name; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
