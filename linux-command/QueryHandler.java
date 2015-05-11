import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Vector;

public class QueryHandler {
	// number of cpu
	public final int CPUtotal = 2;
	// 60 sec update once
	public final long UnixTimeInterval = 60;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public final static String BEIGNDATE = "2014-10-31 00:00";
	public final static String ENDDATE = "2014-11-01 00:00";
	// number of entries of one log
	private int timestamp_Length;
	// key is the ip. value is a matrix with rows for entries of log; columns
	// for cpu number; inside data is cpu usage
	public static HashMap<String, int[][]> map = new HashMap<String, int[][]>();
	private static String query_IP;
	private static String query_CPU;
	private static String query_StartTime;
	private static String query_endTime;
	private static long query_UnixTime_start;
	private static long query_UnixTime_end;

	// the begin and end time for the log data
	private static long unixTime_Beign;
	private static long unixTIme_End;

	public QueryHandler(String directory) {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		unixTime_Beign = convertToUnixTime(BEIGNDATE);
		unixTIme_End = convertToUnixTime(ENDDATE);
		timestamp_Length = (int) ((unixTIme_End - unixTime_Beign) / UnixTimeInterval);
		if (map.isEmpty()) {
			File dir = new File(directory);
			File[] files = dir.listFiles();
			for (File f : files) {
				Vector<String> records = readFile(f.getAbsolutePath());
				// the ip for this file f, escape first line as it is
				// description
				String ipString = records.get(1).split(" ")[1];
				int[][] usage = getUsageMatrix(records);
				map.put(ipString, usage);
			}
		}

	}

	private static boolean parseInput(String input) {
		// check input from linux command
		input = input.trim();

		String[] splitBySpace = input.split(" ");
		// check command for EXIT
		if (splitBySpace[0].equals("EXIT")) {
			System.exit(1);
		}
		// check command for QUERY; with regex match for
		// "QUERY 192.168.x.x x yyyy-MM-dd HH:mm"
		else if (input
				.matches("QUERY\\s192\\.168\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s[0-1]\\s([1-9][0-9]{3})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])\\s([01]?[0-9]|2[0-3]):[0-5][0-9]\\s([1-9][0-9]{3})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])\\s([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
			query_IP = splitBySpace[1];
			query_CPU = splitBySpace[2];
			query_StartTime = splitBySpace[3] + " " + splitBySpace[4];
			query_endTime = splitBySpace[5] + " " + splitBySpace[6];
			return true;
		} else {
			System.err.println("command not valid");
		}
		return false;
	}

	private int[][] getUsageMatrix(Vector<String> records) {
		int[][] usage = new int[timestamp_Length][CPUtotal];
		int k = 0;
		// escape the first line, as it is description
		for (int i = 1; i < records.size(); i++) {
			String[] splits = records.get(i).split(" ");
			int cpu_use = Integer.valueOf(splits[splits.length - 1]);
			if (i % 2 == 0) {
				// load cpu_1
				usage[k][1] = cpu_use;
				k++;
			} else {
				// load cpu_0
				usage[k][0] = cpu_use;
			}
		}
		return usage;
	}

	private Vector<String> readFile(String path) {
		Vector<String> documents = new Vector<String>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(path);

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				documents.add(strLine);
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return documents;
	}

	private static boolean checkTimeValidation() {
		// check only check time within that day
		if (query_UnixTime_start > query_UnixTime_end) {
			System.err.println("Time input not valid");
			return false;
		} else {
			if (query_UnixTime_start < unixTime_Beign) {
				System.err.println("Time input not valid");
				return false;
			}
			if (query_UnixTime_end > unixTIme_End) {
				System.err.println("Time input not valid");
				return false;
			}
			return true;
		}
	}

	private static long convertToUnixTime(String time) {
		long unixTime = 0;
		try {
			Date date = sdf.parse(time);
			unixTime = date.getTime() / 1000;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (unixTime == 0) {
			System.err.println("time error");
		}
		return unixTime;
	}

	private String converToFormatTime(long unixTime) {
		Date date = new Date(unixTime * 1000L);
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	public static void main(String[] args) throws ParseException {
		QueryHandler query = new QueryHandler(args[0]);
		System.out.println("Initialization Finish, please input command:");
		Scanner scanInput = new Scanner(System.in);
		while (scanInput.hasNextLine()) {
			String input = scanInput.nextLine();
			// System.out.println(input);
			boolean queryValid = parseInput(input);
			if (queryValid) {
				query_UnixTime_start = convertToUnixTime(query_StartTime);
				query_UnixTime_end = convertToUnixTime(query_endTime);
				if (checkTimeValidation()) {
					String result = query.search();
					if (result != null) {
						System.out.println("CPU" + query_CPU + " usage on " + query_IP + ":");
						System.out.println(result);
					}
				}
			}
		}
		scanInput.close();
	}

	public String search() {
		String result = "";
		if (map.keySet().contains(query_IP)) {
			int[][] usage = map.get(query_IP);
			int start_entry = (int) ((query_UnixTime_start - unixTime_Beign) / UnixTimeInterval);
			int end_entry = (int) ((query_UnixTime_end - unixTime_Beign) / UnixTimeInterval);

			int k = 0;
			for (int i = start_entry; i < end_entry; i++) {
				long unixTime = query_UnixTime_start + k * UnixTimeInterval;
				String time = converToFormatTime(unixTime);
				int cpu = Integer.valueOf(query_CPU);
				result += String.format("(%s, %d%%),", time, usage[i][cpu]);
				k++;
			}
			if (result.endsWith(",")) {
				result = result.substring(0, result.length() - 1);
			}
			return result;
		} else {
			System.err.println("IP doesn't exist");
			return null;
		}

	}
}
