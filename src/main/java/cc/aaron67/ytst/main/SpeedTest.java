package cc.aaron67.ytst.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.aaron67.fetch.leetcode.utils.Config;
import cc.aaron67.fetch.leetcode.utils.HttpUtils;
import cc.aaron67.ytst.model.PingStatistics;
import cc.aaron67.ytst.model.PingTestResult;
import cc.aaron67.ytst.model.ServerRecord;

public class SpeedTest {

	private final static String LOGIN_URL = Config.get("srvurl") + "/users/sign_in";
	private final static String ADMIN_URL = Config.get("srvurl") + "/admin";
	private final static String SERVER_URL = Config.get("srvurl") + "/admin/servers";

	private String dallasSession = "";
	private List<ServerRecord> serverList = new ArrayList<ServerRecord>();
	private List<PingTestResult> pingTestResult = new ArrayList<PingTestResult>();

	public void runTest() {
		fetchServerList();
		testServerSpeed();
		displayTestResult();
	}

	private void fetchServerList() {
		try {
			if (login()) { // 登录云梯
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Referer", ADMIN_URL);
				headers.put("Cookie", "_dallas_session=" + dallasSession);
				// 获取服务器列表
				Elements serverTable = Jsoup.parse(HttpUtils.fetchPage(SERVER_URL, headers)).select("tr");
				String region = "", name = "";
				for (Element e : serverTable) {
					Elements tds = e.select("td");
					if (tds.size() == 0) {
						continue;
					}
					ServerRecord sr = new ServerRecord();
					int i = 0;
					if (tds.size() == 6) {
						region = tds.get(i++).text();
						name = tds.get(i++).text();
					} else if (tds.size() == 5) {
						name = tds.get(i++).text();
					}
					sr.setRegion(region);
					sr.setName(name);
					sr.setDomain(tds.get(i++).text());
					sr.setStatus(tds.get(i++).text());
					sr.setProtocal(tds.get(i++).text());
					sr.setComment(tds.get(i++).text());
					serverList.add(sr);
				}
				System.out.println("抓取服务器列表成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("抓取服务器列表失败");
		}
	}

	private boolean login() {
		System.out.println("登录云梯网站");
		String authenticity_token = Jsoup.parse(HttpUtils.fetchPage(LOGIN_URL, new HashMap<String, String>()))
				.select("input[name=authenticity_token]").val();
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("utf8", "✓");
		loginParams.put("authenticity_token", authenticity_token);
		loginParams.put("user[login]", Config.get("username"));
		loginParams.put("user[password]", Config.get("password"));
		loginParams.put("user[remember_me]", "0");
		loginParams.put("commit", "登录");
		CloseableHttpResponse response = HttpUtils.post(LOGIN_URL, new HashMap<String, String>(), loginParams);
		try {
			if (response.getStatusLine().getStatusCode() == 302
					&& response.getFirstHeader("Location").getValue().equals(ADMIN_URL)) {
				for (Header h : response.getHeaders("Set-Cookie")) {
					for (HeaderElement he : h.getElements()) {
						if (he != null && he.getName().equals("_dallas_session")) {
							dallasSession = he.getValue();
						}
					}
				}
				System.out.println("登录成功");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("登录失败");
		return false;
	}

	private void testServerSpeed() {
		switch (Config.get("method")) {
		case "ping":
			pingTestServerSpeed();
			break;
		default:
			System.out.println("不支持的测速方法");
		}
	}

	private void pingTestServerSpeed() {
		System.out.println("开始PING测速");
		try {
			for (ServerRecord sr : serverList) {
				PingTestResult ptr = new PingTestResult();
				ptr.setServerRecord(sr);
				ptr.setPingStatistics(new PingStatistics());
				pingTestResult.add(ptr);
			}
			String osType = System.getProperty("os.name").toLowerCase();
			if (osType.contains("windows")) {
				//
			} else if (osType.contains("mac")) {
				macPingTest();
			} else {
				System.out.println("不支持的操作系统");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void macPingTest() {
		System.out.println("启动" + pingTestResult.size() + "个线程测速");
		CountDownLatch signal = new CountDownLatch(pingTestResult.size());
		Map<String, String> params = new HashMap<String, String>();
		params.put("-c", Config.get("ping_count"));
		for (int i = 0; i < pingTestResult.size(); ++i) {
			MacPingMethod mpm = new MacPingMethod();
			mpm.setHost(pingTestResult.get(i).getServerRecord().getDomain());
			mpm.setPingStatistics(pingTestResult.get(i).getPingStatistics());
			mpm.setParams(params);
			mpm.setSignal(signal);
			Thread t = new Thread(mpm);
			t.start();
		}
		try {
			signal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("测速结束");
	}

	private void displayTestResult() {
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%10s%10s%10s%10s%10s%10s%10s\n", "Server", "Protocol", "PKG", "Miss%", "MIN", "MAX", "AVG");
		System.out.println("-------------------------------------------------------------------------");
		switch (Config.get("method")) {
		case "ping":
			displayPingTestResult();
			break;
		}
	}

	private void displayPingTestResult() {
		for (PingTestResult ptr : pingTestResult) {
			System.out.printf("%10s%10s%10s%10s%10s%10s%10s\n", ptr.getServerRecord().getName(),
					ptr.getServerRecord().getProtocal(), ptr.getPingStatistics().getPacketsTransmitted(),
					ptr.getPingStatistics().getPacketsLossRate(), ptr.getPingStatistics().getRoundTripMin(),
					ptr.getPingStatistics().getRoundTripMax(), ptr.getPingStatistics().getRoundTripAvg());
		}
		System.out.println("-------------------------------------------------------------------------");
	}

}
