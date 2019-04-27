package com.dummy.myerp.consumer.dao.impl.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComptabiliteDaoImplIntTest {
	
	private static SimpleDateFormat sdf;
    private static Date date;
	private static ComptabiliteDaoImpl dao;
	private static EcritureComptable vEcritureComptable;
	
	@BeforeClass
    public static void executeBeforeAll() throws ParseException, FunctionalException {
    	ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml"});
    	dao = new ComptabiliteDaoImpl();
    	sdf = new SimpleDateFormat("dd/MM/yyyy");
    	date = sdf.parse("21/12/2020");
    	vEcritureComptable = dao.getListEcritureComptable().get(0);	// AC-2016/00001
    }
	
   @Before
    public void executeBeforeEach() {
    	
    }
	
   	//---------- GET METHODS ----------
    @Test
    public void getListCompteComptable(){
    	List<CompteComptable> ccList = new ArrayList<>();
    	assertTrue(ccList.size() == 0);
    	ccList = dao.getListCompteComptable();
    	assertTrue(ccList.size() != 0);
    }
    
    @Test
    public void getListJournalComptable(){
    	List<JournalComptable> jcList = new ArrayList<>();
    	assertTrue(jcList.size() == 0);
    	jcList = dao.getListJournalComptable();
    	assertTrue(jcList.size() != 0);
    }
    
    @Test
    public void getListEcritureComptable(){
    	List<EcritureComptable> ecList = new ArrayList<>();
    	assertTrue(ecList.size() == 0);
    	ecList = dao.getListEcritureComptable();
    	assertTrue(ecList.size() != 0);
    }
    
    @Test
    public void getEcritureComptableById() throws NotFoundException{
    	EcritureComptable ec = dao.getEcritureComptable(vEcritureComptable.getId());
    	assertEquals(ec.getId(), vEcritureComptable.getId());
    }

    @Test
    public void getEcritureComptableByRef() throws NotFoundException{
    	EcritureComptable ec = dao.getEcritureComptableByRef(vEcritureComptable.getReference());
    	assertEquals(ec.getId(), vEcritureComptable.getId());
    }
    
   	@Test
    public void getSequenceEcritureComptableByEcritureComptable() throws NotFoundException {
    	SequenceEcritureComptable sEC = dao.getSequenceEcritureComptableByEcritureComptable(vEcritureComptable);
    }
    
   	//---------- INSERT METHODS ----------
   	@Test
   	public void insertEcritureComptable() {
    	long sizeListEcritureComptable;
    	long sizeListEcritureComptableAfterInsert;
    	
    	vEcritureComptable.setReference("AC-2017-00001");

    	sizeListEcritureComptable = dao.getListEcritureComptable().stream().count();
    	dao.insertEcritureComptable(vEcritureComptable);
    	sizeListEcritureComptableAfterInsert = dao.getListEcritureComptable().stream().count();
    	
    	assertEquals(sizeListEcritureComptableAfterInsert, sizeListEcritureComptable + 1 );
   		
   	}
   	
   	@Test
   	public void insertNewSequenceEcritureComptable() throws NotFoundException {
   		vEcritureComptable.setDate(new Date());
   		dao.insertNewSequenceEcritureComptable(vEcritureComptable);
   		
   		SequenceEcritureComptable sEC = dao.getSequenceEcritureComptableByEcritureComptable(vEcritureComptable);
   		
   		//si une nouvelle séquence à été crée celle-ci à donc la dernière valeur 1
   		assertTrue(sEC.getDerniereValeur() == 1);
   	}
   	
   	//---------- UPDATE METHODS ----------
   	@Test
   	public void updateEcritureComptable() throws NotFoundException {
   		EcritureComptable eC;
   		vEcritureComptable.setLibelle("updated");
   		dao.updateEcritureComptable(vEcritureComptable);
   		
   		eC = dao.getEcritureComptable(vEcritureComptable.getId());
   		assertTrue(eC.getLibelle().equals("updated"));
   	}
   	
   	@Test
   	public void updateSequenceEcritureComptable() throws NotFoundException {
   		vEcritureComptable.setDate(new Date());
   		dao.updateSequenceEcritureComptable(vEcritureComptable, 2);
   		
   		SequenceEcritureComptable sEC = dao.getSequenceEcritureComptableByEcritureComptable(vEcritureComptable);
   		assertTrue(sEC.getDerniereValeur() == 2);
   	}
   	
   	@Test
   	public void zdeleteEcritureComptable() {
   		dao.deleteEcritureComptable(1);
   	}
}
