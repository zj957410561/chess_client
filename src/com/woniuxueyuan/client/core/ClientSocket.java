package com.woniuxueyuan.client.core;

import com.woniuxueyuan.client.view.LoginUI;
import com.woniuxueyuan.client.view.MainUI;
import com.woniuxueyuan.common.util.JavaFXUtil;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

/**
 * TODO 单例socket
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/30 15:56
 */
public class ClientSocket {
  private static Socket instance;

  private ClientSocket() {
    super();
  }

  public static Socket getInstance() {
    if (instance == null) {
      try {
        instance = new Socket("localhost", 9999);
      } catch (IOException e) {
        Platform.runLater(
            () -> {
              JavaFXUtil.alert("提示", "服务器", "停服升级");
              LoginUI.getINSTANCE().hide();
              MainUI.getINSTANCE().show();
            });
      }
    }
    return instance;
  }
}
