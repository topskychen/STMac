/**
 * 
 */
package utility;

import graphics.Data;
import graphics.ShowData;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * @author chenqian
 *
 */
public class DataParser {

	public static void listSubFileNames (File file) {
		for (String subFile : file.list()) {
			System.out.println(subFile);
		}
	}
	
	public static int getLineCount(String fileName) {
		BufferedReader reader;
		int lines = 0;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			while (reader.readLine() != null) lines++;
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
	
	public static void parseTraFileWithSize(int size) {
		String dirName = "./dataset/Geolife/Data/";
		String maxName = ""; long minSize = Integer.MAX_VALUE;
		for (int i = 0; i <= 181; i ++) {
			File file = new File(dirName + String.format("%03d", i) + "/Trajectory");
			for (String subName : file.list()) {
//				System.out.println(subFile.getName() + ", " + file.length() + " B");
				int lines = getLineCount(dirName + String.format("%03d", i) + "/Trajectory/" + subName) - 6;
				if (Math.abs(lines - size) < minSize) {
					minSize = Math.abs(lines - size);
					maxName = dirName + String.format("%03d", i) + "/Trajectory/" + subName;
				}
				if (minSize < 10) {
					System.out.println(maxName + ", " + size + " (+-) " + minSize);
					return;
				}
			}
		}
		System.out.println(maxName + ", " + size + " (+-) " + minSize);
	}
	
	public static void parseTraFileMaxSize() {
		String dirName = "./dataset/Geolife/Data/";
		String maxName = ""; long maxSize = -1;
		for (int i = 0; i <= 181; i ++) {
			File file = new File(dirName + String.format("%03d", i) + "/Trajectory");
			for (File subFile : file.listFiles()) {
//				System.out.println(subFile.getName() + ", " + file.length() + " B");
				if (subFile.length() > maxSize) {
					maxSize = subFile.length();
					maxName = dirName + String.format("%03d", i) + "/Trajectory/" + subFile.getAbsolutePath();
				}
			}
		}
		System.out.println(maxName + ", " + maxSize / 1000.0 + " KB");
	}
	
	public static Date[] getDate(String fileName) {
		File file = new File(fileName);
		ArrayList<Date> dates= new ArrayList<Date>();
		try {
			Scanner in = new Scanner(file);
			for (int i = 0; i < 6; i ++) in.nextLine();
			while(in.hasNext()) {
				String[] tks = in.nextLine().split(",");
				String time = tks[5] + " " + tks[6];
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = (Date) sdf.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dates.add(date);
			}
			System.out.println("Time size: " + dates.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dates.toArray(new Date[]{});
	}
	
	public static int[] readTimeStamps(String fileName) {
		Date[] dates = getDate(fileName);
		long begin = dates[0].getTime();
		int[] timeStamps = new int[dates.length];
		for (int i = 0 ; i < dates.length; i ++) {
			timeStamps[i] = (int) ((dates[i].getTime() - begin) / 1000.0 );
//			System.out.println(dates[i].getTime() + ", " + timeStamps[i]);
		}
		return timeStamps;
	}
	
	public static double[][] readLatLng(String fileName) {
		File file = new File(fileName);
		ArrayList<double[]> points = new ArrayList<double[]>();
		try {
			Scanner in = new Scanner(file);
			for (int i = 0; i < 6; i ++) in.nextLine();
			while(in.hasNext()) {
				String[] tks = in.nextLine().split(",");
				points.add(new double[]{Double.parseDouble(tks[0]), Double.parseDouble(tks[1])});
			}
//			System.out.println("Point size: " + points.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return points.toArray(new double[][]{});
	} 
	
//	public static int[][] formatRegion(double[][] region) {
//		double[] x = new double[region.length];
//		double l = Double.MAX_VALUE, r = Double.MIN_VALUE;
//		for (int i = 0; i < region.length; i ++) {
//			x[i] = region[i][0];
//		}
//	}
	
	public static void drawTrajectory(String fileName) {
		Data data = new Data(readLatLng(fileName), Color.BLACK);
		data.setLineType();
		ShowData showData = new ShowData(new Data[]{data});
		ShowData.draw(showData);
	}
	
	public static void generateTraWithTime(String sourceFile, String destFile) {
		double[][] tra = readLatLng(sourceFile);
		int[] time = readTimeStamps(sourceFile);
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(destFile + "_x"));
			pw.println(time.length);
			for (int i = 0; i < time.length; i ++) {
				pw.println( String.format("%32s", Integer.toBinaryString((int)(tra[i][0] * 1000000))).replace(' ', '0') + "\t" + time[i]);
			}
			pw.close();
			pw = new PrintWriter(new File(destFile + "_y"));
			pw.println(time.length);
			for (int i = 0; i < time.length; i ++) {
				pw.println(String.format("%32s", Integer.toBinaryString((int)(tra[i][1] * 1000000))).replace(' ', '0') + "\t" + time[i]);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public DataParser(String fileName) {
		// TODO Auto-generated constructor stub
//		File file
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub;
//		parseTraFileMaxSize();
//		drawTrajectory("./dataset/Geolife/Data/010/Trajectory/20081219114010.plt"); //92645
//		drawTrajectory("./dataset/Geolife/Data/180/Trajectory/20090530161052.plt"); //1024
//		parseTraFileWithSize(1000);
//		parseTraFileWithSize(10000);
//		parseTraFileWithSize(100000);
		/**
		 * ./dataset/Geolife/Data/000/Trajectory/20090402060732.plt, 1000 (+-) 7
			./dataset/Geolife/Data/064/Trajectory/20080824001306.plt, 10000 (+-) 19
			./dataset/Geolife/Data/010/Trajectory/20081219114010.plt, 100000 (+-) 7355

		 */
		
//		readTimeStamps("./dataset/Geolife/Data/000/Trajectory/20090402060732.plt");
//		readTimeStamps("./dataset/Geolife/Data/064/Trajectory/20080824001306.plt");
//		readTimeStamps("./dataset/Geolife/Data/010/Trajectory/20081219114010.plt");
		
		
		drawTrajectory("./dataset/Geolife/Data/000/Trajectory/20090402060732.plt");
//		generateTraWithTime("./dataset/Geolife/Data/000/Trajectory/20090402060732.plt", "./dataset/1000.txt");
//		generateTraWithTime("./dataset/Geolife/Data/064/Trajectory/20080824001306.plt", "./dataset/10000.txt");
//		generateTraWithTime("./dataset/Geolife/Data/010/Trajectory/20081219114010.plt", "./dataset/100000.txt");
	}

}
