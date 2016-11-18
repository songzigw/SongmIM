/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package songm.im.dao.keygen;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * 键值生成器
 * 
 * @author zhansong
 *
 */
public class KeyGenerator {

    private static final int POOL_SIZE = 20;
    
    private static final KeyGenerator keygen = new KeyGenerator();
    
    private Map<String, KeyInfo> keyInfos = new HashMap<String, KeyInfo>();
    
    private KeyGenerator() {}
    
    public static KeyGenerator getInstance() {
        return keygen;
    }
    
    public synchronized long getNextKey(String keyName,
            DataSource dataSource,
            PlatformTransactionManager tm) {
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
