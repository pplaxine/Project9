package com.dummy.myerp.consumer.db;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;


/**
 * <p>Classe mère des classes de Consumer DB</p>
 */
public abstract class AbstractDbConsumer {

// ==================== Attributs Static ====================
    /** Logger Log4j pour la classe */
    private static final Logger LOGGER = LogManager.getLogger(AbstractDbConsumer.class);


    /** Map des DataSources */
    private static Map<DataSourcesEnum, DataSource> mapDataSource;			//initialisée via méthode configure  


    // ==================== Constructeurs ====================

    /**
     * Constructeur.
     */
    protected AbstractDbConsumer() {
        super();
    }


    // ==================== Getters/Setters ====================
    /**
     * Renvoie une {@link DaoProxy}
     *
     * @return {@link DaoProxy}
     */
    protected static DaoProxy getDaoProxy() {
        return ConsumerHelper.getDaoProxy();
    }


    // ==================== Méthodes ====================
    /**
     * Renvoie le {@link DataSource} associé demandée
     *
     * @param pDataSourceId -
     * @return SimpleJdbcTemplate
     */
    protected DataSource getDataSource(DataSourcesEnum pDataSourceId) { //MYERP
    	
        DataSource vRetour = this.mapDataSource.get(pDataSourceId);
        if (vRetour == null) {
            throw new UnsatisfiedLinkError("La DataSource suivante n'a pas été initialisée : " + pDataSourceId);
        }
        return vRetour;
    }


    /**
     * Renvoie le dernière valeur utilisé d'une séquence
     *
     * <p><i><b>Attention : </b>Méthode spécifique au SGBD PostgreSQL</i></p>
     *
     * @param <T> : La classe de la valeur de la séquence.
     * @param pDataSourcesId : L'identifiant de la {@link DataSource} à utiliser
     * @param pSeqName : Le nom de la séquence dont on veut récupérer la valeur
     * @param pSeqValueClass : Classe de la valeur de la séquence
     * @return la dernière valeur de la séquence
     */
    protected <T> T queryGetSequenceValuePostgreSQL(DataSourcesEnum pDataSourcesId,
                                                    String pSeqName, Class<T> pSeqValueClass) {

        JdbcTemplate vJdbcTemplate = new JdbcTemplate(getDataSource(pDataSourcesId));
        String vSeqSQL = "SELECT last_value FROM " + pSeqName;
        T vSeqValue = vJdbcTemplate.queryForObject(vSeqSQL, pSeqValueClass);

        return vSeqValue;
    }


    // ==================== Méthodes Static ====================
    /**
     * Méthode de configuration de la classe
     *
     * @param pMapDataSource -
     */
    public static void configure(Map<DataSourcesEnum, DataSource> pMapDataSource) {			//permet de recup toutes les dataSources de pMapDataSource
        // On pilote l'ajout avec l'Enum et on ne rajoute pas tout à l'aveuglette...
        //   ( pas de AbstractDbDao.mapDataSource.putAll(...) )
        Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<>(DataSourcesEnum.values().length);		//map<Enum, DataSource>		//initialisation d'un Hashmap de la taille de l'enum
       
        DataSourcesEnum[] vDataSourceIds = DataSourcesEnum.values();		//stock dans un tab d'enum, les valeur de l'enum DataSourceEnum
        for (DataSourcesEnum vDataSourceId : vDataSourceIds) {				//pour chaque Enum du tab d'Enum 
            DataSource vDataSource = pMapDataSource.get(vDataSourceId);		//recup dans map (passée en param) la valeur(dataSource) dont key = Enum (cela retourne null si pas dans pMapDataSource) 
            // On test si la DataSource est configurée
            // (NB : elle est considérée comme configurée si elle est dans pMapDataSource mais à null)
            if (vDataSource == null) {								//configurée si dataSource est null
                if (!pMapDataSource.containsKey(vDataSourceId)) {		//pas initialisée si map ne contient pas la key Enum 
                    LOGGER.error("La DataSource " + vDataSourceId + " n'a pas été initialisée !");
                }
            } else {												
                vMapDataSource.put(vDataSourceId, vDataSource);		//si La dataSource n'est pas null on l'ajoute à la vMapDataSource (initialisé dans cette class) 
            }
        }
        mapDataSource = vMapDataSource;								//map de dataSource est initialisé
    }
    
    // ==================== Méthodes Date Helper ==================== 
    
    protected LocalDate dateToLocalDate(Date date) {
    	
    	return Instant.ofEpochMilli(date.getTime())
    		      .atZone(ZoneId.systemDefault())
    		      .toLocalDate();
    }
}
