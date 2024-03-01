/**
 * SetupDialog.java created 24.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.calendar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;

import lombok.Setter;

/**
 * SetupDialog created 24.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
public class SetupDialog extends Dialog {
	private static final long serialVersionUID = SetupDialog.class.hashCode();

	@Setter
	private Runnable closeListener;
	
	public SetupDialog(Component... components) {
		setCloseOnEsc(true);
		setHeaderTitle("Einstellungen");

		setCloseOnOutsideClick(true);
		addThemeVariants(DialogVariant.LUMO_NO_PADDING);

		add(components);
		

		Button buttonCancel = new Button("Done",e -> {
			if (closeListener != null) {
				closeListener.run();
			}
			close();
		});
		buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		getFooter().add(buttonCancel);

	}

}
