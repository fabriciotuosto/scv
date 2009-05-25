package org.tadp.scv.web.application;

import org.tadp.scv.web.pages.ScvPage;

import wicket.markup.html.WebPage;
import wicket.protocol.http.WebApplication;

public class SCVApplication extends WebApplication
{

	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return ScvPage.class;
	}

}
