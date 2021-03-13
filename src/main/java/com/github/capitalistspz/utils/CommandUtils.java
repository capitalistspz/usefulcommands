package com.github.capitalistspz.utils;
import java.text.DecimalFormat;

public class CommandUtils {
    public static final String MOD_ID = "CommandUtils";
    public static final int SINGLE_FAIL = 0;
    public static final DecimalFormat df = new DecimalFormat("0.00");
    static {
        df.setMaximumFractionDigits(2);
    }
}