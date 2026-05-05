package tetris.audio;


import java.util.Random;

public enum SoundType {
    COMBO_1("Combo1.wav", false),
    COMBO_2("Combo2.wav", false),
    COMBO_3("Combo3.wav", false),
    COMBO_4("Combo4.wav", false),
    COMBO_5("Combo5.wav", false),
    COMBO_6("Combo6.wav", false),
    COMBO_7("Combo7.wav", false),
    LINE_REMOVE_1("Remove1Line.wav", false),
    LINE_REMOVE_2("Remove2Line.wav", false),
    LINE_REMOVE_3("Remove3Line.wav", false),
    LINE_REMOVE_4("Remove4Line.wav", false),
    T_SPIN("Remove4Line.wav", false), // TSpin and Remove 4 lines are the same sound
    HOLD("Hold.wav", false),
    SPACE("Space.wav", false),
    KO1("KO1.wav", false),
    KO2("KO2.wav", false),
    WET_FART("Wet_Fart.wav", false),
    TIME_WARNING("Time_Warning.wav", false),
    TIMES_UP("Times_Up.wav", false),
    BLITZ_OST("OSTShorten.wav", true);

    private final String fileName;
    private final boolean isBgm;

    SoundType(String fileName, boolean isBgm) {
        this.fileName = fileName;
        this.isBgm = isBgm;
    }

    public String getFileName() {
        return this.fileName;
    }
    public boolean isBgm() {
        return this.isBgm;
    }

    public static SoundType fromLinesRemoved(int lines) {
        return switch (lines) {
            case 1 -> SoundType.LINE_REMOVE_1;
            case 2 -> SoundType.LINE_REMOVE_2;
            case 3 -> SoundType.LINE_REMOVE_3;
            case 4 -> SoundType.LINE_REMOVE_4;
            default -> null;
        };
    }

    public static SoundType fromCombo(int combo) {
        return switch (combo) {
            case 1 -> SoundType.COMBO_1;
            case 2 -> SoundType.COMBO_2;
            case 3 -> SoundType.COMBO_3;
            case 4 -> SoundType.COMBO_4;
            case 5 -> SoundType.COMBO_5;
            case 6 -> SoundType.COMBO_6;
            default -> (combo >= 7) ? SoundType.COMBO_7 : null; // combo >= 7 uses the same sound, combo < 1 return null
        };
    }
}
