package de.tum.os.drs.client.model;

/**
 * Interface to be implemented by all classes that provide authentication with different providers.
 * 
 * @author Marius
 * 
 */
public interface IAuthenticator {
	
	/**
	 * Initiates an OAuth2 authentication process.
	 */
	public void login();
}
