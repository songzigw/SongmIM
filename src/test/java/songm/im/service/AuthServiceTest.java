package songm.im.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import songm.im.IMException;
import songm.im.entity.Session;
import songm.im.entity.Token;
import songm.im.utils.CodeUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class AuthServiceTest {

    @Resource(name = "authService")
    private AuthService authService;

    private Token token;
    
    public AuthServiceTest() {
        token = new Token();
        token.setUid("100");
        token.setNick("zhangsong");
        token.setAvatar("");
        token.setAppKey("zhangsong");
    }
    
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
        String secret = "1234567";
        String nonce = String.valueOf(Math.random() * 1000000);
        long timestamp = System.currentTimeMillis();
        StringBuilder toSign = new StringBuilder(secret)
                            .append(nonce).append(timestamp);
        String signature = CodeUtils.sha1(toSign.toString());
        boolean b = authService.auth(key, nonce, signature, timestamp);
        assertThat(b, is(true));
    }

    @Test
    public void testCreateToken() {
        Token t = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());

        assertThat(t.getUid(), is(token.getUid()));
        assertThat(t.getNick(), is(token.getNick()));
        assertThat(t.getAvatar(), is(token.getAvatar()));
        
        authService.deleteToken(t.getTokenId());
    }
    
    @Test
    public void testCreateTokeDontRepeat() {
        Token t = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());
        String tId1 = t.getTokenId();
        
        Token t2 = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());
        String tId2 = t2.getTokenId();
        
        assertThat(tId1.equals(tId2), is(true));
        
        authService.deleteToken(tId1);
        authService.deleteToken(tId2);
    }
    
    @Test
    public void testGetTokenByTokenId() {
        Token t = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());
        
        Token t2 = authService.getTokenByTokenId(t.getTokenId());
        
        assertThat(t2.getTokenId().equals(t.getTokenId()), is(true));
        assertThat(t2.getUid(), is(t.getUid()));
        assertThat(t2.getNick(), is(t.getNick()));
        assertThat(t2.getAvatar(), is(t.getAvatar()));
        
        authService.deleteToken(t.getTokenId());
    }
    
    @Test
    public void testDeleteToken() {
        Token t = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());
        authService.deleteToken(t.getTokenId());
        
        Token t2 = authService.getTokenByTokenId(t.getTokenId());
        
        assertThat(t2 == null, is(true));
    }
    
    @Test
    public void testOnlineSuccess() throws IMException {
        Token t = authService.createToken(token.getAppKey(),
                token.getUid(), token.getNick(), token.getAvatar());
        
        boolean isOnline = true;
        Session ses = null;
        try {
            ses = authService.online(t.getTokenId(), null, null);
        } catch (IMException e) {
            isOnline = false;
        }
        Assert.assertTrue(isOnline);
        
        authService.offline(ses.getSessionId());
        authService.deleteToken(t.getTokenId());
    }
    
    @Test
    public void testOnlineTokenError() {
        // 错误的TokenId
        String tokenId = "abcdefg";
        boolean isOnline = true;
        try {
            authService.online(tokenId, null, null);
        } catch (IMException e) {
            isOnline = false;
        }
        Assert.assertFalse(isOnline);
    }
}
