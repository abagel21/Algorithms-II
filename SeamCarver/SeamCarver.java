import edu.princeton.cs.algs4.Picture;

import java.awt.*;

/**
 * Finds the lowest "weight" path in an image calculated with a specific gradient priority equation
 * for removal or addition upon resizing an image
 * using a special digraph and the edge weighted directed acyclic graph shortest path algorithm
 */
public class SeamCarver {
    private Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("No null arguments");
        this.pic = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x > pic.width() - 1 || y > pic.height() - 1 || x < 0 || y < 0)
            throw new IllegalArgumentException("must be in picture");
        if (x == 0 || x == pic.width() - 1 || y == 0 || y == pic.height() - 1) return 1000;
        // gets the color of all surrounding pixels
        Color xpix1 = pic.get(x + 1, y);
        Color xpix2 = pic.get(x - 1, y);
        Color ypix1 = pic.get(x, y + 1);
        Color ypix2 = pic.get(x, y - 1);
        // calculates the weight for this pixel
        return Math.sqrt(sqrdColorDist(xpix1, xpix2) + sqrdColorDist(ypix1, ypix2));
    }

    private double sqrdColorDist(Color a, Color b) {
        return Math.pow(Math.abs(a.getGreen() - b.getGreen()), 2) + Math.pow(Math.abs(a.getRed() - b.getRed()), 2) + Math.pow(Math.abs(a.getBlue() - b.getBlue()), 2);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] energy = new double[pic.height()][pic.width()];
        double[][] distTo = new double[pic.height()][pic.width()];
        int[][] edgeTo = new int[pic.height()][pic.width()];
        //initialize arrays
        for (int i = 0; i < pic.height(); i++) {
            // all first row pixels have distance 1000
            distTo[i][0] = 1000;
            energy[i][0] = 1000;
            energy[i][pic.width() - 1] = 1000;
            edgeTo[i][0] = i;
            if (pic.width() > 1) {
                energy[i][1] = energy(1, i);
                distTo[i][1] = energy[i][1] + 1000;
                edgeTo[i][1] = i;
            }
        }
        for (int j = 2; j < pic.width(); j++) {
            for (int x = 0; x < pic.height(); x++) {
                distTo[x][j] = Double.POSITIVE_INFINITY;
            }
        }
        // for every row
        for (int i = 1; i < pic.width(); i++) {
            // for each pixel in the row
            for (int j = 0; j < pic.height(); j++) {
                if (Double.isInfinite(energy[j][i]) || energy[j][i] == 0.0) {
                    energy[j][i] = energy(i, j);
                }
                // relax all adjacent pixels in the DAG
                relaxHor(j - 1, i, j, energy, distTo, edgeTo);
                relaxHor(j, i, j, energy, distTo, edgeTo);
                relaxHor(j + 1, i, j, energy, distTo, edgeTo);
            }
        }

        // set the min to be the bottom pixel with the lowest distTo
        int min = 0;
        for (int i = 1; i < pic.height(); i++) {
            if (distTo[i][pic.width() - 1] < distTo[min][pic.width() - 1]) {
                min = i;
            }
        }
        // initialize seam array
        int[] seam = new int[pic.width()];
        seam[0] = min;
        // trace minimum path and add to seam
        min = edgeTo[min][pic.width() - 1];
        for (int i = 0; i < seam.length; i++) {
            seam[i] = min;
            min = edgeTo[min][pic.width() - i - 1];
        }
        // reverse seam array
        for (int i = 0; i < seam.length / 2; i++) {
            int temp = seam[seam.length - i - 1];
            seam[seam.length - i - 1] = seam[i];
            seam[i] = temp;
        }
        return seam;
    }

    // resets distTo for this pixel if the path to it is shorter than the current one stored
    private void relaxHor(int adj, int i, int j, double[][] energy, double[][] distTo, int[][] edgeTo) {
        // bounds check
        if (adj < 0 || adj > pic.height() - 1) return;
        if (i < 0 || i > pic.width() - 2) return;
        // checks if energy has been calculated yet, only calculates when necessary to avoid repeat calls
        if (Double.isInfinite(energy[adj][i + 1]) || energy[adj][i + 1] == 0.0) {
            energy[adj][i + 1] = energy(i + 1, adj);
        }
        // i is column
        // j is row
        // adj is modified row
        // resets distTo and edgeTo if path is shorter
        if (distTo[adj][i + 1] > distTo[j][i] + energy[adj][i + 1]) {
            distTo[adj][i + 1] = distTo[j][i] + energy[adj][i + 1];
            edgeTo[adj][i + 1] = j;
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energy = new double[pic.height()][pic.width()];
        double[][] distTo = new double[pic.height()][pic.width()];
        int[][] edgeTo = new int[pic.height()][pic.width()];
        //initialize arrays
        for (int i = 0; i < pic.width(); i++) {
            // all first row pixels have distance 1000
            distTo[0][i] = 1000;
            energy[0][i] = 1000;
            energy[pic.height() - 1][i] = 1000;
            if (pic.height() > 1) {
                energy[1][i] = energy(i, 1);
                distTo[1][i] = energy[1][i] + 1000;
                edgeTo[1][i] = i;
            }
            edgeTo[0][i] = i;
        }
        for (int j = 2; j < pic.height(); j++) {
            for (int x = 0; x < pic.width(); x++) {
                distTo[j][x] = Double.POSITIVE_INFINITY;
            }
        }
        // for every row
        for (int i = 1; i < pic.height(); i++) {
            // for each pixel in the row
            for (int j = 0; j < pic.width(); j++) {
                if (Double.isInfinite(energy[i][j]) || energy[i][j] == 0.0) {
                    energy[i][j] = energy(j, i);
                }
                // relax all adjacent pixels
                relaxVert(j - 1, i, j, energy, distTo, edgeTo);
                relaxVert(j, i, j, energy, distTo, edgeTo);
                relaxVert(j + 1, i, j, energy, distTo, edgeTo);
            }
        }

        // set the min to be the bottom pixel with the lowest distTo
        int min = 0;
        for (int i = 1; i < pic.width(); i++) {
            if (distTo[pic.height() - 1][i] < distTo[pic.height() - 1][min]) {
                min = i;
            }
        }
        // initialize seam array
        int[] seam = new int[pic.height()];
        seam[0] = min;
        // trace minimum path and add to seam
        min = edgeTo[pic.height() - 1][min];
        for (int i = 1; i < seam.length; i++) {
            seam[i] = min;
            min = edgeTo[pic.height() - i - 1][min];
        }
        // reverse seam array
        for (int i = 0; i < seam.length / 2; i++) {
            int temp = seam[seam.length - i - 1];
            seam[seam.length - i - 1] = seam[i];
            seam[i] = temp;
        }
        return seam;
    }

    // resets distTo for this pixel if the path to it is shorter than the current one stored
    private void relaxVert(int adj, int i, int j, double[][] energy, double[][] distTo, int[][] edgeTo) {
        // bounds check
        if (adj < 0 || adj > pic.width() - 1) return;
        if (i < 0 || i > pic.height() - 2) return;
        // checks if energy has been calculated yet, only calculates when necessary to avoid repeat calls
        if (Double.isInfinite(energy[i + 1][adj]) || energy[i + 1][adj] == 0.0) {
            energy[i + 1][adj] = energy(adj, i + 1);
        }
        // i is row
        // j is column
        // adj is modified column
        // resets distTo and edgeTo if path is shorter
        if (distTo[i + 1][adj] > distTo[i][j] + energy[i + 1][adj]) {
            distTo[i + 1][adj] = distTo[i][j] + energy[i + 1][adj];
            edgeTo[i + 1][adj] = j;
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (checkHorSeam(seam))
            throw new IllegalArgumentException("No null arguments, array must be equal to the height, and picture must have a height greater than 1");
        // creates new picture with a row removed
        Picture x = new Picture(pic.width(), pic.height() - 1);
        // sets the pixel values for the new picture
        // skipping anything in the seam
        for (int i = 0; i < x.width(); i++) {
            for (int j = 0; j < x.height(); j++) {
                if (j < seam[i]) {
                    x.setRGB(i, j, pic.getRGB(i, j));
                } else {
                    x.setRGB(i, j, pic.getRGB(i, j + 1));
                }
            }
        }
        this.pic = x;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (checkVertSeam(seam))
            throw new IllegalArgumentException("No null arguments, array must be equal to the height, and picture must have a width greater than 1");
        // creates new picture with a row removed
        Picture x = new Picture(pic.width() - 1, pic.height());
        // sets the pixel values for the new picture
        // skipping anything in the seam
        for (int i = 0; i < x.height(); i++) {
            for (int j = 0; j < x.width(); j++) {
                if (j < seam[i]) {
                    x.setRGB(j, i, pic.getRGB(j, i));
                } else {
                    x.setRGB(j, i, pic.getRGB(j + 1, i));
                }
            }
        }
        this.pic = x;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver s = new SeamCarver(picture);
        int[] x = s.findVerticalSeam();
        s.removeVerticalSeam(x);
    }

    // checks if a seam is valid (all values are within bounds, nonnull, picture is > 2 in width or height)
    private boolean checkVertSeam(int[] seam) {
        if (pic.width() < 2) return true;
        if (seam == null || seam.length != pic.height()) return true;
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                return true;
            }
            if (seam[i] < 0 || seam[i] > pic.width() - 1) return true;
        }
        if (seam[seam.length - 1] < 0 || seam[seam.length - 1] > pic.width() - 1) return true;
        return false;
    }
    
    // checks if a seam is valid (all values are within bounds, nonnull, picture is > 2 in width or height)
    private boolean checkHorSeam(int[] seam) {
        if (pic.height() < 2) return true;
        if (seam == null || seam.length != pic.width()) return true;
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                return true;
            }
            if (seam[i] < 0 || seam[i] > pic.height() - 1) return true;
        }
        if (seam[seam.length - 1] < 0 || seam[seam.length - 1] > pic.height() - 1) return true;
        return false;
    }
}
