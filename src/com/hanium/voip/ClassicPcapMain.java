package com.hanium.voip;
import java.util.LinkedList;

import org.jnetpcap.Pcap;

public class ClassicPcapMain {

	public static void main(String[] args) {

		final StringBuilder errbuf = new StringBuilder();
		final String file = "voip.pcap";
		CaptureHandler capture = new CaptureHandler();
		System.out.printf("Opening file for reading: %s%n", file);

		Pcap pcap = Pcap.openOffline(file, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());
			return;
		}
		
		try {
			pcap.loop(Pcap.LOOP_INFINITE, capture, "jNetPcap rocks!");
		} finally {
			
			pcap.close();

			LinkedList<byte[]> callerPayloadList = capture.getCallerPayloadData();
			LinkedList<byte[]> calleePayloadList = capture.getCalleePayloadData();
			
			int length1 = 0;
			int length2 = 0;
			for (int i = 0; i < callerPayloadList.size(); i++) {
				byte[] data = callerPayloadList.get(i);
				length1 += data.length;
			}
			for (int i = 0; i < calleePayloadList.size(); i++) {
				byte[] data = calleePayloadList.get(i);
				length2 += data.length;
			}

			byte[] soundData = new byte[length1];
			byte[] soundData2 = new byte[length2];
			int index = 0;
			for (int i = 0; i < callerPayloadList.size(); i++) {
				byte[] data = callerPayloadList.get(i);

				for (int j = 0; j < data.length; j++) {
					soundData[index++] = data[j];
				}
			}
			
			index = 0;
			for (int i = 0; i < calleePayloadList.size(); i++) {
				byte[] data = calleePayloadList.get(i);

				for (int j = 0; j < data.length; j++) {
					soundData2[index++] = data[j];
				}
			}

			WavConverter converter = new WavConverter();
			converter.convertSoundDataToWave("aaa.wav", soundData, capture.getAudioFormat());
			converter.convertSoundDataToWave("aaa2.wav", soundData2, capture.getAudioFormat());

		}
	}

}
