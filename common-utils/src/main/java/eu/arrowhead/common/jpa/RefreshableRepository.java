package eu.arrowhead.common.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface RefreshableRepository<T,ID extends Serializable> extends JpaRepository<T,ID> {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void refresh(final T t);
}