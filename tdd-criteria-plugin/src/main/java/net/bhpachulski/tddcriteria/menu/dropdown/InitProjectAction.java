package net.bhpachulski.tddcriteria.menu.dropdown;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import net.bhpachulski.tddcriteria.eclipse.util.EclipseUtil;
import net.bhpachulski.tddcriteria.exception.TDDCriteriaException;
import net.bhpachulski.tddcriteria.project.util.TDDCriteriaProjectUtil;

public class InitProjectAction implements IObjectActionDelegate {

	private Shell shell;
	private TDDCriteriaProjectUtil projectPropertiesUtil = new TDDCriteriaProjectUtil();
	
	/**
	 * Constructor for Action1.
	 */
	public InitProjectAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			projectPropertiesUtil.verifyProjectProperties(EclipseUtil.getCurrentProject());
			
			MessageDialog.openInformation(
					shell,
					"TDDCriteria Plugin",
					"O projeto foi inicializado com sucesso.");
			
		} catch (TDDCriteriaException e) {
			MessageDialog.openInformation(
					shell,
					"TDDCriteria Plugin",
					"Erro ao inicializar Projeto.");
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
