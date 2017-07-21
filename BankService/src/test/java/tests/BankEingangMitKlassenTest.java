package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = flow.BankEingangMitKlassen.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@DataJpaTest
public class BankEingangMitKlassenTest extends FlowTestBasis {

   
    @Test
    public void pollFindsValidFile() throws Exception {
        einenCountdownMachen();
        überprüfeVerzeichnisse();
        überprüfeDieDatenbank();
    }
}
