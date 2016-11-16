/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Karaf2xStartupLaunchConfiguratorTest {
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime runtime;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IKarafRuntime karafRuntime;

	@Mock
	private IServer server;
	@InjectMocks
	private Karaf2xStartupLaunchConfigurator karaf2xStartupLaunchConfigurator;
	@Mock
	private ILaunchConfigurationWorkingCopy launchConfig;


	@Before
	public void setup() throws CoreException {
		doReturn("-Djava.ext.dirs=\"whatever\" -Djava.endorsed.dirs=whateverAgain").when(launchConfig).getAttribute(eq(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS),
				anyString());
		when(server.getRuntime()).thenReturn(runtime);
		when(server.getRuntime().loadAdapter(eq(IKarafRuntime.class), any(IProgressMonitor.class))).thenReturn(karafRuntime);
		when(karafRuntime.getVM().getInstallLocation()).thenReturn(new File("install"));
		when(runtime.getLocation().toOSString()).thenReturn("karafInstallDir");
	}

	@Test
	public void testDoOverrides() throws Exception {
		karaf2xStartupLaunchConfigurator.doOverrides(launchConfig);

		verify(launchConfig).setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
				"-Djava.ext.dirs=" + karaf2xStartupLaunchConfigurator.createExtDirValue("karafInstallDir", new File("install")) + " " + "-Djava.endorsed.dirs="
						+ karaf2xStartupLaunchConfigurator.createEndorsedDirValue("karafInstallDir", new File("install")) + " ");
	}

}
