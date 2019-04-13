package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {
	
	// créer ligne d'ecriture comptable
    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {	
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);			//if Debit not null cast from String to BigDecimal
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);		//if Credit not null cast from String to BigDecimal
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)		//Creates Libelle : Debit(or if null =0) - Credit(or if null =0) 
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),	//creates LigneEcritureComptable : param /CompteComptable /Libelle /Debit / Credit
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test		//ADDED FOR TEST
    public void getTotalDebit() {
    	EcritureComptable vEcriture;
    	vEcriture = new EcritureComptable();
    	vEcriture.setLibelle("Total Debit Test");
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, "10.50", null));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "10.50"));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, "3", "18"));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, null, null));
    	Assert.assertEquals("Test" + vEcriture.getLibelle(), vEcriture.getTotalDebit(), new BigDecimal(13.5));
    }
    
    @Test		//ADDED FOR TEST
    public void getTotalCredit() {
    	EcritureComptable vEcriture;
    	vEcriture = new EcritureComptable();
    	vEcriture.setLibelle("Total Credit Test");
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "10.50"));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, "10.50" , null));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, "3", "11.50"));
    	vEcriture.getListLigneEcriture().add(this.createLigne(1, null, null));
    	Assert.assertEquals("Test" + vEcriture.getLibelle(), vEcriture.getTotalCredit(), new BigDecimal(22));
    }
    
    @Test
    public void isEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();
        
        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();		//empty the list
        
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }
    
    
    


}
