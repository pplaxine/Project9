package com.dummy.myerp.business.impl.manager;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.DaoProxyImpl;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

public class MonPetitTest {

	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
	EcritureComptable ec = Mockito.mock(EcritureComptable.class);
	

	
	@Test(expected = FunctionalException.class)
	public void methodeTest() throws NotFoundException, FunctionalException {
		manager.checkEcritureComptableUnit(ec);
	}

	@Test
	public void methodeTest2() throws NotFoundException, FunctionalException {
		when(ec.getReference()).thenReturn("Hello");
		assertEquals("Hello", ec.getReference());
	}
	
}
