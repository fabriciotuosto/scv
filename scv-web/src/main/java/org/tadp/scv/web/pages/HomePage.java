package org.tadp.scv.web.pages;

import wicket.markup.html.WebPage;

@SuppressWarnings("serial")
public class HomePage extends WebPage
{

	public HomePage()
	{
		add(new Header("header"));
		add(new Footer("footer"));
	}
}
