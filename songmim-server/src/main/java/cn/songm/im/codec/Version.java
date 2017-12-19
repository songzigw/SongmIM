package cn.songm.im.codec;

public enum Version {
    SONGM_IM_1("SongmIM1.0", (byte)1);
    
    private String name;
    private byte level;
    
    private Version(String name, byte level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public byte getLevel() {
        return level;
    }
    
    public static Version instance(byte level) {
        for (Version v : Version.values()) {
            if (v.level == level) {
                return v;
            }
        }
        throw new IllegalArgumentException(String.format("out of level: %d", level));
    }
}
