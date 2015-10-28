package cc.aaron67.ytst.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.aaron67.ytst.model.PingStatistics;

public class PingMethod implements Runnable {

	private PingStatistics pingStatistics;
	private String host;
	private Map<String, String> params;
	private CountDownLatch signal;
	private String patternPackage;
	private String patternRoundTrip;

	@Override
	public void run() {
		try {
			StringBuilder paramsBuilder = new StringBuilder();
			for (String key : params.keySet()) {
				paramsBuilder.append(key).append(" ").append(params.get(key)).append(" ");
			}
			String command = "ping " + paramsBuilder.toString() + host;
			BufferedReader buf = new BufferedReader(
					new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()));
			String readline;
			while ((readline = buf.readLine()) != null) {
				Pattern pp = Pattern.compile(patternPackage);
				Matcher mp = pp.matcher(readline);
				if (mp.find()) {
					pingStatistics.setPacketsTransmitted(Integer.parseInt(mp.group(1)));
					pingStatistics.setPacketsReceived(Integer.parseInt(mp.group(2)));
					pingStatistics.setPacketsLossRate(Double.parseDouble(mp.group(3)));
				}
				Pattern prt = Pattern.compile(patternRoundTrip);
				Matcher mrt = prt.matcher(readline);
				if (mrt.find()) {
					double rtt0 = Double.parseDouble(mrt.group(1));
					double rtt1 = Double.parseDouble(mrt.group(2));
					double rtt2 = Double.parseDouble(mrt.group(3));
					pingStatistics.setRoundTripMin(Math.min(rtt0, Math.min(rtt1, rtt2)));
					pingStatistics.setRoundTripMax(Math.max(rtt0, Math.max(rtt1, rtt2)));
					DecimalFormat df = new DecimalFormat("#.###");
					pingStatistics.setRoundTripAvg(Double.parseDouble(df.format(
							rtt0 + rtt1 + rtt2 - pingStatistics.getRoundTripMin() - pingStatistics.getRoundTripMax())));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSignal().countDown();
	}

	public PingStatistics getPingStatistics() {
		return pingStatistics;
	}

	public void setPingStatistics(PingStatistics pingStatistics) {
		this.pingStatistics = pingStatistics;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public CountDownLatch getSignal() {
		return signal;
	}

	public void setSignal(CountDownLatch signal) {
		this.signal = signal;
	}

	public String getPatternPackage() {
		return patternPackage;
	}

	public void setPatternPackage(String patternPackage) {
		this.patternPackage = patternPackage;
	}

	public String getPatternRoundTrip() {
		return patternRoundTrip;
	}

	public void setPatternRoundTrip(String patternRoundTrip) {
		this.patternRoundTrip = patternRoundTrip;
	}

}
