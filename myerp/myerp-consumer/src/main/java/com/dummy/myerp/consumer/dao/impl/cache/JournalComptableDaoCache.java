package com.dummy.myerp.consumer.dao.impl.cache;

import java.util.List;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;


/**
 * Cache DAO de {@link JournalComptable}
 */
public class JournalComptableDaoCache {

    // ==================== Attributs ====================
    /** The List compte comptable. */
    private List<JournalComptable> listJournalComptable;


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Compte comptable dao cache.
     */
    public JournalComptableDaoCache() {
    }


    // ==================== Méthodes ====================
    /**
     * Gets by code.
     *
     * @param pCode le code du {@link JournalComptable}
     * @return {@link JournalComptable} ou {@code null}
     */
    public JournalComptable getByCode(String pCode) {
        if (listJournalComptable == null) {
            listJournalComptable = ConsumerHelper.getDaoProxy().getComptabiliteDao().getListJournalComptable();
        }

        JournalComptable vRetour = JournalComptable.getByCode(listJournalComptable, pCode);				//methode static dans model, renvoie le Journal correspondant au code si dans list passé en param
        return vRetour;
    }
}
