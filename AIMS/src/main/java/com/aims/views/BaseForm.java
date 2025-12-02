package com.aims.views;

import java.io.IOException;
import java.util.Hashtable;

import com.aims.controller.BaseController;
import com.aims.views.home.HomeForm;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseForm extends FXMLForm {

	private Scene scene;
	private BaseForm prev;
	protected final Stage stage;
	public Stage getStage() {
		return stage;
	}
	protected HomeForm homeScreenHandler;
	protected Hashtable<String, String> messages;
	private BaseController bController;

	protected BaseForm(String screenPath) throws IOException {
		super(screenPath);
		this.stage = new Stage();
	}

	protected BaseForm() throws IOException {
		super();
		this.stage = new Stage();
	}

	public void setPreviousScreen(BaseForm prev) {
		this.prev = prev;
	}

	public BaseForm getPreviousScreen() {
		return this.prev;
	}

	@SuppressWarnings("exports")
	public BaseForm(Stage stage, String screenPath) throws IOException {
		super(screenPath);
		this.stage = stage;
	}

	public void show() {
		if (this.scene == null) {
			this.scene = new Scene(this.content);
		}
		this.stage.setScene(this.scene);
		this.stage.show();
	}

	public void setScreenTitle(String string) {
		this.stage.setTitle(string);
	}

	@SuppressWarnings("exports")
	public void setBController(BaseController bController){
		this.bController = bController;
	}

	@SuppressWarnings("exports")
	public BaseController getBController(){
		return this.bController;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void forward(Hashtable messages) {
		this.messages = messages;
	}

	@SuppressWarnings("exports")
	public void setHomeScreenHandler(HomeForm HomeScreenHandler) {
		this.homeScreenHandler = HomeScreenHandler;
	}

}
