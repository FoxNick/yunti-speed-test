package cc.aaron67.ytst.model;

public class PingTestResult {
	private ServerRecord serverRecord;
	private PingStatistics pingStatistics;

	public ServerRecord getServerRecord() {
		return serverRecord;
	}

	public void setServerRecord(ServerRecord serverRecord) {
		this.serverRecord = serverRecord;
	}

	public PingStatistics getPingStatistics() {
		return pingStatistics;
	}

	public void setPingStatistics(PingStatistics pingStatistics) {
		this.pingStatistics = pingStatistics;
	}
}
