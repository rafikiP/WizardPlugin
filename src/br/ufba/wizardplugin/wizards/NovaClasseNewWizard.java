package br.ufba.wizardplugin.wizards;

import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "java". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NovaClasseNewWizard extends Wizard implements INewWizard {
	private NovaClasseWizardPage page;
	private ISelection selection;
	private NewClassWizardPage newClassPage;

	/**
	 * Constructor for NovaClasseNewWizard.
	 */
	public NovaClasseNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		//page = new NovaClasseWizardPage(selection);
		//addPage(page);
		newClassPage=new NewClassWizardPage();
		newClassPage.init((IStructuredSelection)selection);
		addPage(newClassPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String className = newClassPage.getTypeName();
		final String packageName = newClassPage.getPackageText(); 
		final String sourceName= newClassPage.getPackageFragmentRootText();
		//final String containerName=newClassPage.get
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(className, sourceName,packageName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 * @throws CoreException 
	 */

	
	public void prepare(IFolder folder) throws CoreException {
	    if (!folder.exists()) {
	    	IFolder f;
	    	try
	    	{
	    		 f=(IFolder) folder.getParent();
	    	}catch(Exception e) {
	    		folder.create(false, false, null);
	    		return;
	    	}
	        prepare(f);
	        folder.create(false, false, null);
	    }
	}
	
	private void doFinish(String className,String SourceName, String packageName,
			IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + className, 2);
		String filePath=SourceName+"/"+packageName.replace(".", "/");
	//	if(!SourceName.contains("src"))
	//	{
	//		throwCoreException("No src folder found!");
	//		return;
	//	}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder= root.getFolder(new Path(filePath));
		prepare(folder);
	
		IResource resource = root.findMember(new Path(filePath));
		
		
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + filePath
					+ "\" does not exist.");
		}

		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(className+".java"));
		
		try {
			InputStream stream = openContentStream(className,packageName);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * We will initialize file contents with a sample text.
	 */

	public InputStream openContentStream(String classe,String packageName) {

		final String newline = "\n"; // System.getProperty("line.separator");
		String line;
		StringBuffer sb = new StringBuffer();

		InputStream input = this.getClass().getResourceAsStream("template");
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		try {
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll("\\$class", classe);
				line=line.replaceAll("\\$package", packageName);
				sb.append(line);
				sb.append(newline);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ByteArrayInputStream(sb.toString().getBytes());

	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "WizardPlugin", IStatus.OK,
				message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}