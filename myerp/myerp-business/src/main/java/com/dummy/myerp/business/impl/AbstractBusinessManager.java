package com.dummy.myerp.business.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;


/**
 * <p>Classe mère des Managers</p>
 */
public abstract class AbstractBusinessManager {

    /** Le Proxy d'accès à la couche Business */
    private static BusinessProxy businessProxy;
    /** Le Proxy d'accès à la couche Consumer-DAO */
    private static DaoProxy daoProxy;
    /** Le gestionnaire de Transaction */
    private static TransactionManager transactionManager;


    // ==================== Constructeurs ====================

    /**
     * Méthode de configuration de la classe
     *
     * @param pBusinessProxy      -
     * @param pDaoProxy           -
     * @param pTransactionManager -
     */
    public static void configure(BusinessProxy pBusinessProxy,
                                 DaoProxy pDaoProxy,
                                 TransactionManager pTransactionManager) {
        businessProxy = pBusinessProxy;
        daoProxy = pDaoProxy;
        transactionManager = pTransactionManager;
    }


    // ==================== Getters/Setters ====================

    /**
     * Renvoie le Proxy d'accès à la couche Business
     *
     * @return {@link BusinessProxy}
     */
    protected BusinessProxy getBusinessProxy() {
        return businessProxy;
    }


    /**
     * Renvoie le Proxy d'accès à la couche Consumer-DAO
     *
     * @return {@link DaoProxy}
     */
    protected DaoProxy getDaoProxy() {
        return daoProxy;
    }


    /**
     * Renvoie le gestionnaire de Transaction
     *
     * @return TransactionManager
     */
    protected TransactionManager getTransactionManager() {
        return transactionManager;
    }


    /**
     * Renvoie un {@link Validator} de contraintes
     *
     * @return Validator
     */
    protected Validator getConstraintValidator() {
        Configuration<?> vConfiguration = Validation.byDefaultProvider().configure();
        ValidatorFactory vFactory = vConfiguration.buildValidatorFactory();
        Validator vValidator = vFactory.getValidator();
        return vValidator;
    }
    
    // ==================== Reference builder ====================
    
    protected String referenceBuilder(String codeJournalComptable, String anneeEcritureComptable, String numeroDeSequence) {
    	
    	StringBuilder reference = new StringBuilder(); 
		reference.append(codeJournalComptable);
		reference.append("-");
		reference.append(anneeEcritureComptable);
		reference.append("/");
		reference.append(numeroDeSequence);
    	
    	return reference.toString();
    }
    
    // ==================== Date converter Helper ==================== 
    
    protected LocalDate dateToLocalDate(Date date) {
    	
    	return Instant.ofEpochMilli(date.getTime())
    		      .atZone(ZoneId.systemDefault())
    		      .toLocalDate();
    }
}
