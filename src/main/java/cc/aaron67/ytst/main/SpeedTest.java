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
		//
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

	public List<ServerRecord> getServerList() {
		return serverList;
	}

	public void fetchServerList() {
		// 登录云梯
		if (login()) {

		}

		// 临时拼装数据
		ServerRecord sr0 = new ServerRecord();
		sr0.setRegion("香港");
		sr0.setName("香港2号");
		sr0.setDomain("p1.hk2.vpntower.com");
		sr0.setStatus("正常");
		sr0.setProtocal("PPTP");
		sr0.setComment("电信良好。联通优秀。");
		serverList.add(sr0);
		ServerRecord sr1 = new ServerRecord();
		sr1.setRegion("日本");
		sr1.setName("日本2号");
		sr1.setDomain("p2.jp2.vpntower.com");
		sr1.setStatus("正常");
		sr1.setProtocal("L2TP");
		sr1.setComment("电信一般。联通良好。");
		serverList.add(sr1);
	}

	public void testServerSpeed() {
		//
	}

}
