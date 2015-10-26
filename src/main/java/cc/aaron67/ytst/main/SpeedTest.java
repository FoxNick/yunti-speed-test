package cc.aaron67.ytst.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.aaron67.fetch.leetcode.utils.Config;
import cc.aaron67.fetch.leetcode.utils.HttpUtils;
import cc.aaron67.ytst.model.ServerRecord;

public class SpeedTest {

	private static Logger logger = Logger.getLogger(SpeedTest.class);

	private String dallasSession = "";
	private List<ServerRecord> serverList = new ArrayList<ServerRecord>();

	private final static String LOGIN_URL = Config.get("srvurl") + "/users/sign_in";
	private final static String ADMIN_URL = Config.get("srvurl") + "/admin";
	private final static String SERVER_URL = Config.get("srvurl") + "/admin/servers";

	public void run() {
		fetchServerList();
	}

	public List<ServerRecord> getServerList() {
		return serverList;
	}

	public boolean login() {
		logger.info("登录云梯网站");
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
				logger.info("登录成功");
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		logger.info("登录失败");
		return false;
	}

	public void fetchServerList() {
		try {
			// 登录云梯
			if (login()) {
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
				logger.info("抓取服务器列表成功");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			logger.info("抓取服务器列表失败");
		}
	}

	public void testServerSpeed() {
		//
	}

}
