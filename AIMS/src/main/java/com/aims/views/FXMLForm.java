package com.aims.views;

import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class FXMLForm {

	protected AnchorPane content;

	public FXMLForm(String screenPath) throws IOException {
		// Initialize with empty AnchorPane if no FXML file
		this.content = new AnchorPane();
	}

	public FXMLForm() throws IOException {
		this.content = new AnchorPane();
	}

	@SuppressWarnings("exports")
	public AnchorPane getContent() {
		return this.content;
	}

	@SuppressWarnings("exports")
	public void setImage(ImageView imv, String path){
		File file = new File(path);
		Image img = new Image(file.toURI().toString());
		imv.setImage(img);
	}
}
