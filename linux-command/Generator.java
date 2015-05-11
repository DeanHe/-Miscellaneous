import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Generator {
	// target date
	public static final String DATE = "2014-10-31";
	// number of servers
	public static final int SERVERTotal = 1000;
	// number of cpu
	public static final int CPUtotal = 2;
	// 60 sec update once
	public static final long UnixTimeInterval = 60;

	public Date date;
	public long UnixTimeStart;
	public long UnixTimeEnd;

	public Generator() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			date = sdf.parse(DATE);
			UnixTimeStart = date.getTime() / 1000;
			// get next day for the end time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			// (add one more day by Calendar)
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
			UnixTimeEnd = date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Invalid Directory");
			return;
		}
		String path = args[0];
		File directory = new File(path);
		Generator generator = new Generator();
		generator.createData(directory);

	}

	public void createData(File directory) {
		for (int i = 0; i < SERVERTotal; i++) {
			String ip = convertToIP(i);
			String filePath = directory.getPath() + File.separator + ip + ".txt";
			File file = new File(filePath);
			try {
				PrintWriter printWriter = new PrintWriter(file);
				printWriter.println("timestamp 	IP       cpu_id usage");
				int cupUsage;
				for (long time = UnixTimeStart; time < UnixTimeEnd; time += UnixTimeInterval) {
					for (int k = 0; k < CPUtotal; k++) {
						cupUsage = (int) (Math.random() * 100 + 1);
						String entry = String.format("%d %s   %d  %d", time, ip, k, cupUsage);
						printWriter.println(entry);
					}

				}
				printWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public String convertToIP(int i) {
		// assign 1000 servers to each ip
		String res = "192.168.";
		int subnet1 = i / 256;
		int subnet2 = i % 256;
		res += Integer.toString(subnet1) + "." + Integer.toString(subnet2);
		return res;
	}

}
