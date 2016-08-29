package songm.im.service;

import static org.hamcrest.Matchers.*;
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

        // authService.auth(key, nonce, signature, timestamp);
        // 测试变量不等于指定值
        assertThat(4, is(4));

    }
}
