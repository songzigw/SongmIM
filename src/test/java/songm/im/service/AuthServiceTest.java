package songm.im.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import songm.im.entity.Token;
import songm.im.utils.CodeUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class AuthServiceTest {

    @Resource(name = "authServiceImpl")
    private AuthService authService;

    @BeforeClass
    public static void beforeClass() {

    }

    @AfterClass
    public static void afterClass() {

    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAuth() {
        String key = "zhangsong";
        String secret = "123456";
        String nonce = String.valueOf(Math.random() * 1000000);
        long timestamp = System.currentTimeMillis();
        StringBuilder toSign = new StringBuilder(secret).append(nonce).append(
                timestamp);
        String signature = CodeUtils.sha1(toSign.toString());
        boolean b = authService.auth(key, nonce, signature, timestamp);
        assertThat(b, is(true));
    }

    @Test
    public void testGetToken() {
        String uid = "100";
        String nick = "zhangsong";
        String avatar = null;
        Token t = authService.getToken(uid, nick, avatar);
        assertThat(t.getUid(), is(uid));
        assertThat(t.getNick(), is(nick));
        assertThat(t.getAvatar(), is(avatar));
    }
}
