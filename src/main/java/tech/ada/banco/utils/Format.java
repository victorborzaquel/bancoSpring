package tech.ada.banco.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Format {
    private Format() {
    }

    public static BigDecimal format(double value) {
        return format(BigDecimal.valueOf(value));
    }

    public static BigDecimal format(int value) {
        return format(BigDecimal.valueOf(value));
    }

    public static BigDecimal format(String value) {
        return format(BigDecimal.valueOf(Double.parseDouble(value)));
    }

    public static BigDecimal format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
