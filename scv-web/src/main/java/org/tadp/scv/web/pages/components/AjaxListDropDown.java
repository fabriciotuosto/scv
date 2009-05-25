/**
 * 
 */
package org.tadp.scv.web.pages.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.web.behavoirs.OnChangeBehavoir;

import wicket.markup.html.WebPage;
import wicket.markup.html.form.DropDownChoice;
import wicket.model.PropertyModel;

/**
 * @author FTUOST
 * 
 */
@SuppressWarnings("serial")
public class AjaxListDropDown extends DropDownChoice {

	private FileTree fileTree = null;
	private OnChangeBehavoir behavoir = null;
	private String selectedProyect = null;
	private Map<String,DirectoryVersionable> choices = null;

	public AjaxListDropDown(String id) {
		super(id);
		createBehavoir();
		add(behavoir);
	}

	public void setFileTree(FileTree fileTree) {
		this.fileTree = fileTree;
		behavoir.setFileTree(this.fileTree);
	}

	public FileTree getFileTree() {
		return fileTree;
	}

	public String getSelectedProyect() {
		return selectedProyect;
	}

	public void setSelectedProyect(String selectedProyect) {
		this.selectedProyect = selectedProyect;
	}

	private void createBehavoir() {
		behavoir = new OnChangeBehavoir("onchange");
		behavoir.setListenObject(this);
		setModel(new PropertyModel(this, "selectedProyect"));
	}

	@SuppressWarnings("unchecked")
	public void setChoices(Map<String, DirectoryVersionable> map)
	{
		this.choices = map;
		List<String> strings = new ArrayList<String>();
		for (String string : choices.keySet())
		{
			strings.add(string);
		}
		setChoices(strings);
	}

	public Map<String, DirectoryVersionable> getMChoices()
	{
		return this.choices;
	}

	public void setContainer(WebPage page)
	{
		behavoir.setContainer(page);
	}
	
}
