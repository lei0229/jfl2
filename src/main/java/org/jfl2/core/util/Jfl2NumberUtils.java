package org.jfl2.core.util;

/**
 */
public class Jfl2NumberUtils {
    /**
     * カーソル位置計算用 最小値は0
     *
     * @param newValue 新規値
     * @param maxValue 最大値+1
     * @param loop     ループする場合はtrue
     * @return
     */
    static public int loopValue(int newValue, int maxValue, boolean loop) {
        return loopValue(newValue, 0, maxValue, loop);
    }

    /**
     * カーソル位置計算用
     *
     * @param newValue 新規値
     * @param minValue 最小値
     * @param maxValue 最大値+1
     * @param loop     ループする場合はtrue
     * @return 範囲内の値
     */
    static public int loopValue(int newValue, int minValue, int maxValue, boolean loop) {
        if (loop) {
            if (newValue < minValue) {
                return maxValue-1;
            } else if (newValue >= maxValue) {
                return minValue;
            }
        } else {
            if (newValue < minValue) {
                return minValue;
            } else if (newValue >= maxValue) {
                return maxValue-1;
            }
        }
        return newValue;
    }
}
