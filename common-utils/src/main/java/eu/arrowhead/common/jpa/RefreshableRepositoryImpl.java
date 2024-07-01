package eu.arrowhead.common.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

public class RefreshableRepositoryImpl<T,ID extends Serializable> extends SimpleJpaRepository<T,ID> implements RefreshableRepository<T,ID> {
	
	//=================================================================================================
	// members

	private final EntityManager entityManager;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RefreshableRepositoryImpl(final JpaEntityInformation entityInformation, final EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	@Transactional
	public void refresh(final T t) {
		entityManager.refresh(t);
	}
}
