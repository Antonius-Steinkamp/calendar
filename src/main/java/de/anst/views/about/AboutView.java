package de.anst.views.about;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import lombok.extern.java.Log;

@PageTitle("About")
@Route(value = "about")
// @RouteAlias(value = "/")
@Log
public class AboutView extends VerticalLayout {

	/**
	 * the long serialVersionUID since 10.02.2024
	 */
	private static final long serialVersionUID = 1L;

	public AboutView() {
		super();
		log.info(AboutView.class.getName() + " ctor");
		setSpacing(false);
		setPadding(false);

		H2 header = new H2("Moin");
		header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
		add(header);

		// add(DetailsBasic.createDetails());

		Image img = new Image("images/ok-check.png", "my logo");
		img.setWidth("200px");
		add(img);

		add(createHtml("Demo von <a href=\"http://antonius.hopto.org\" target=\"_blank\">Antonius</a>"));
		add(new Paragraph("mit"));
		try {
			Class<?> clazz = Class.forName("com.vaadin.flow.component.Component");
			add(createHtml( "Vaadin  Version " + clazz.getPackage().getImplementationVersion()));
		} catch (ClassNotFoundException ex) {
			// isnich
		}

		try {
			Class<?> clazz = Class.forName("org.vaadin.crudui.crud.impl.GridCrud");
			add(createHtml(clazz.getSimpleName() + " Version " + clazz.getPackage().getImplementationVersion()));
		} catch (ClassNotFoundException ex) {
			// isnich
		}
		try {
			Class<?> clazz = Class.forName("org.springframework.boot.SpringBootVersion");
			add(createHtml(clazz.getSimpleName() + " Version " + clazz.getPackage().getImplementationVersion()));
		} catch (ClassNotFoundException ex) {
			// isnich
		}

		add(createHtml("Browserlocale: " + UI.getCurrent().getLocale() + " Systemlocale " + Locale.getDefault() + " Zoneid is " + TimeZone.getDefault().toZoneId()));
		add(createHtml("Java Version " + System.getProperty("java.version") + " " + System.getProperty("java.vm.name")
				+ " " + System.getProperty("java.vm.version") + " " + System.getProperty("java.vm.vendor")));
		add(createHtml("OS " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
				+ System.getProperty("os.arch")));
		add(createHtml("Memory free: " + getMemoryInfo() + " MB Memory"));

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		getStyle().set("text-align", "center");
	}

	/**
	 * Dekorieren mit einem {@code<div>}
	 * 
	 * @param String html
	 * 
	 * @return String der Übnergabestring mit einem {@code<div>} drumrum. since
	 *         13.02.2024
	 */
	private static Html createHtml(String html) {
		return new Html("<div>" + html + "</div>");
	}

	private static DecimalFormat format = new DecimalFormat("#,###");

	private static String getMemoryInfo() {
		System.gc();
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
		long totalMemory = Runtime.getRuntime().totalMemory() / 1024;

		var result = format.format(freeMemory) + " / " + format.format(totalMemory);
		// log.info(result);

		return result;
	}

}
