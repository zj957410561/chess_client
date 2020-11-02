package com.woniuxueyuan.client.view.Freined;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TODO
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/31 14:53
 */
public class friendUI extends Stage {
  private static friendUI INSTANCE = new friendUI();

  private friendUI() {
    super();
    start();
  }

  public static friendUI getINSTANCE() {
    return INSTANCE;
  }

  public static void main(String[] args) {
    friendUI.getINSTANCE();
  }

  public void start() {
    Parent root;
    VBox vBox = new VBox();
    Scene scene = new Scene(vBox);
    this.setScene(scene);
    vBox.setMinHeight(100);
    vBox.setMinWidth(200);
    for (int i = 0; i < 10; i++) {
      Button button = new Button(i + "");
      button.setMinWidth(vBox.getWidth());
      vBox.getChildren().add(button);
    }
  }
}
