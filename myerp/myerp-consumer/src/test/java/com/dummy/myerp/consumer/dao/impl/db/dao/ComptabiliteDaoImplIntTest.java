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
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

public class ComptabiliteDaoImplIntTest {
	
	private static SimpleDateFormat sdf;
    private static Date date;
	private static ComptabiliteDaoImpl dao;
	private EcritureComptable vEcritureComptable;
	
	@BeforeClass
    public static void executeBeforeAll() throws ParseException, FunctionalException {
    	ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml"});
    	dao = new ComptabiliteDaoImpl();
    	sdf = new SimpleDateFormat("dd/MM/yyyy");
    	date = sdf.parse("21/12/2020");
    }
	
   @Before
    public void executeBeforeEach() {
    	vEcritureComptable = dao.getListEcritureComptable().get(0);	// AC-2016/00001
    }
	
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
    

}
