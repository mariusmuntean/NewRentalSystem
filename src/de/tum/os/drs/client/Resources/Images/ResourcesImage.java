/**
 * 
 */
package de.tum.os.drs.client.Resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * @author Marius
 * 
 */
public interface ResourcesImage extends ClientBundle {

	public static final ResourcesImage INSTANCE = GWT.create(ResourcesImage.class);

	ImageResource clearIcon();

	ImageResource clearIconSmall();

	ImageResource facebookLogo();

	ImageResource facebookLogoSmall();

	ImageResource generic();

	ImageResource googleLogo();

	ImageResource loader();

	ImageResource twitterLogoSmall();

	ImageResource tumLogoSmall();

}
