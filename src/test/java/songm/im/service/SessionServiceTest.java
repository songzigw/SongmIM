package songm.im.service;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import songm.im.IMException;
import songm.im.entity.Session;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class SessionServiceTest {

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Test
    public void testCreate() throws IMException {
        String tokenId = "123456789";
        Session ses = sessionService.createSession(tokenId, null, null);
        
        Assert.assertNotNull(ses);
        Assert.assertEquals(ses.getTokenId(), tokenId);
        Assert.assertNotNull(ses.getSessionId());
        
        sessionService.removeSession(ses.getSessionId());
    }
    
}
