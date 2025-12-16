/**
 * Chart.java
 * 
 * This class graphs one plot containint two lists of points for the Zipf Sequence:
 *     1) List<WordFreq> that is set by the client
 *     2) Theoretical Zipf Frequency values that are calculated
 * 
 * These two lists are abstracted behind the GetPointInfo interface.
 * This class implements the GetPointInfo in two ways:
 *     1) via implements keyword: This class implements the methods
 *        and reveals the points provided in the List<WordFreq>
 *     2) Anonymous Inner Class: an instance field, zipfInfo, implements
 *        the interface and is used to plot the theoretical Zipf values. y=1/x
 * 
 * The user can interact with the graph in three ways:
 *     1) Double click: Zooms into the set of points around where clicked
 *     2) Right click: Unzooms all the way out to the original points
 *     3) Left-Right arrow keys: Pages across the values
 */

package src.main.java;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.JPanel;
import java.util.List;

public class Chart extends JPanel implements GetPointInfo {

    private static int BOUNDARY = 50;

    // This List is provided by the client
    private List<WordFreq> wordFreq;

    // These are used as the starting/ending index values in the chart
    private int start = 0;
    private int end = 100;

    public Chart() {
        // add click handler to zoom in
        // Save a reference to ourself as a final. The value, self, is leveraged in the
        // Anonymous inner class to be "this" chart objects.
        // In the Anonymous Inner Class, the "this" keyword is the MouseAdapter object.
        // "this" is the Chart object
        final Chart self = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    // zoom out! Reset the start/end values.
                    self.start = 0;
                    self.end = self.wordFreq == null ? 100 : self.wordFreq.size() - 1;

                    // don't forget to trigger a repaint!
                    self.repaint();
                } else if (me.getClickCount() > 1) {
                    // The user double-clicked. Zoom in!
                    // Set new start/end values.
                    double x = me.getX() - BOUNDARY;
                    double width = Main.WIDTH - 2 * BOUNDARY;
                    double ratio = x / width - .1;
                    ratio = Math.max(0.0, ratio);

                    // System.out.printf("ratio = %.4f\n", ratio);
                    int newStart = (int) ((end - start) * ratio + start);
                    int newEnd = (int) ((end - start) * (ratio + 0.2) + start);
                    // System.out.printf("start: %d, end: %d\n", newStart, newEnd);
                    self.start = newStart;
                    self.end = Math.max(newEnd, newStart + 9);

                    // trigger a redraw
                    self.repaint();
                }
            }
        });
    }

    // The Chart will receive keyboard events from the JFrame
    // Respond to the LEFT & RIGHT Arrow keys being released.
    public void keyReleased(int keyCode) {

        if (keyCode == KeyEvent.VK_LEFT) {
            // Move graph to the left
            int newStart = this.start - (this.end - this.start);
            newStart = Math.max(0, newStart);
            this.end = newStart + (this.end - this.start);
            this.start = newStart;

            this.repaint();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            // Move graph to the right
            int newStart = this.start + (this.end - this.start);
            int max = this.wordFreq == null ? 100 : this.wordFreq.size() - 1;
            newStart = Math.min(max, newStart);
            this.end = newStart + (this.end - this.start);
            this.start = newStart;

            this.repaint();
        }
    }

    // Paint the chart. Paint the axes, the theoretical Zipf values, and
    // the client provided frequency values.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.WHITE);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());

        double maxY = this.wordFreq == null || this.wordFreq.size() <= this.end
                ? 1.0
                : this.wordFreq.get(this.start).getFreq();
        maxY = Math.min(1.0, maxY * 1.2);

        // System.out.printf("Draw axis: %d, %d, %.3f\n", start, end, maxY);
        drawAxis(g, start, end, maxY);

        // draw Zipf info first in Green
        g.setColor(Color.GREEN);
        drawPoints(g, start, end, maxY, this.zipfInfo);

        // draw our data info second in Blue
        g.setColor(Color.BLUE);
        drawPoints(g, start, end, maxY, this);
    }

    // Setter that allows the client to set the points to draw
    public void setData(List<WordFreq> zipf) {
        this.wordFreq = zipf;

        this.start = 0;
        this.end = zipf == null ? 100 : this.wordFreq.size() - 1;

        // trigger a redraw
        this.repaint();
    }

    // Implement the GetPointInfo interface for point information on
    // the Zipf's theoretical values.
    // Store the interface implementation into an instance field, zipfInfo.
    private GetPointInfo zipfInfo = new GetPointInfo() {
        @Override
        public String getWord(int index) {
            // no words for the theortical Zipf sequence
            return "";
        }

        @Override
        public double getFreq(int index) {
            // index is zero-based, so be sure to add 1
            return 1.0 / (index + 1);
        }

        @Override
        public int getPointCount() {
            // as many points as we need. There is no limit beyond
            // what an integer can hold.
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean hasWords() {
            // Theoretical Zipf sequence has no words
            return false;
        }
    };

    // Draw the points onto the Graphics using the start & end indices.
    // Limit the y-axis to the maxY value provided.
    // Use the GetPointInfo implementation to retreive information about
    // the points to draw.
    // The Color of the line must be set before calling this method.
    // This charting assumes that Y will always be in the range [0, 1.0].
    private void drawPoints(Graphics g, int start, int end, double maxY, GetPointInfo ptInfo) {
        // System.out.printf("Draw points: %d, %d, %.3f\n", start, end, maxY);
        // we may not have any points (yet). Don't plot anything!
        if (this.wordFreq == null) {
            return;
        }

        // Draw the words if we have them and we have fewer than 12 to draw.
        // If we have lots of points, it is too ugly to draw the words.
        boolean drawWord = ptInfo.hasWords() && (end - start) < 11;

        // Cache our multipliers that allow us to scale to the graph's size
        double kx = (double) (this.getWidth() - 2 * BOUNDARY) / (end - start);
        double ky = (this.getHeight() - 2 * BOUNDARY);
        if (Math.max(start, end) >= ptInfo.getPointCount()) {
            return;
        }

        // draw our first point (FencePost problem)
        double y0 = ptInfo.getFreq(start);
        int xpx0 = BOUNDARY;
        int ypx0 = (int) (ky * (1.0 - y0 / maxY)) + BOUNDARY;
        if (ypx0 < BOUNDARY) {
            ypx0 = BOUNDARY;
        }
        if (drawWord) {
            g.drawString(ptInfo.getWord(start), xpx0, ypx0 - 20);
        }

        // Loop through all the remaining points and connect the lines
        for (int index = start + 1; index <= end; index++) {
            if (index >= this.wordFreq.size()) {
                break;
            }
            double y1 = ptInfo.getFreq(index);
            int xpx1 = (int) ((index - start) * kx) + BOUNDARY;
            int ypx1 = (int) (ky * (1.0 - y1 / maxY)) + BOUNDARY;

            if (ypx1 < BOUNDARY) {
                ypx1 = BOUNDARY;
            }

            g.drawLine(xpx0, ypx0, xpx1, ypx1);

            if (drawWord) {
                g.drawString(ptInfo.getWord(index), xpx1, ypx1 - 20);
            }

            // save prior point to allow us to draw lines
            xpx0 = xpx1;
            ypx0 = ypx1;
        }
    }

    // Draw the labeled axes with the correct start/end indices and
    // the maximum y value.
    private void drawAxis(Graphics g, int start, int end, double maxY) {
        g.setColor(Color.BLACK);
        g.drawLine(BOUNDARY, BOUNDARY, BOUNDARY, this.getHeight() - BOUNDARY);
        g.drawLine(BOUNDARY, this.getHeight() - BOUNDARY, this.getWidth() - BOUNDARY, this.getHeight() - BOUNDARY);

        // Sample mappings from (index, value) to pixel (x, y)
        // for a start/end of 0-100 and a maxY of 1.0.
        // (0, 0) on the graph is at (BOUNDARY, HEIGHT-BOUNDARY)
        // (100, 1.0) on the graph is at: (WIDTH - BOUNDARY, BOUNDARY)
        // (50, 0.5) on the graph is at: (WIDTH - 2*BOUNDARY)*50/100,

        double kx = (double) (this.getWidth() - 2 * BOUNDARY) / (end - start);
        double ky = (this.getHeight() - 2 * BOUNDARY);
        double dy = maxY / 4.0;
        // draw 4 y-ticks
        for (double y = 0.0; y <= maxY; y += dy) {
            int ypx = (int) (ky * (1.0 - y / maxY)) + BOUNDARY;
            // draw tick
            g.drawLine(BOUNDARY - 3, ypx, BOUNDARY + 3, ypx);
            // draw text
            String value = String.format("%.4f", y);
            g.drawString(value, 0, ypx);
        }

        // draw 10 x-ticks
        int ypx = this.getHeight() - BOUNDARY;
        int dx = (end - start + 1) / 10;
        // we can't let our dx be zero!
        dx = Math.max(1, dx);
        for (int x = start; x <= end; x += dx) {
            int xpx = (int) ((x - start) * kx) + BOUNDARY;
            g.drawLine(xpx, ypx - 3, xpx, ypx + 3);
            g.drawString("" + x, xpx - 5, ypx + (BOUNDARY / 2));
        }
    }

    // ** Implement GetPointInfo Interface **
    // This will use the the frequency list set by the client code.
    @Override
    public String getWord(int index) {
        if (this.wordFreq != null && index < this.wordFreq.size()) {
            return this.wordFreq.get(index).getWord();
        }
        return "";
    }

    @Override
    public double getFreq(int index) {
        if (this.wordFreq != null && index < this.wordFreq.size()) {
            return this.wordFreq.get(index).getFreq();
        }
        return 0.0;
    }

    @Override
    public int getPointCount() {
        return (this.wordFreq == null ? 0 : this.wordFreq.size());
    }

    @Override
    public boolean hasWords() {
        return this.wordFreq != null;
    }
}
