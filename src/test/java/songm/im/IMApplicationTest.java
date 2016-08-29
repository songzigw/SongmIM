package songm.im;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application.xml" })
public class IMApplicationTest {

    @Autowired
    private IMApplication imApplication;

    @Test
    public void testStart() {
        try {
            imApplication.start();
        } catch (IMException e) {
            e.printStackTrace();
        }
        imApplication.shutdown();
    }

    @Test
    public void testRestart() {
        try {
            imApplication.restart();
        } catch (IMException e) {
            e.printStackTrace();
        }
        imApplication.shutdown();
    }

    @Test
    public void testShutdown() {
        imApplication.shutdown();
    }
}