/**
 * GetPointInfo.java
 * 
 * DO NOT CHANGE THIS FILE
 * 
 * This interface is used to enable the Chart class to plot two sets of points:
 *     1) a set of points in a List
 *     2) a calculated set of point values without any words
 * 
 * We use an interface like this because we want to Abstract the values.
 * We do not want to create a List<> of theoretical values. Instead, we just
 * calculate those on the fly when they are created.
 */
package src.main.java;

public interface GetPointInfo {
    public String getWord(int index);
    public double getFreq(int index);
    public int getPointCount();
    public boolean hasWords();
}
