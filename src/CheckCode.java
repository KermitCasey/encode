import java.sql.BatchUpdateException;
import java.util.ArrayList;

/**
 * Created by Kermit on 15/12/5.
 */
public class CheckCode {

	public static byte ESC_BEGIN = (byte) 0x0A;
	public static byte ESC_ESC = (byte) 0x0B;
	public static byte ESC_RES = (byte) 0x0C;
	public static byte ESC_END = (byte) 0x0D;

	public static byte FLAG_BEGIN = (byte) 0x1A;
	public static byte FLAG_ESC = (byte) 0x1B;
	public static byte FLAG_RES = (byte) 0x1C;
	public static byte FLAG_END = (byte) 0x1D;

	public static byte COMMAND_DEVICE = (byte) 0x0010;
	public static byte COMMAND_DATA = (byte) 0x0011;

	/**
	 * 测试数据
	 */
	public static byte[] getTestData() {
		byte[] data = new byte[45];

		data[0] = (byte) 0x1A;
		data[1] = (byte) 0x33;
		data[2] = (byte) 0x33;
		data[3] = (byte) 0x33;
		data[4] = (byte) 0x33;
		data[5] = (byte) 0x11;
		data[6] = (byte) 0x00;
		data[7] = (byte) 0x1B;
		data[8] = (byte) 0x0C;
		data[9] = (byte) 0x00;
		data[10] = (byte) 0x01;
		data[11] = (byte) 0x00;
		data[12] = (byte) 0x07;
		data[13] = (byte) 0x85;
		data[14] = (byte) 0x00;
		data[15] = (byte) 0x00;
		data[16] = (byte) 0x01;
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x00;
		data[19] = (byte) 0x00;
		data[20] = (byte) 0xC2;
		data[21] = (byte) 0x94;
		data[22] = (byte) 0x16;
		data[23] = (byte) 0x45;
		data[24] = (byte) 0xB6;
		data[25] = (byte) 0x65;
		data[26] = (byte) 0xB5;
		data[27] = (byte) 0x45;
		data[28] = (byte) 0x69;
		data[29] = (byte) 0xBE;
		data[30] = (byte) 0xC0;
		data[31] = (byte) 0x41;
		data[32] = (byte) 0x2D;
		data[33] = (byte) 0x00;
		data[34] = (byte) 0x00;
		data[35] = (byte) 0x00;
		data[36] = (byte) 0x78;
		data[37] = (byte) 0x56;
		data[38] = (byte) 0x34;
		data[39] = (byte) 0x12;
		data[40] = (byte) 0x02;
		data[41] = (byte) 0x00;
		data[42] = (byte) 0x00;
		data[43] = (byte) 0x00;
		data[44] = (byte) 0x1D;
		return data;
	}

	/**
	 * 发送查询设备信息命令
	 */
	public void sendDeviceInfoOrder() {

	}

	/**
	 * 发送查询设备当前数据命令
	 */
	public void sendDeviceDataOrder() {

	}

	/**
	 * 第一步判断是否符合初步条件
	 * 
	 * @param data
	 * @return
	 */
	public boolean checkDataLength(byte[] data) {
		if (data.length < 3) {
			return false;
		}

		if (!(data[0] == FLAG_BEGIN && data[data.length - 1] == FLAG_END)) {
			return false;
		}
		return true;
	}

	/**
	 * 数据转义
	 */
	public byte[] translateData(byte[] data) {
		ArrayList<Byte> arr_byte = new ArrayList<Byte>();
		for (int i = 1; i < data.length - 1; i++) {
			if (data[i] == FLAG_ESC) {
				byte temp = 0;
				if (data[i + 1] == ESC_BEGIN) {
					temp = FLAG_BEGIN;
				} else if (data[i + 1] == ESC_ESC) {
					temp = FLAG_ESC;
				} else if (data[i + 1] == ESC_RES) {
					temp = FLAG_RES;
				} else if (data[i + 1] == ESC_END) {
					temp = FLAG_END;
				}
				i++;
				arr_byte.add(temp);
			} else {
				arr_byte.add(data[i]);
			}
		}
		byte[] newData = new byte[arr_byte.size()];
		for (int i = 0; i < arr_byte.size(); i++) {
			newData[i] = arr_byte.get(i);
		}
		return newData;
	}

	/**
	 * 数据校验
	 */
	public boolean checkCRC16(byte[] data) {
		byte[] data1 = new byte[data.length - 4];
		byte[] data2 = new byte[4];
		System.arraycopy(data, 0, data1, 0, 10);
		System.arraycopy(data, 14, data1, 10, data.length - 14);

		System.arraycopy(data, 10, data2, 2, 2);
		System.arraycopy(data, 12, data2, 0, 2);

		if (CRC16.calcCrc16(data1) == ByteUtil.getInt(data2))
			return true;
		else
			return false;
	}

	/**
	 * 检查帧头是否正确
	 */
	public boolean checkDataHeader(byte[] data) {

		// 检查命令字
		if (!(data[4] == 0x11 && data[5] == 0x00)) {
			return false;
		}

		int length = ByteUtil.getShort(new byte[] { data[6], data[7] });
		if (data.length != length + 14) {
			return false;
		}

		return true;
	}

	public void getData(byte[] data) {
		System.out.println("data");
		int mode = ByteUtil.getInt(new byte[] { data[17], data[16] , data[15],data[14] });
		float data1 = ByteUtil.getFloat(new byte[] { data[21], data[20], data[19], data[18] });
		float data2 = ByteUtil.getFloat(new byte[] { data[25], data[24], data[23], data[22] });
		float data3 = ByteUtil.getFloat(new byte[] { data[29], data[28], data[27], data[26] });
		int Bat = ByteUtil.getInt(new byte[] { data[33], data[32], data[31], data[30] });
		int id = ByteUtil.getInt(new byte[] { data[37], data[36], data[35], data[34] });
		int ver = ByteUtil.getInt(new byte[] { data[41], data[40], data[39], data[38] });

	}

}
