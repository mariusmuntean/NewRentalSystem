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

	ImageResource liveLogo();

	@Source("Tum_logo.gif")
	ImageResource tumLogo();

	ImageResource tumOsLogo();

	ImageResource tumOsLogoTransparent();

	@Source("de/tum/os/drs/client/Resources/Images/icons/devicesDown.png")
	ImageResource devicesDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/devicesUp.png")
	ImageResource devicesUp();

	@Source("de/tum/os/drs/client/Resources/Images/icons/historyDown.png")
	ImageResource historyDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/historyUp.png")
	ImageResource historyUp();

	@Source("de/tum/os/drs/client/Resources/Images/icons/overviewDown.png")
	ImageResource overviewDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/overviewUp.png")
	ImageResource overviewUp();

	@Source("de/tum/os/drs/client/Resources/Images/icons/rentDown.png")
	ImageResource rentDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/rentUp.png")
	ImageResource rentUp();

	@Source("de/tum/os/drs/client/Resources/Images/icons/returnDown.png")
	ImageResource returnDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/returnUp.png")
	ImageResource returnUp();

	@Source("de/tum/os/drs/client/Resources/Images/icons/studentsDown.png")
	ImageResource studentsDown();

	@Source("de/tum/os/drs/client/Resources/Images/icons/studentsUp.png")
	ImageResource studentsUp();

	ImageResource imeiSample();

}
