package songm.im.dao.keygen;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 
 * @author zhangsong
 * 
 */
public class KeyGenerator {
	private static KeyGenerator keygen = new KeyGenerator();

	private static final int POOL_SIZE = 1;

	private Map<String, KeyInfo> keyInfos = new HashMap<String, KeyInfo>();

	private KeyGenerator() {
	}

	public static KeyGenerator getInstance() {
		return keygen;
	}

	public synchronized long getNextKey(String keyName,
			BasicDataSource dataSource, PlatformTransactionManager tm) {
		KeyInfo keyInfo = null;
		if (keyInfos.containsKey(keyName)) {
			keyInfo = keyInfos.get(keyName);
		} else {
			keyInfo = new KeyInfo(POOL_SIZE, keyName);
			keyInfos.put(keyName, keyInfo);
		}
		return keyInfo.getNextKey(dataSource, tm);
	}
}
