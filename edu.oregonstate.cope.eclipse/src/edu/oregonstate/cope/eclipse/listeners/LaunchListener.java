package edu.oregonstate.cope.eclipse.listeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;

import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.eclipse.COPEPlugin;

/**
 * Listens to launches that are being added to the LaunchManager.
 * 
 * It records both the test launch and it's end. The launch timestamp
 * serves as a unique ID to connect the beginning event with the end
 * event.
 *   
 * @author Caius Brindescu
 *
 */
public class LaunchListener implements ILaunchListener {

	private ClientRecorder clientRecorder = COPEPlugin.getDefault().getClientRecorder();

	@Override
	public void launchRemoved(ILaunch launch) {
		clientRecorder.recordLaunchEnd(launch.getAttribute(DebugPlugin.ATTR_LAUNCH_TIMESTAMP));
	}

	@Override
	public void launchChanged(ILaunch launch) {
	}

	@Override
	public void launchAdded(ILaunch launch) {
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
		String mainType = getMainType(launchConfiguration);
		String launchMode = launch.getLaunchMode();
		String launchTime = launch.getAttribute(DebugPlugin.ATTR_LAUNCH_TIMESTAMP);
		String launchName = "";
		String launchFile = "";
		try {
			if (launchConfiguration.isLocal()) {
				launchName = launchConfiguration.getName();
				launchFile = new String(getLaunchFileContents(launchName));
			} else {
				
			}
			if (launchMode.equals(ILaunchManager.RUN_MODE)) {
				clientRecorder.recordNormalLaunch(launchTime, launchName, launchFile, launchConfiguration.getMemento(), launchConfiguration.getAttributes());
			}
			if (launchMode.equals(ILaunchManager.DEBUG_MODE))
				clientRecorder.recordDebugLaunch(launchTime, launchName, launchFile, launchConfiguration.getMemento(), launchConfiguration.getAttributes());
		} catch (CoreException e) {
			COPEPlugin.getDefault().getLogger().error(this, "Error retrievieng the launch config", e);
		} catch (IOException e) {
			COPEPlugin.getDefault().getLogger().error(this, "Could not read .launch file", e);
		}
	}

	private byte[] getLaunchFileContents(String launchName) throws IOException {
		return Files.readAllBytes(Paths.get(getWorkspaceLocation(), getLaunchFileRelativeToWorkspace(launchName)));
	}

	private String getWorkspaceLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().makeAbsolute().toPortableString();
	}

	private String getLaunchFileRelativeToWorkspace(String launchName) {
		return ".metadata/.plugins/org.eclipse.debug.core/.launches/" + launchName + ".launch";
	}
	
	private boolean isTestLauch(ILaunchConfiguration launchConfiguration) {
		try {
			String attribute = launchConfiguration.getAttribute("org.eclipse.jdt.junit.TEST_KIND", "");
			if (!attribute.equals(""))
				return true;
		} catch (CoreException e) {
		}
		return false;
	}

	private String getMainType(ILaunchConfiguration launchConfiguration) {
		try {
			return launchConfiguration.getAttribute("org.eclipse.jdt.launching.MAIN_TYPE", "");
		} catch (CoreException e) {
		}
		return "";
	}
}