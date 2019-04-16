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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.BusinessProxyImpl;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.impl.DaoProxyImpl;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplUnitTest {
	
	
	@InjectMocks		 
    private ComptabiliteManagerImpl manager;
    @InjectMocks
    private DaoProxyImpl daoProxy;
    @Mock
    private ComptabiliteDaoImpl comptabiliteDaoImpl;
    @Mock
    private SequenceEcritureComptable sec;
    @Mock
	private BusinessProxyImpl businessProxyImpl;
    @Mock
    private TransactionManager trM;
	
    private static SimpleDateFormat sdf;
    private static Date date;
    private EcritureComptable vEcritureComptable;
    private String expectedRef; 
    private Integer derniereValeurSequence;
    
    @BeforeClass
    public static void executeBeforeAll() throws ParseException, FunctionalException {
    	//ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml","src/main/resources/com/dummy/myerp/business/applicationContext.xml" });
    	sdf = new SimpleDateFormat("dd/MM/yyyy");
    	date = sdf.parse("21/12/2019");
    }

    
    @Before
    public void executeBeforeEach() {
    	//config with MOCK   
    	AbstractBusinessManager.configure(businessProxyImpl, daoProxy, trM);
    	
    	//STUB 
    	vEcritureComptable = new EcritureComptable();
    	vEcritureComptable.setDate(date);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00004");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));
    }
    

    
    // ---------- METHODE addReference ----------
    //Methode addReference Test : si la référence de l'ecriture comptable suit la séquence du journal comptable auquel cette dernière appartient  
    @Test
    public void addReference() throws Exception {
    	derniereValeurSequence = 2;
    	expectedRef = "AC-2019/00003";
    	
    	//Mock the consumer layer
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	
    	//method to test 
    	manager.addReference(vEcritureComptable);
    	
    	assertEquals(expectedRef, vEcritureComptable.getReference());
    }
    
    //Methode addReference Test : si l'ecriture comptable appartient à un journal comptable ne possédant pas de séquence, création qu'une nouvelle séquence  
    @Test
    public void addReferenceSequenceJounalComptableNotFound() throws Exception {
    	expectedRef = "AC-2019/00001";
    	
    	//Mock the consumer layer : no sequence found 
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenThrow(NotFoundException.class);
    	
    	//method to test 
    	manager.addReference(vEcritureComptable);
    	
    	assertEquals(expectedRef, vEcritureComptable.getReference());
    }
    
    //Methode addReference Test : si l'ecriture comptable appartient à un journal comptable qui possède une séquence mais celle-ci est null, technical exception   
    @Test(expected = TechnicalException.class)
    public void addReferenceSequenceJounalComptableReturnNull() throws Exception {
    	
    	//Mock the consumer layer : sequence found but null
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(null);
    	
    	//method to test
    	manager.addReference(vEcritureComptable);
    }
    
    //Methode addReference Test : si l'ecriture comptable appartient à un journal comptable qui possède une séquence mais dont la VALEUR est null, technical exception   
    @Test(expected = TechnicalException.class)
    public void addReferenceSequenceJounalComptableExistButLastValueIsNull() throws Exception {
    	 derniereValeurSequence = null;
    	
    	//Mock the consumer layer : sequence found but last value is null
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	
    	//method to test
    	manager.addReference(vEcritureComptable);
    }

    // ---------- METHODE checkEcritureComptableUnit ----------
    
    //Methode checkEcritureComptableUnit Test : si respect de toutes les règles de gestion  
    @Test
    public void checkEcritureComptableUnit() throws Exception {
    	//method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si non respect d'une règles de gestion 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
    	//The Object EcritureComptable doesn't comply with all MR 
    	vEcritureComptable.getListLigneEcriture().clear();
    	
    	//method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si CompteComptable pas equilibré 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
    	//Unbalanced 
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123),null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(1234)));
        
        //method to test
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture ()  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
    	//Contains only 1 line 
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, null));
        
        //method to test
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture (Credit < 1) 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Debit() throws Exception {
    	//Contains 2 lines (Debit), but none as Credit 
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        
        //method to test
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si moins de 2 ligne d'ecriture (it < 1)  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Credit() throws Exception {
    	//Contains 2 lines (Credit), but none as Debit 
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal(123)));
       
        //method to test
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si la référence ne respecte pas le Regex prédéfini      
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeSequence() throws Exception {
        //Reference doesn't comply with Regex rules (Validators) 
    	vEcritureComptable.setReference("AC-2019-00001");
        
        //method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si le code journal de la référence ne correspond pas au code journal du l'ecriture comptable  
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeJournal() throws Exception {
    	//Journal code of reference is different from the journal code of the Ecriture Comptable 
        vEcritureComptable.setReference("BQ-2019/00001");
        
        //method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si la date de la référence ne correspond pas a la date de l'ecriture comptable    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeDate() throws Exception {
        //Year of the reference is different from year of Ecriture comptable  
    	vEcritureComptable.setReference("AC-2016/00001");
        
        //method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoi une FunctionnalException si le numéro de séquence de la référence est inférieur à 00001    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5CodeSequenceZero() throws Exception {
        //Sequence number of the reference is inferior to 1
    	vEcritureComptable.setReference("AC-2019/00000");
    	
    	//method to test
    	manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoi une FunctionnalException si une des lignes d'écritures comporte plus de 2 chiffres après la virgule    
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7() throws Exception {
    	//Wrong format entry numbers
    	vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("123.501"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null , new BigDecimal("123.501")));
    	
        //method to test
        manager.checkEcritureComptableUnit(vEcritureComptable);
    	
    }
    
    // ---------- METHODE checkEcritureComptableContext ----------
    
    //Methode checkEcritureComptableContext Test : si respect de toutes les règles de gestion
    @Test
    public void checkEcritureComptableContext() throws Exception {
    	derniereValeurSequence = 4;
    	
    	//Mock the consumer layer : Sequence is returned + EcritureComptable not found   
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	when(daoProxy.getComptabiliteDao().getEcritureComptableByRef(vEcritureComptable.getReference())).thenThrow(NotFoundException.class);
    	
    	//method to test
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableContext Test : l'écriture comptable ne possède pas de référence 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextNoRef() throws Exception {
    	vEcritureComptable.setReference(null);
    	
    	//method to test
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableContext Test : si création d'une nouvelle séquence d'un jounal comptable
    @Test
    public void checkEcritureComptableContextRG5NewSequenceCase() throws Exception {
    	vEcritureComptable.setReference("AC-2019/00001");
    	Integer derniereValeurSequence = 1;
    	
    	//Mock the consumer layer : Sequence returned is 1 (new sequence) + EcritureComptable not found
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	when(daoProxy.getComptabiliteDao().getEcritureComptableByRef(vEcritureComptable.getReference())).thenThrow(NotFoundException.class);
    	
    	//method to test
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableContext Test : si non respect RG5
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG5() throws Exception {
    	derniereValeurSequence = 5;
    	
    	//Mock the consumer layer : last value of sequence returned is higher than expected 
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);

    	//method to test
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableContext Test : renvoie une FunctionnalException si la séquence n'a pas été trouvée en base de donnée 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG5NotFoundException() throws Exception {
    	//Mock the consumer layer : No sequence found 
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenThrow(NotFoundException.class);
    	
    	//method to test
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si non respect RG6
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6EcritureComptableIdNull() throws Exception {
    	derniereValeurSequence = 4;
    	vEcritureComptable.setId(null);
    	
    	//Mock the consumer layer : Sequence is returned + EcritureComptable return same Ecriture Comptable   
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	when(daoProxy.getComptabiliteDao().getEcritureComptableByRef(vEcritureComptable.getReference())).thenReturn(vEcritureComptable);
    	
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si non respect RG6
    @Test
    public void checkEcritureComptableContextRG6SameRefAndSameId() throws Exception {
    	derniereValeurSequence = 4;
    	vEcritureComptable.setId(2);
    	EcritureComptable vEcritureComptableNew = new EcritureComptable();
    	vEcritureComptableNew.setId(2);
    	
    	//Mock the consumer layer : Sequence is returned + EcritureComptable not found   
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	when(daoProxy.getComptabiliteDao().getEcritureComptableByRef(vEcritureComptable.getReference())).thenReturn(vEcritureComptableNew);
    	
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    //Methode checkEcritureComptableUnit Test : renvoie une FunctionnalException si non respect RG6
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6SameRefButDiffId() throws Exception {
    	derniereValeurSequence = 4;
    	vEcritureComptable.setId(2);
    	EcritureComptable vEcritureComptableNew = new EcritureComptable();
    	vEcritureComptableNew.setId(3);
    	
    	//Mock the consumer layer : Sequence is returned + EcritureComptable not found   
    	when(sec.getDerniereValeur()).thenReturn(derniereValeurSequence);
    	when(daoProxy.getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(vEcritureComptable)).thenReturn(sec);
    	when(daoProxy.getComptabiliteDao().getEcritureComptableByRef(vEcritureComptable.getReference())).thenReturn(vEcritureComptableNew);
    	
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    

}
