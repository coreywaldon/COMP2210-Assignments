package A3;

import java.io.File;
import java.util.*;

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
   public Extractor(String filename) {
      Scanner in = new Scanner(filename);
      int n = in.nextInt();
      points = new Point[n];
      for (int i = 0; i < n; i++) {
         points[i] = new Point(in.nextInt(), in.nextInt());
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
      lines = new TreeSet<Line>();
      SortedSet<Line> checkedLines = new TreeSet<>();
      combinations(new Point[4], lines, 0, points.length - 1, 0);
      for (Line l : lines) {
         Line check = new Line();
         for (Point aL : l) {
            if (!check.add(aL)) {
               break;
            }
         }
         if (check.length() == 4) {
            checkedLines.add(check);
         }
      }
      return checkedLines;
   }


   private void combinations(Point[] temp, SortedSet<Line> to, int start, int n, int index) {
      if (index == 4) {
         to.add(new Line(Arrays.asList(temp)));
         return;
      }
      for (int i = start; i <= n; i++) {
         temp[index++] = points[i];
         combinations(temp, to, i + 1, n, index);
         index--;
      }
   }
  
   /**
    * Returns a sorted set of all line segments of at least four collinear
    * points. The line segments are maximal; that is, no sub-segments are
    * identified separately. A sort-and-scan strategy is used. Returns an empty
    * set if there are no qualifying line segments.
    */
   public SortedSet<Line> getLinesFast() {
      lines = new TreeSet<Line>();
      // Turns out this is just a fancier way of doing brute...
//      for (int j = 0; j < points.length; j++) {
//         Point q = points[j];
//         SortedSet<Double> slopes = new TreeSet<>();
//         HashMap<Double, SortedSet<Point>> slopeMap = new HashMap<>();
//         for (int i = 0; i < points.length; i++) {
//            if (i != j) {
//               Double slope = q.slopeTo(points[i]);
//               slopes.add(slope);
//               if (slopeMap.containsKey(slope)) {
//                  slopeMap.get(slope).add(points[i]);
//               } else {
//                  SortedSet<Point> pointGroup = new TreeSet<>();
//                  pointGroup.add(points[i]);
//                  slopeMap.put(slope, pointGroup);
//               }
//            }
//         }
//         for (Double slope : slopes) {
//            if (slopeMap.get(slope).size() > 3) {
//               lines.add(new Line(slopeMap.get(slope)));
//            }
//         }
//      }
      ArrayList<Point> pointsAL = new ArrayList<>();
      pointsAL.addAll(Arrays.asList(points));
      for (int i = 0; i < pointsAL.size(); i++) {
         Line l = new Line();
         l.add(pointsAL.get(i));
         for (Point p : pointsAL) {
            l.add(p);
         }
         if (l.length() > 3) {
            lines.add(l);
         }
         for (Point p : l.line) {
            pointsAL.remove(p);
         }
      }
      return lines;
   }
   
}
