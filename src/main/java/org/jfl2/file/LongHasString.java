package org.jfl2.file;

import lombok.EqualsAndHashCode;

/**
 * toStringで特定の文字列を返すLongオブジェクト
 */
@EqualsAndHashCode(callSuper=false)
public class LongHasString extends Number implements Comparable<LongHasString> {
    final long longValue;
    final String strValue;

    /**
     * constructor
     *
     * @param longValue
     */
    public LongHasString(long longValue) {
        this(longValue, null);
    }

    /**
     * constructor
     *
     * @param longValue
     * @param strValue
     */
    public LongHasString(long longValue, String strValue) {
        this.longValue = longValue;
        this.strValue = strValue;
    }

    @Override
    public String toString() {
        if (strValue == null) {
            return String.valueOf(longValue);
        }
        return strValue;
    }

    @Override
    public int intValue() {
        return (int) longValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public float floatValue() {
        return (float) longValue;
    }

    @Override
    public double doubleValue() {
        return (double) longValue;
    }

    /**
     * compare
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(LongHasString o) {
        return Long.compare(longValue, o.longValue());
    }
}
