package com.dummy.myerp.business.main;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.BusinessProxyImpl;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.DaoProxyImpl;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;

public class Main {

	
	public static void main(String[] args) throws FunctionalException {
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] {"../myerp-consumer/src/main/resources/com/dummy/myerp/consumer/applicationContext.xml","src/main/resources/com/dummy/myerp/business/applicationContext.xml" });
		
		//DaoProxy dp = DaoProxyImpl.getInstance();						//instancier via SpringBean
		//TransactionManager tr = TransactionManager.getInstance();		//instancier via SpringBean
		//BusinessProxy bp = BusinessProxyImpl.getInstance(dp, tr);
		
		//--------------------------------------
		
		BusinessProxy bp = BusinessProxyImpl.getInstance();
		
		
		
//		List<EcritureComptable> lec = bp.getComptabiliteManager().getListEcritureComptable();
//		lec.get(0).setDate(new Date());
//		bp.getComptabiliteManager().updateEcritureComptable(lec.get(0));
		
		//List<EcritureComptable> ec2 = new ArrayList<>(); 
		
		
//		for (EcritureComptable ec : bp.getComptabiliteManager().getListEcritureComptable()) {
//			bp.getComptabiliteManager().addReference(ec);
//			System.out.println(ec.getLibelle());
//			System.out.println(ec.getReference());
//			System.out.println("---------------------------");
//		} 

		List<EcritureComptable> lec = bp.getComptabiliteManager().getListEcritureComptable();
		lec.get(0).setDate(new Date());;
		
		
		bp.getComptabiliteManager().addReference(lec.get(0));
		EcritureComptable ec = lec.get(0);
		bp.getComptabiliteManager().insertEcritureComptable(ec);
		
		bp.getComptabiliteManager().addReference(lec.get(2));
		bp.getComptabiliteManager().insertEcritureComptable(lec.get(2));
		
		bp.getComptabiliteManager().addReference(lec.get(3));
		bp.getComptabiliteManager().insertEcritureComptable(lec.get(3));
		
		bp.getComptabiliteManager().addReference(lec.get(4));
		bp.getComptabiliteManager().insertEcritureComptable(lec.get(4));
		//ec.setReference("AC-2019/00005");
	
		System.out.println(lec.get(0).getReference());
		System.out.println(lec.get(2).getReference());
		System.out.println(lec.get(3).getReference());
		System.out.println(lec.get(4).getReference());
		
		
		
		
	}

}
