package songm.im.service;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import songm.im.IMException;
import songm.im.entity.Session;
import songm.im.entity.Token;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class SessionServiceTest {

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Test
    public void testCreate() throws IMException {
        Token t = new Token();
        t.setTokenId("123456789");
        t.setUid("100");
        t.setNick("zhangsong");
        Session ses = sessionService.create(t, null, null);
        
        Assert.assertThat(t.getTokenId(), Matchers.is(ses.getTokenId()));
        Assert.assertThat(t.getUid(), Matchers.is(ses.getUid()));
        
        sessionService.remove(ses.getSessionId());
    }
    
}
