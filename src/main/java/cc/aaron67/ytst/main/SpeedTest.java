package cc.aaron67.ytst.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import cc.aaron67.fetch.leetcode.utils.Config;
import cc.aaron67.fetch.leetcode.utils.HttpUtils;
import cc.aaron67.ytst.model.ServerRecord;

public class SpeedTest {

	private static Logger logger = Logger.getLogger(SpeedTest.class);

	private List<ServerRecord> serverList = new ArrayList<ServerRecord>();

	public void run() {
		//
	}

	public List<ServerRecord> getServerList() {
		return serverList;
	}

	public void fetchServerList() {
		String srvurl = Config.get("srvurl");
		logger.info(srvurl);
		// 登录云梯
		fetchPage(srvurl);
		// 获取服务器列表

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
		
		for(ServerRecord sr : serverList) {
			try {
				//获取操作系统类型
				String osType = System.getProperty("os.name").toLowerCase();
				
				//根据操作系统类型抓取ping信息
				if(osType.contains("windows")) {
					showPingMsgFromWindwos(Runtime.getRuntime().exec("ping -n 10 " + sr.getDomain()), sr.getDomain());
				} else if(osType.contains("mac")) {
					showPingMsgFromMac(Runtime.getRuntime().exec("ping -n 10 " + sr.getDomain()));
				} else {
					System.out.println("Sorry,we can't support the " + osType + " Operating System now.");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showPingMsgFromWindwos(Process p, String domainName) {
		try {
			
			System.out.println("-- " + domainName + " ping statistics --");
			
			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "GBK");
			BufferedReader br = new BufferedReader(isr);
			int count = 1;
			while (true) {
				String strRead = br.readLine();
				if (strRead == null) {
					break;
				} else {
					//WINDOWS下，ping命令在控制台需要经过14行才能获得统计信息
					if (count <= 14) {
						count++;
					} else {
						System.out.println(strRead);
					}
				}

			}
			System.out.println();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		
	}
	
	private void showPingMsgFromMac(Process p) {
		try {
			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "utf8");
			BufferedReader br = new BufferedReader(isr);
			int count = 1;
			while (true) {
				String strRead = br.readLine();
				if (strRead == null) {
					break;
				} else {
					//WINDOWS下，ping命令在控制台需要经过12行才能获得统计信息
					if (count <= 12) {
						count++;
					} else {
						System.out.println(strRead);
					}
				}

			}
			System.out.println();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		
	}

	/**
	 * 抓取页面的源码
	 */
	private String fetchPage(String url) {
		Map<String, String> headers = new HashMap<String, String>();
		CloseableHttpResponse response = HttpUtils.get(url, headers);
		try {
			return HttpUtils.fetchWebpage(response);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
