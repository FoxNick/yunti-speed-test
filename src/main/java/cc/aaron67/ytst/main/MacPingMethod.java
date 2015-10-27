package cc.aaron67.ytst.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.aaron67.ytst.model.PingStatistics;

public class MacPingMethod implements Runnable {

	private PingStatistics pingStatistics;
	private String host;
	private Map<String, String> params;
	private CountDownLatch signal;

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
				// 4 packets transmitted, 3 packets received, 25.0% packet loss
				Pattern pp = Pattern.compile(
						"(\\d+) packets transmitted, (\\d+) packets received, (\\d+\\.\\d+|\\d+)% packet loss");
				Matcher mp = pp.matcher(readline);
				if (mp.find()) {
					pingStatistics.setPacketsTransmitted(Integer.parseInt(mp.group(1)));
					pingStatistics.setPacketsReceived(Integer.parseInt(mp.group(2)));
					pingStatistics.setPacketsLossRate(Double.parseDouble(mp.group(3)));
				}
				// round-trip min/avg/max/... = 101.843/124.146/156.229/... ms
				Pattern prt = Pattern.compile(
						"round-trip[\\s\\S]*=[\\s]*(\\d+\\.\\d+|\\d+)/(\\d+\\.\\d+|\\d+)/(\\d+\\.\\d+|\\d+)/([\\s\\S]*)ms");
				Matcher mrt = prt.matcher(readline);
				if (mrt.find()) {
					pingStatistics.setRoundTripMin(Double.parseDouble(mrt.group(1)));
					pingStatistics.setRoundTripAvg(Double.parseDouble(mrt.group(2)));
					pingStatistics.setRoundTripMax(Double.parseDouble(mrt.group(3)));
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

}
