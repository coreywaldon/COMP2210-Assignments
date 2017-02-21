package A3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Extractor.java. Implements feature extraction for collinear points in
 * two dimensional data.
 *
 * @author  Dean Hendrix (dh@auburn.edu)
 * @version 2017-02-08
 *
 */
public class Extractor {

   /** raw data: all (x,y) points from source data. */
   private Point[] points;

   /** lines identified from raw data. */
   private SortedSet<Line> lines;

   /**
    * Builds an extractor based on the points in the file named by filename.
    */
   public Extractor(String filename) throws FileNotFoundException {
      Scanner in = new Scanner(new File(filename));
      if (in.hasNextInt()) {
         int n = in.nextInt();
         points = new Point[n];
         for (int i = 0; i < n; i++) {
            points[i] = new Point(in.nextInt(), in.nextInt());
         }
      }
   }

   /**
    * Builds an extractor based on the points in the Collection named by pcoll.
    *
    * THIS METHOD IS PROVIDED FOR YOU AND MUST NOT BE CHANGED.
    */
   public Extractor(Collection<Point> pcoll) {
      points = pcoll.toArray(new Point[]{});
   }

   /**
    * Returns a sorted set of all line segments of exactly four collinear
    * points. Uses a brute-force combinatorial strategy. Returns an empty set
    * if there are no qualifying line segments.
    */
   public SortedSet<Line> getLinesBrute() {
      lines = new TreeSet<>();
      combinationsIterative(lines);
      ArrayList<Line> lines = new ArrayList<>();
      lines.addAll(this.lines);
      for (Line l : lines) {
         if (l.length() < 4) {
            this.lines.remove(l);
         }
      }
      return this.lines;
   }


   private void combinationsRecursive(Point[] temp, SortedSet<Line> to, int start, int n, int index) {
      if (index == 4) {
         to.add(new Line(Arrays.asList(temp)));
         return;
      }
      for (int i = start; i <= n; i++) {
         temp[index] = points[i];
         combinationsRecursive(temp, to, i + 1, n, index + 1);
      }
   }

   private void combinationsIterative(SortedSet<Line> lines) {
      for (int a = 0; a < points.length; a++) {
         for (int b = a + 1; b < points.length; b++) {
            for (int c = b + 1; c < points.length; c++) {
               for (int d = c + 1; d < points.length; d++) {
                  lines.add(new Line(Arrays.asList(points[a], points[b], points[c], points[d])));
               }
            }
         }
      }
   }

   /**
    * Returns a sorted set of all line segments of at least four collinear
    * points. The line segments are maximal; that is, no sub-segments are
    * identified separately. A sort-and-scan strategy is used. Returns an empty
    * set if there are no qualifying line segments.
    */
   public SortedSet<Line> getLinesFast() {
      lines = new TreeSet<>();
      ArrayList<Point> dataSet = new ArrayList<>();
      dataSet.addAll(Arrays.asList(points));
      while (dataSet.size() > 0) {
         Set<Double> slopes = new HashSet<>();
         HashMap<Double, Set<Point>> slopeMap = new HashMap<>();
         Point r = dataSet.get(0);
         for (Point p : dataSet) {
            if (!p.equals(r)) {
               if (slopeMap.containsKey(p.slopeTo(r))) {
                  slopeMap.get(p.slopeTo(r)).add(p);
               } else {
                  Set<Point> pointSet = new HashSet<>();
                  pointSet.add(r);
                  pointSet.add(p);
                  slopes.add(p.slopeTo(r));
                  slopeMap.put(p.slopeTo(r), pointSet);
               }
            }
         }
         for (Double slope : slopes) {
            if (slopeMap.get(slope).size() > 3) {
               boolean isSubset = false;
               for (Line l : lines) {
                  if (l.line.containsAll(slopeMap.get(slope))) {
                     isSubset = true;
                     break;
                  }
               }
               if (!isSubset) {
                  lines.add(new Line(slopeMap.get(slope)));
               }
            }
         }
         dataSet.remove(r);
      }
      return lines;
   }

}
