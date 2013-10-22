package br.ufba.wizardplugin.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class NewModelWizardPage extends NewTypeWizardPage  {

	private IStructuredSelection selection;
	public NewModelWizardPage(boolean isClass, String pageName,IStructuredSelection selection) {
		super(isClass, pageName);
		// TODO Auto-generated constructor stub
		this.selection=selection;
	}


	
	/*private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				//   .setText(container.getFullPath().toString());
		
				
			}
		}
	}*/


	@Override
	public void createControl(Composite arg0) {
		// TODO Auto-generated method stub
		Composite container = new Composite(arg0, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		layout.verticalSpacing = 9;
		
		createContainerControls(container, 4);
		createPackageControls(container, 4);
		createTypeNameControls(container, 2);
		
		setControl(container);
	}

}
