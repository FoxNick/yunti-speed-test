package cc.aaron67.ytst.model;

public class PingStatistics {
	private int packetsTransmitted; // 发送了多少数据包
	private int packetsReceived; // 收到多少数据包
	private double packetsLossRate; // 丢包率
	private double roundTripMin; // 往返行程估计最短时间，单位毫秒
	private double roundTripMax; // 往返行程估计最长时间，单位毫秒
	private double roundTripAvg; // 往返行程估计平均时间，单位毫秒

	public PingStatistics() {
		packetsLossRate = 100;
	}

	public int getPacketsTransmitted() {
		return packetsTransmitted;
	}

	public void setPacketsTransmitted(int packetsTransmitted) {
		this.packetsTransmitted = packetsTransmitted;
	}

	public int getPacketsReceived() {
		return packetsReceived;
	}

	public void setPacketsReceived(int packetsReceived) {
		this.packetsReceived = packetsReceived;
	}

	public double getPacketsLossRate() {
		return packetsLossRate;
	}

	public void setPacketsLossRate(double packetsLossRate) {
		this.packetsLossRate = packetsLossRate;
	}

	public double getRoundTripMin() {
		return roundTripMin;
	}

	public void setRoundTripMin(double roundTripMin) {
		this.roundTripMin = roundTripMin;
	}

	public double getRoundTripMax() {
		return roundTripMax;
	}

	public void setRoundTripMax(double roundTripMax) {
		this.roundTripMax = roundTripMax;
	}

	public double getRoundTripAvg() {
		return roundTripAvg;
	}

	public void setRoundTripAvg(double roundTripAvg) {
		this.roundTripAvg = roundTripAvg;
	}
}
