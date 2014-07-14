package com.hanium.voip;
import java.util.LinkedList;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.voip.Rtp;
import org.jnetpcap.protocol.voip.Sip;


public class CaptureHandler implements PcapPacketHandler<String>{

	private int audioFormat;
	private long calleessrc;
	private long callerssrc;
	
	private Rtp rtp = new Rtp();
	private Sip sip = new Sip();
	private LinkedList<byte[]> callerPayloadData = new LinkedList<byte[]>();
	private LinkedList<byte[]> calleePayloadData = new LinkedList<byte[]>();
	private LinkedList<byte[]> allPayloadData = new LinkedList<byte[]>();

	@Override
	public void nextPacket(PcapPacket packet, String user) {
		
		if ( packet.hasHeader(sip)) {
			
		}

		if ( packet.hasHeader(rtp)) {

			if ( rtp.type() == 0 ) 
				audioFormat = 7;
			else if ( rtp.type() == 8)
				audioFormat = 6;
			
			if ( calleessrc == 0 ) {
				calleessrc = rtp.ssrc();
			} else if ( callerssrc == 0 ) {
				if ( calleessrc != rtp.ssrc() )
					callerssrc = rtp.ssrc(); 
			}
			
			allPayloadData.add(rtp.getPayload());
			
			if ( rtp.ssrc() == calleessrc)
				calleePayloadData.add(rtp.getPayload());
			if ( rtp.ssrc() == callerssrc) 
				callerPayloadData.add(rtp.getPayload());
		}
	}
	
	public int getAudioFormat() {
		return audioFormat;
	}

	public LinkedList<byte[]> getCallerPayloadData() {
		return callerPayloadData;
	}

	public LinkedList<byte[]> getCalleePayloadData() {
		return calleePayloadData;
	}

	public LinkedList<byte[]> getAllPayloadData() {
		return allPayloadData;
	}

}
