package com.dummy.myerp.business.impl.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;

public class ComptabiliteManagerIntTest {
	
	private static SimpleDateFormat sdf;
    private static Date date;
	private static ComptabiliteManagerImpl manager;
	private EcritureComptable vEcritureComptable;

	@BeforeClass
    public static void executeBeforeAll() throws ParseException, FunctionalException {
    	ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml","src/main/resources/com/dummy/myerp/business/applicationContext.xml" });
    	manager = new ComptabiliteManagerImpl();
    	sdf = new SimpleDateFormat("dd/MM/yyyy");
    	date = sdf.parse("21/12/2020");
    }
	
    @Before
    public void executeBeforeEach() {
    	vEcritureComptable = manager.getListEcritureComptable().get(0);	// AC-2016/00001
    }
    
    // ---------- METHODES GET ----------
    @Test
    public void getListCompteComptable(){
    	manager.getListCompteComptable();
  
    }
    
    @Test
    public void getListJournalComptable(){
    	manager.getListJournalComptable();

    }
    
    @Test
    public void getListEcritureComptable(){
    	manager.getListEcritureComptable();

    }
    
    // ---------- METHODE addReference ----------
    
    @Test
    public void addReference() throws TechnicalException, FunctionalException {
    	manager.addReference(vEcritureComptable);
    	assertTrue(vEcritureComptable != null);
    }
    
    //def rollBack (otherwise works first run after resetting the BDD 
    @Test
    public void addReferenceNotFoundException() throws TechnicalException, FunctionalException {
    	vEcritureComptable.setDate(date);
    	manager.addReference(vEcritureComptable);
    }
    
    // ---------- METHODES CHECK ----------
    
    @Test
    public void checkEcritureComptable() throws FunctionalException {
    	manager.checkEcritureComptable(vEcritureComptable);
    }
   
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableFunctionalException() throws FunctionalException {
    	vEcritureComptable.getListLigneEcriture().clear();
    	manager.checkEcritureComptable(vEcritureComptable);
    }
 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitCodeJournal() throws FunctionalException {
    	vEcritureComptable.getJournal().setCode("BQ");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitAnneeRef() throws FunctionalException {
    	
    	vEcritureComptable.setDate(date);
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitSequenceNeg() throws FunctionalException {
    	
    	vEcritureComptable.setReference("AC-2016/00000");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
}
