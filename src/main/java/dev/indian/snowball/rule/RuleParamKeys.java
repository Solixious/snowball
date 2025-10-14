package dev.indian.snowball.rule;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RuleParamKeys {
    // Common keys
    public static final String PERIOD = "period";
    public static final String OPERATOR = "operator";
    public static final String COMPARISON = "comparison";
    public static final String OP = "op";
    public static final String VALUE = "value";
    public static final String THRESHOLD = "threshold";

    // SMA specific aliases
    public static final String SMA_PERIOD = "smaPeriod";
    public static final String LEFT_PERIOD = "leftPeriod";
    public static final String RIGHT_PERIOD = "rightPeriod";
    public static final String OTHER_PERIOD = "otherPeriod";
    public static final String COMPARE_TO_PERIOD = "compareToPeriod";
    public static final String COMPARE_TO_SMA_PERIOD = "compareToSmaPeriod";

    // EMA specific aliases
    public static final String EMA_PERIOD = "emaPeriod";

    // RSI specific aliases
    public static final String RSI_PERIOD = "rsiPeriod";

    // Grouped aliases (varargs-friendly)
    public static final String[] OPERATOR_KEYS = new String[] { OPERATOR, COMPARISON, OP };
    public static final String[] THRESHOLD_KEYS = new String[] { VALUE, THRESHOLD };

    public static final String[] SMA_LEFT_PERIOD_KEYS = new String[] { PERIOD, SMA_PERIOD, LEFT_PERIOD };
    public static final String[] SMA_RIGHT_PERIOD_KEYS = new String[] { OTHER_PERIOD, COMPARE_TO_PERIOD, RIGHT_PERIOD, COMPARE_TO_SMA_PERIOD };

    public static final String[] EMA_PERIOD_KEYS = new String[] { PERIOD, EMA_PERIOD };
    public static final String[] RSI_PERIOD_KEYS = new String[] { PERIOD, RSI_PERIOD };
}

