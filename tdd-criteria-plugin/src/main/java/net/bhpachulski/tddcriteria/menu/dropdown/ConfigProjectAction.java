package net.bhpachulski.tddcriteria.menu.dropdown;

import net.bhpachulski.tddcriteria.configuration.TDDCriteriaPluginConfigurationDialog;
import net.bhpachulski.tddcriteria.eclipse.util.EclipseUtil;
import net.bhpachulski.tddcriteria.file.FileUtil;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.project.util.TDDCriteriaProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ConfigProjectAction implements IObjectActionDelegate {
	
	private Shell shell;
	private TDDCriteriaProjectUtil projectPropertiesUtil = new TDDCriteriaProjectUtil();
	private FileUtil futil = new FileUtil();
	
	public ConfigProjectAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {
		TDDCriteriaPluginConfigurationDialog configDialog = new TDDCriteriaPluginConfigurationDialog(shell);
		 configDialog.create();
		 
		if (configDialog.open() == Window.OK) {
			
			IProject currentProject = EclipseUtil.getCurrentProject();
			
			TDDCriteriaProjectProperties propertiesFile = projectPropertiesUtil.verifyProjectProperties(currentProject);
			propertiesFile.setIp(configDialog.getServerIp());
			
			futil.updateProjectConfigFile(currentProject, propertiesFile);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
