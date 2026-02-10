import java.util.Arrays;

public class SquareCheck {

    static int distance(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    static boolean isSquare(int[] p1, int[] p2, int[] p3, int[] p4) {

        int[] dist = new int[6];

        dist[0] = distance(p1[0], p1[1], p2[0], p2[1]);
        dist[1] = distance(p1[0], p1[1], p3[0], p3[1]);
        dist[2] = distance(p1[0], p1[1], p4[0], p4[1]);
        dist[3] = distance(p2[0], p2[1], p3[0], p3[1]);
        dist[4] = distance(p2[0], p2[1], p4[0], p4[1]);
        dist[5] = distance(p3[0], p3[1], p4[0], p4[1]);

        Arrays.sort(dist);

           return dist[0] > 0 &&
               dist[0] == dist[1] &&
               dist[1] == dist[2] &&
               dist[2] == dist[3] &&
               dist[4] == dist[5];
    }

    public static void main(String[] args) {

        int[] p1 = {20, 10};
        int[] p2 = {10, 20};
        int[] p3 = {20, 20};
        int[] p4 = {10, 10};

        if (isSquare(p1, p2, p3, p4))
            System.out.println("Yes, the points form a Square");
        else
            System.out.println("No, the points do not form a Square");
    }
}
