package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.BusinessProxyImpl;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.impl.DaoProxyImpl;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {
	
	
	
	@InjectMocks		 
    private ComptabiliteManagerImpl manager;
	
    private static SimpleDateFormat sdf;
    private static Date date;
    private List<LigneEcritureComptable> lEC;
    private EcritureComptable vEcritureComptable;

    
    @BeforeClass
    public static void executeBeforeAll() throws ParseException, FunctionalException {
    	//ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml","src/main/resources/com/dummy/myerp/business/applicationContext.xml" });
    	//ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"src/main/resources/com/dummy/myerp/business/applicationContext.xml" });
    	//--- WITH MOCK ---- (bug) 
//    	lEC = new ArrayList<>();
//    	lEC.add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
//    	lEC.add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));
    	
    	
    	sdf = new SimpleDateFormat("dd/MM/yyyy");
    	date = sdf.parse("21/12/2019");

    }
    
    @Before
    public void executeBeforeEach() {
    	vEcritureComptable = new EcritureComptable();
    	vEcritureComptable.setDate(date);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));
    }
    
    @InjectMocks
    DaoProxyImpl daoProxy;
    @Mock
    ComptabiliteDaoImpl comptabiliteDaoImpl;
    @Mock
    SequenceEcritureComptable sec;
    @Mock
    BusinessProxyImpl businessProxyImpl;
    @Mock
    TransactionManager trM;
    
    // ---------- METHODE addReference ----------
    
    @Test
    public void addReference() throws Exception {
    	AbstractBusinessManager.configure(businessProxyImpl, daoProxy, trM);
    	Mockito.when(sec.getDerniereValeur()).thenReturn(2);
    	Mockito.when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	manager.addReference(vEcritureComptable);
    	System.out.println(vEcritureComptable.getReference());
    	//Mockito.verify(dao.getComptabiliteDao()).getSequenceEcritureComptableByEcritureComptable(vEcritureComptable);
    }
    
//    @Test
//    public void addReference2() throws Exception {
//    	 
//    	//Mockito.when(sec.getDerniereValeur()).thenReturn(2);
//    	//Mockito.when(dao.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
//    	//System.out.println(dao.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable).getDerniereValeur());
//    	vEcritureComptable.setDate(sdf.parse("21/12/2026"));
// 
//    	manager.addReference(vEcritureComptable);
//    	//Mockito.verify(dao.getComptabiliteDao()).getSequenceEcritureComptableByEcritureComptable(vEcritureComptable);
//    }
    
    
    

    // ---------- METHODE checkEcritureComptable ----------
    
    //Methode checkEcritureComptableUnit Test : si respect de toutes les règles de gestion  
    @Test
    public void checkEcritureComptableUnit() throws Exception {
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si non respect d'une règles de gestion 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
    	vEcritureComptable.getListLigneEcriture().clear();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si CompteComptable pas equilibré 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123),null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture ()  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture (Credit < 1) 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Debit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
        
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture (it < 1)  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Credit() throws Exception {
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si la référence ne respecte pas le Regex prédéfini      
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeSequence() throws Exception {
        vEcritureComptable.setReference("AC-2019-00001");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si le code journal de la référence ne correspond pas au code journal du l'ecriture comptable  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeJournal() throws Exception {
        vEcritureComptable.setReference("BQ-2019/00001");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si la date de la référence ne correspond pas a la date de l'ecriture comptable    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeDate() throws Exception {
        vEcritureComptable.setReference("AC-2016/00001");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoi une FunctionnalException si le numéro de séquence de la référence est inférieur à 00001    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeSequenceZero() throws Exception {
        vEcritureComptable.setReference("AC-2019/00000");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoi une FunctionnalException si une des lignes d'écritures comporte plus de 2 chiffres après la virgule    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7() throws Exception {
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("123.501"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null , new BigDecimal("123.501")));
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
  //------------------------------------ TEST UNITAIRE ??????????????? ---------------------  
  //Methode checkEcritureComptable Test :
//    public void checkEcritureComptable() throws FunctionalException {
//    	manager.checkEcritureComptable(vEcritureComptable);
//    }
//    
//    //Methode checkEcritureComptable Test :
//    @Test
//    public void checkEcritureComptable2() throws FunctionalException {
//    	vEcritureComptable.setReference("BQ-2019/00001");
//    	manager.checkEcritureComptable(vEcritureComptable);
//    }
   //-------------------------------------------------------------------------------------


}
