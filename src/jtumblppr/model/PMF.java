package jtumblppr.model;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public enum PMF {
	INSTANCE;
	private final transient PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	public static PersistenceManagerFactory get() {
		return INSTANCE.pmfInstance; 
	}
}
	