package tests;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import repositories.EingangsDateiRepository;
import ausgang.AusgangsdateiInEinenObjektbaumUmformen;
import fehlerManagement.Bombe;
import flow.InDieDatei;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = batch.IntegrationUndBatchZusammen.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@DataJpaTest
public class IntegrationUndBatchZusammenTest extends FlowTestBasis {

    
    @Test
    public void normalerAblauf() throws Exception {
        einenCountdownMachen();
        überprüfeVerzeichnisse();
        überprüfeDieDatenbank();
    }
    
}
