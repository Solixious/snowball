package dev.indian.snowball.rule;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class RuleParamUtil {
    public boolean hasAny(Map<String, Object> params, String... keys) {
        for (String k : keys) {
            if (params.containsKey(k) && params.get(k) != null) return true;
        }
        return false;
    }

    public String getString(Map<String, Object> params, String... keys) {
        for (String k : keys) {
            Object v = params.get(k);
            if (v != null) return v.toString();
        }
        throw new IllegalArgumentException("Missing required parameter. Tried keys: " + String.join(", ", keys));
    }

    public int getInt(Map<String, Object> params, String... keys) {
        for (String k : keys) {
            Object v = params.get(k);
            if (v != null) return Integer.parseInt(v.toString());
        }
        throw new IllegalArgumentException("Missing required integer parameter. Tried keys: " + String.join(", ", keys));
    }

    public double getDouble(Map<String, Object> params, String... keys) {
        for (String k : keys) {
            Object v = params.get(k);
            if (v != null) return Double.parseDouble(v.toString());
        }
        throw new IllegalArgumentException("Missing required numeric parameter. Tried keys: " + String.join(", ", keys));
    }
}

