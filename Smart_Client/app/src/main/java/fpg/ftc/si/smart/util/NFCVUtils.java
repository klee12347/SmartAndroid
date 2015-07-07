package fpg.ftc.si.smart.util;

import android.nfc.tech.NfcV;

import java.io.IOException;


public class NFCVUtils {
	private NfcV mNfcV;
	private byte[] ID;
	private String UID;
	private String DSFID;
	private String AFI;
	private int blockNumber;
	private int oneBlockSize;
	private byte[] infoRmation;

	public NFCVUtils(NfcV mNfcV) throws IOException {
		this.mNfcV = mNfcV;
		ID = this.mNfcV.getTag().getId();
		byte[] uid = new byte[ID.length];
		int j = 0;
		for (int i = ID.length - 1; i >= 0; i--) {
			uid[j] = ID[i];
			j++;
		}
		this.UID = printHexString(uid);
		getInfoRmation();
	}

	public String getUID() {
		return UID;
	}

	private byte[] getInfoRmation() throws IOException {
		byte[] cmd = new byte[10];
		cmd[0] = (byte) 0x22; // flag
		cmd[1] = (byte) 0x2B; // command
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		infoRmation = mNfcV.transceive(cmd);
		blockNumber = infoRmation[12];
		oneBlockSize = infoRmation[13];
		AFI = printHexString(new byte[] { infoRmation[11] });
		DSFID = printHexString(new byte[] { infoRmation[10] });
		return infoRmation;
	}

	public String getDSFID() {
		return DSFID;
	}

	public String getAFI() {
		return AFI;
	}

	public int getBlockNumber() {
		return blockNumber + 1;
	}

	public int getOneBlockSize() {
		return oneBlockSize + 1;
	}

	public String readOneBlock(int position) throws IOException {
		byte cmd[] = new byte[11];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x20;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		cmd[10] = (byte) position;
		byte res[] = mNfcV.transceive(cmd);
		if (res[0] == 0x00) {
			byte block[] = new byte[res.length - 1];
			System.arraycopy(res, 1, block, 0, res.length - 1);
			return printHexString(block);
		}
		return null;
	}

	public String readBlocks(int begin, int count) throws IOException {
		if ((begin + count) > blockNumber) {
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();
		for (int i = begin; i < count + begin; i++) {
			data.append(readOneBlock(i));
		}
		return data.toString();
	}

	private String printHexString(byte[] data) {
		StringBuffer s = new StringBuffer();
		;
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			s.append(hex);
		}
		return s.toString();
	}

	public boolean writeBlock(int position, byte[] data) throws IOException {
		byte cmd[] = new byte[15];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x21;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		// block
		cmd[10] = (byte) 0x02;
		// value
		System.arraycopy(data, 0, cmd, 11, data.length);
		byte[] rsp = mNfcV.transceive(cmd);
		if (rsp[0] == 0x00)
			return true;
		return false;
	}
}
