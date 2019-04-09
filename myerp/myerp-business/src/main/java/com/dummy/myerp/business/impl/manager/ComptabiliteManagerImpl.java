package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Methodes ====================
    

    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();		//appel via abstractBusinessManager (DaoProxy)  
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException{
    		
	    	String codeJournalComptable = pEcritureComptable.getJournal().getCode();
	    	String anneeEcritureComptable = Integer.toString(dateToLocalDate(pEcritureComptable.getDate()).getYear());
	    	String numeroDeSequence ="";
	    	
    	try {
    		//verification d'existence de sequence d'ecriture comptable 
			SequenceEcritureComptable sequenceEcritureComptable = getDaoProxy().getComptabiliteDao().getSequenceEcritureComptableByEcritureComptable(pEcritureComptable);
			
			if(sequenceEcritureComptable != null ) {
				
				//MàJ de la dernière valeur de la séquence 
				if(sequenceEcritureComptable.getDerniereValeur() != null && sequenceEcritureComptable.getDerniereValeur() != 0 ) {
					numeroDeSequence = new DecimalFormat("00000").format(sequenceEcritureComptable.getDerniereValeur()+1);
					sequenceEcritureComptable.setDerniereValeur(Integer.valueOf(numeroDeSequence));
				}else {
					sequenceEcritureComptable.setDerniereValeur(1);
				}
				
				//création de la référence 
				String reference = referenceBuilder(codeJournalComptable, anneeEcritureComptable, numeroDeSequence);
				
				//MàJ de l'ecriture 
				pEcritureComptable.setReference(reference);
				
				//Enregistrer la valeur en persistance	
				updateSequenceEcritureComptable(pEcritureComptable, sequenceEcritureComptable.getDerniereValeur());
			}
			
		} catch (NotFoundException e) {
			// LOG A INSERER 
			
			//Séquence d'ecriture comptable inexistante (ref journal et année de l'écriture comptable) ---> création d'une nouvelle séquence
				
			
			//création de la référence (fixé à 00001 car nouvelle séquence) 
			String reference = referenceBuilder(codeJournalComptable, anneeEcritureComptable, "00001");
			
			//MàJ de l'ecriture 
			pEcritureComptable.setReference(reference);
			
			//Enregistre nouvelle séquence d'ecriture comptable en persistance
			this.insertSequenceEcritureComptable(pEcritureComptable);
		}
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);		//recup Validator via AbstractBusiness, test pEcritureComptable. Si existe ContraintViolation mis dans un Set 
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {			//pour chaque ligne d'ecriture comptable 
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0) {		//defaultIfNull: renvoie l'autre si un des param null  
                vNbrCredit++;	// si credit d'une ligne comptable est egal à null, BigDecimal.Zero est renvoyé. 0 est renvoyé par compareTo. 0 == 0 donc on incremente pas.
                				// si credit d'une ligne comptable est pas null, sa valeur est renvoyée.  valeur !=0 donc on incremente 
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(), BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        
    	// ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {			//si EcritureComptable possède une ref 
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(pEcritureComptable.getReference());			//recherche en BDD EcritureComptable avec cette ref (si continue c'est que une ecriture existe (sinon catch)

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null																			//si l'ecritureComptable existe en BDD (c'est qu'elle a un id), si celle passé en param n'en possède pas c'est quelle n'est pas encore persistée mais possède pourtant la meme ref.   
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {													//si l'ecritureComptable possède id mais qui es différent de celle récupe en base de donnée, cela signifie meme ref pour 2 ecritureComptable (dont une déjà persistée)
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    
    /**						
     * {@inheritDoc}
     */
	@Override
	public void insertSequenceEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {				//ADDED FOR TEST 
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
        	getDaoProxy().getComptabiliteDao().insertNewSequenceEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
		
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public void updateSequenceEcritureComptable(EcritureComptable pEcritureComptable, int derniereValeurSequence) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
        	getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pEcritureComptable, derniereValeurSequence);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }






}
