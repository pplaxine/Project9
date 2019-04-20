package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.TechnicalException;

public class ComptabiliteManagerIntTest {
	
	private static SimpleDateFormat sdf;
    private static Date date;
	private static ComptabiliteManagerImpl manager;
	private EcritureComptable vEcritureComptable;
	
	private final int SEQUENCE_NUMBER8_SIZE = 5;

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
    	assertFalse(vEcritureComptable.getReference() == null);
    	manager.addReference(vEcritureComptable);
    	assertTrue(vEcritureComptable.getReference() != null);
    }
    
    //def rollBack (otherwise works first run after resetting the BDD 
    @Test
    public void addReferenceNewSequence() throws TechnicalException, FunctionalException {
    	vEcritureComptable.setDate(date);
    	manager.addReference(vEcritureComptable);
    	//sequence number part of the reference 
    	String refNumber = vEcritureComptable.getReference().substring(vEcritureComptable.getReference().length() - SEQUENCE_NUMBER8_SIZE);
    	assertTrue("Le numéro de séquence " + refNumber + " de la référence devrai être 00001",refNumber.equals("00001"));
    }
    
    // ---------- METHODES CHECK ----------
    // -- checkEcritureComptable
    @Test
    public void checkEcritureComptable() throws FunctionalException {
    	manager.checkEcritureComptable(vEcritureComptable);
    }
   
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableFunctionalException() throws FunctionalException {
    	vEcritureComptable.getListLigneEcriture().clear();
    	manager.checkEcritureComptable(vEcritureComptable);
    }
 
    // -- checkEcritureComptableUnit
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
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitLigneCreditLessThanOne() throws FunctionalException {
    	List<LigneEcritureComptable> lecList = vEcritureComptable.getListLigneEcriture();
    	LigneEcritureComptable lec =  lecList.get(0);
    	lec.setDebit(null);
    	lec.setDebit(new BigDecimal("35"));
    	vEcritureComptable.getListLigneEcriture().clear();
    	vEcritureComptable.getListLigneEcriture().add(lec);
    	vEcritureComptable.getListLigneEcriture().add(lec);
    	
    	manager.checkEcritureComptable(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitLigneDebitLessThanOne() throws FunctionalException {
		List<LigneEcritureComptable> lecList = vEcritureComptable.getListLigneEcriture();
		LigneEcritureComptable lec =  lecList.get(0);
		lec.setDebit(null);
		lec.setCredit(new BigDecimal("35"));
		vEcritureComptable.getListLigneEcriture().clear();
		vEcritureComptable.getListLigneEcriture().add(lec);
		vEcritureComptable.getListLigneEcriture().add(lec);
	
		manager.checkEcritureComptable(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitECNonEq() throws FunctionalException {
    	
    	LigneEcritureComptable lec = vEcritureComptable.getListLigneEcriture().get(0);
    	lec.setDebit(new BigDecimal("101.05"));
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws FunctionalException {
    	vEcritureComptable.setReference("AC-2016-00004");
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    // -- checkEcritureComptableContext
    
    @Test
    public void checkEcritureComptableContex() throws FunctionalException {
    	vEcritureComptable.setDate(date);
    	vEcritureComptable.setReference("AC-2020/00001");
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextNoRef() throws FunctionalException {
    	vEcritureComptable.setReference(null);
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRefNotFollowingSequence() throws FunctionalException {
    	vEcritureComptable.setReference("AC-2016/00018");
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRefNotFoundSequence() throws FunctionalException {
    	vEcritureComptable.getJournal().setCode("BA");
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRefAlreadyExistButNoId() throws FunctionalException {
    	vEcritureComptable = manager.getListEcritureComptable().get(1); //VE-2016/00002
    	vEcritureComptable.setReference("VE-2016/00004");
    	vEcritureComptable.setId(null);
    	
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRefAlreadyExistAndDiffId() throws FunctionalException {
    	vEcritureComptable = manager.getListEcritureComptable().get(1); //VE-2016/00002
    	vEcritureComptable.setReference("VE-2016/00004");
    	vEcritureComptable.setId(5);
    	
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    @Test
    public void checkEcritureComptableContexNotFoundEcritureComptable() throws FunctionalException {
    	vEcritureComptable.setReference("AC-2021/00001");
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    // ---------- INSERT ----------
    @Test
    public void InsertEcritureComptable() throws FunctionalException, TechnicalException {
    	long sizeListEcritureComptable;
    	long sizeListEcritureComptableAfterInsert;
    	
    	
    	
    	sizeListEcritureComptable = manager.getListEcritureComptable().stream().count();
    	manager.addReference(vEcritureComptable);
    	manager.insertEcritureComptable(vEcritureComptable);
    	sizeListEcritureComptableAfterInsert = manager.getListEcritureComptable().stream().count();
    	
    	assertEquals(sizeListEcritureComptableAfterInsert, sizeListEcritureComptable + 1 );
    }
    
    // ---------- UPDATE ----------
    @Test
    public void updateEcritureComptable() throws FunctionalException, TechnicalException {
    	int size;
    	
    	List<EcritureComptable> ecList = manager.getListEcritureComptable();
    	size = ecList.size();
    	EcritureComptable ec = ecList.get(1);
    	ec.setLibelle("Updated libelle");
    	
    	manager.updateEcritureComptable(ec);
    	
    	List<EcritureComptable> ecList2 = manager.getListEcritureComptable();
    	EcritureComptable ec2 = ecList2.get(size -1 );
    	assertTrue("Le libelle de l'ecriture comptable " + ec2.getLibelle() + " n'a pas été mise à jour" ,ec2.getLibelle().equals("Updated libelle"));
    	
    }
    
    
}
