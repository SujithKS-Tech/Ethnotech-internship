import java.io.*;
import java.util.*;
import java.util.stream.*;

public class MarksAnalyzer {

    public static void main(String[] args) {

        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("C:\\Users\\senth\\Desktop\\internship\\Streams\\marks.txt"));
                    

            List<Integer> marks = br.lines()
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

            int total = marks.stream().mapToInt(Integer::intValue).sum();
            double average = marks.stream().mapToInt(Integer::intValue).average().orElse(0);
            int highest = marks.stream().mapToInt(Integer::intValue).max().orElse(0);

            System.out.println("Total Marks: " + total);
            System.out.println("Average Marks: " + average);
            System.out.println("Highest Mark: " + highest);

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}