package edu.oregonstate.cope.eclipse;


import static org.junit.Assert.*;

import org.junit.Test;

public class GetWorkspaceIDTest {
	
	//@Test
	public void testGetWorkspaceID() {
		String workspaceID = COPEPlugin.getDefault().getWorkspaceID();
		assertNotNull(workspaceID);
		assertFalse(workspaceID.equals(""));
		String secondWorkspaceID = COPEPlugin.getDefault().getWorkspaceID();
		assertEquals(workspaceID, secondWorkspaceID);
	}
}
