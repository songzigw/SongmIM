package songm.im;


public class SSOApplicationTest {

    public static void main(String[] args) {
        IMApplication ima = new IMApplication();
        try {
            ima.start();
        } catch (IMException e) {
            e.printStackTrace();
        }
    }
}
