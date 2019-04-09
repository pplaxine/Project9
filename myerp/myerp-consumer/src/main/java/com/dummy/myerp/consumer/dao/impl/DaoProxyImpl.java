package com.dummy.myerp.consumer.dao.impl;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;


/**
 * <p>Implémentation du Proxy d'accès à la couche DAO.</p>
 */
public final class DaoProxyImpl implements DaoProxy {

    // ==================== Attributs ====================
    /** {@link ComptabiliteDao} */
    private ComptabiliteDao comptabiliteDao;


    // ==================== Constructeurs ====================
    /** Instance unique de la classe (design pattern Singleton) */
    private static final DaoProxyImpl INSTANCE = new DaoProxyImpl();

    /**
     * Renvoie l'instance unique de la classe (design pattern Singleton).
     *
     * @return {@link DaoProxyImpl}
     */
    public static DaoProxyImpl getInstance() {			//protected (origin) replaced with public 
        return DaoProxyImpl.INSTANCE;
    }

    /**
     * Constructeur.
     */
    private DaoProxyImpl() {
        super();
    }


    // ==================== Getters/Setters ====================
    public ComptabiliteDao getComptabiliteDao() {		
        return this.comptabiliteDao;
    }
    public void setComptabiliteDao(ComptabiliteDao pComptabiliteDao) {
        this.comptabiliteDao = pComptabiliteDao;
    }
}