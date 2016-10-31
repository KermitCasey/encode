
public class TestMain {
	public static void main(String[] args) {

		CheckCode code = new CheckCode();

		if (code.checkDataLength(CheckCode.getTestData())) {
			byte[] data = code.translateData(CheckCode.getTestData());
			if (code.checkCRC16(data)) {
				if (code.checkDataHeader(data)) {
					code.getData(data);
					System.out.println("success");
				}
			} else {
				System.out.println("fail2");
			}

		} else {
			System.out.println("fail1");
		}

	}
}
