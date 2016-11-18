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

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 键值信息封裝
 * @author zhangsong
 *
 */
public class KeyInfo {

    private final String SQL1 = "UPDATE KEY_TABLE SET KEY_VALUE=KEY_VALUE+? WHERE KEY_NAME=?";
    private final String SQL2 = "SELECT KEY_VALUE FROM KEY_TABLE WHERE KEY_NAME=?";
    private final String SQL3 = "insert into KEY_TABLE(KEY_NAME, KEY_VALUE) values(?, ?)";
    
    /** 最小值 */
    private long keyMin;
    /** 最大值 */
    private long keyMax;
    /** 下一个值 */
    private long nextKey;
    /** 键值缓存数量 */
    private int poolSize;
    /** 键的名称 */
    private String keyName;
    
    public KeyInfo(int poolSize, String keyName) {
        this.poolSize = poolSize;
        this.keyName = keyName;
    }

    public long getKeyMax() {
        return keyMax;
    }

    public long getKeyMin() {
        return keyMin;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public String getKeyName() {
        return keyName;
    }
    
    public synchronized long getNextKey(DataSource dataSource,
            PlatformTransactionManager tm) {
        if (nextKey == 0 || nextKey > keyMax) {
            this.retrieveFromDB(dataSource, tm);
        }
        return nextKey++;
    }
    
    private void retrieveFromDB(final DataSource dataSource,
            PlatformTransactionManager tm) {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
                JdbcTemplate jt = new JdbcTemplate(dataSource);
                jt.update(SQL1, new Object[] { poolSize, keyName });
                long keyFormDB = jt.query(SQL2, new Object[] { keyName },
                    new ResultSetExtractor<Long>() {
                        @Override
                        public Long extractData(ResultSet rs) throws
                                SQLException, DataAccessException {
                            if (rs.next()) {
                                return rs.getLong("KEY_VALUE");
                            } else {
                                jt.update(SQL3, new Object[] {keyName, poolSize});
                                return (long) poolSize;
                            }
                        }
                    });
                keyMin = keyFormDB - poolSize + 1;
                keyMax = keyFormDB;
                nextKey = keyMin;
                return null;
            }
        });
    }
}
