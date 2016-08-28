package songm.im.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class AuthServiceTest {

    @Resource
    private AuthService authService;

    @Test
    public void authTest() {

        // authService.auth(key, nonce, signature, timestamp);
    }

}
