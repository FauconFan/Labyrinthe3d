package src.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public class Menu extends VBox
{
	protected Rectangle2D primaryScreenBounds;

	public Menu()
	{
		super();

		setPadding(new Insets(10));
		setSpacing(15);
		setAlignment(Pos.CENTER);

		primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	}

	protected Button addButton(String name)
	{
		Button b = new Button(name);

		getChildren().add(b);
		return (b);
	}
}
