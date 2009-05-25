package org.tadp.scv.web.behavoirs;

import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.web.pages.components.AjaxListDropDown;
import org.tadp.scv.web.pages.components.FileTree;
import org.tadp.scv.web.pages.model.FileSystemNodeTreeModel;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import wicket.markup.html.WebPage;

	@SuppressWarnings("serial")
	public class OnChangeBehavoir extends AjaxFormComponentUpdatingBehavior
	{

		private FileTree fileTree;
		private AjaxListDropDown component;
		private WebPage page;

		public OnChangeBehavoir(String event)
		{
			super(event);
		}

		public OnChangeBehavoir(String event, FileTree comp)
		{
			this(event);
			fileTree = comp;
		}
		
		
		public OnChangeBehavoir(String event, FileTree fileTree,AjaxListDropDown comp)
		{
			this(event);
			this.fileTree = fileTree;
			this.component = comp;
		}
		@Override
		protected void onUpdate(AjaxRequestTarget target)
		{
			DirectoryVersionable file = component.getMChoices().get(component.getSelectedProyect());
			FileSystemNodeTreeModel treeModel = new FileSystemNodeTreeModel(file);
			FileTree tempFileTree = new FileTree(fileTree.getId(),treeModel.createTreeModel());
			tempFileTree.setPanel(fileTree.getPanel());
			fileTree = tempFileTree;
			page.replace(fileTree);
			target.addComponent(fileTree);
		}

		public FileTree getFileTree() {
			return fileTree;
		}

		public void setFileTree(FileTree fileTree) {
			this.fileTree = fileTree;
		}
		
		public void setListenObject(AjaxListDropDown comp)
		{
			this.component = comp;
		}
		public void setContainer(WebPage page) {
			this.page = page;
		}

		
	}