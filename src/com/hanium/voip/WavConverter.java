package com.hanium.voip;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


public class WavConverter {

	/**
	 * <code>RIFF</code> block identifier.
	 */
	private byte[] RIFF = toBytes("RIFF");
	/**
	 * <code>WAVE</code> block identifier.
	 */
	private byte[] WAVE = toBytes("WAVE");
	/**
	 * <code>fmt </code> block identifier.
	 */
	private byte[] FMT = toBytes("fmt ");
	/**
	 * <code>data</code> block identifier.
	 */
	private byte[] DATA = toBytes("data");

	private short numChannels = 1;
	private short bitPerSample = 8;
	private int sampleRateInHz = 8000;
	
	public void convertSoundDataToWave(String filePath, byte[] data, int audioFormat) {

		try {
			
			final RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			file.setLength(data.length + 44);
			final FileChannel fc = file.getChannel();

			fc.position(44);
			ByteBuffer soundBuffer = ByteBuffer.allocateDirect(data.length);
			soundBuffer.put(data);
			soundBuffer.clear();
			fc.write(soundBuffer);

			ByteBuffer waveHeader = ByteBuffer.allocateDirect(44);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(RIFF);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(data.length + 36);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(WAVE);
			waveHeader.put(FMT);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(16);
			waveHeader.putShort((short) audioFormat);
			waveHeader.putShort(numChannels);
			waveHeader.putInt(sampleRateInHz);
			waveHeader.putInt(sampleRateInHz * numChannels * bitPerSample / 8);
			waveHeader.putShort((short) (numChannels * bitPerSample / 8));
			waveHeader.putShort(bitPerSample);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(DATA);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(data.length);
			waveHeader.clear();

			fc.position(0);
			while (waveHeader.hasRemaining() && fc.isOpen()) {
				fc.write(waveHeader);
			}
			file.close();
			fc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] toBytes(String s) {
		try {
			return s.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			// unlikely to happen
			throw new IllegalStateException("ASCII encoding is not available", e);
		}
	}
	
	
}
