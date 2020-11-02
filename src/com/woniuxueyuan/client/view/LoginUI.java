package com.woniuxueyuan.client.view;

import com.woniuxueyuan.client.core.ClientSocket;
import com.woniuxueyuan.common.message.LoginRequestMaeeage;
import com.woniuxueyuan.common.util.JavaFXUtil;
import com.woniuxueyuan.common.util.NetUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

/**
 * TODO 主页面是单单例的 Stage 舞台
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/26 16:52
 */
public class LoginUI extends Stage {
  private static final LoginUI INSTANCE = new LoginUI();
  Label userlabel = JavaFXUtil.getTextLabel("用  户  名:", 50, 50, 20);
  TextField loginfield = JavaFXUtil.getTextField(150, 50, 200, 50);
  Label pwdlabel = JavaFXUtil.getTextLabel("密        码:", 50, 120, 20);
  PasswordField pwdfield = JavaFXUtil.getPasswordField(150, 120, 200, 50);
  Button loginbutton = JavaFXUtil.getButton("登录", 130, 200, 90, 40, 20);
  Hyperlink hyperlink = JavaFXUtil.getHyperlink("注册", 300, 250, 20);

  private LoginUI() {
    super();
    init();
  }

  public static LoginUI getINSTANCE() {
    return INSTANCE;
  }

  private void init() {
    // 1.创建面板
    Pane pane = new Pane();
    draw(pane);
    login();
    backMain();
    goRegister();
  }

  private void draw(Pane pane) {
    // 1.创建面板
    pane.getChildren().add(userlabel);
    pane.getChildren().add(loginfield);
    pane.getChildren().add(pwdlabel);
    pane.getChildren().add(pwdfield);
    pane.getChildren().add(loginbutton);
    pane.getChildren().add(hyperlink);
    // 2.创建一个场景
    Scene scene = new Scene(pane, 400, 300);
    // 在舞台中设置场景
    this.setScene(scene);
    this.setResizable(false);
    this.setTitle("五子棋游戏");
  }

  private void login() {
    loginbutton.setOnMouseClicked(
        event -> {
          userLogin();
        });
  }

  private void userLogin() {
    String username = loginfield.getText();
    String pwd = pwdfield.getText();
    // 1. 非空验证
    if ("".equals(username)) {
      JavaFXUtil.alert("warning", "亲爱的玩家", "用户名不能为空");
      return;
    }
    // 2.判断密码两次是否相同
    if ("".equals(pwd)) {
      JavaFXUtil.alert("警告", "亲爱的玩家", "密码不能为空");
      return;
    }
    Socket clientSocket = ClientSocket.getInstance();
    LoginRequestMaeeage loginRequestMaeeage = new LoginRequestMaeeage(username, pwd);
    try {
      NetUtil.sendMessage(clientSocket, loginRequestMaeeage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void backMain() {
    this.setOnCloseRequest(
        event -> {
          Alert.AlertType alertAlertType;
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("退出");
          alert.setHeaderText("你是否要退出?");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            MainUI.getINSTANCE().show();
            this.hide();

          } else {
            event.consume();
          }
        });
  }

  private void goRegister() {
    hyperlink.setOnMouseClicked(
        event -> {
          this.hide();
          RegisterUI.getINSTANCE().show();
        });
  }
}
