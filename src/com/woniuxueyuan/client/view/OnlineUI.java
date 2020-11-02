package com.woniuxueyuan.client.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO
 *
 * @author 95741
 * @version 1.0
 * @date 2020/11/2 11:59
 */
public class OnlineUI extends Stage {
  public static final OnlineUI INSTANCE = new OnlineUI();

  public OnlineUI() {
    super();
  }

  public static OnlineUI getInstance() {
    return INSTANCE;
  }

  private void init() { 
    // 创建一个边框面板
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane, 300, 600);
    this.setTitle("xxx在线");
    this.setResizable(false);
    this.show();
    this.setOnCloseRequest(e -> {

    });
  }
}
