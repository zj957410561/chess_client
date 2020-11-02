package com.woniuxueyuan.client.view;

import com.woniuxueyuan.client.core.ClientSocket;
import com.woniuxueyuan.common.message.RegisterReponseMessage;
import com.woniuxueyuan.common.message.RegisterRequestMaeeage;
import com.woniuxueyuan.common.util.JavaFXUtil;
import com.woniuxueyuan.common.util.NetUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
public class RegisterUI extends Stage {
  private static final RegisterUI INSTANCE = new RegisterUI();
  TextField loginfield = JavaFXUtil.getTextField(150, 50, 200, 50);
  Label pwdlabel = JavaFXUtil.getTextLabel("密          码:", 25, 120, 20);
  PasswordField pwdfield = JavaFXUtil.getPasswordField(150, 120, 200, 50);
  PasswordField pwdfields = JavaFXUtil.getPasswordField(150, 190, 200, 50);
  Label pwdlabels = JavaFXUtil.getTextLabel("再次输入密码:", 25, 190, 20);
  Label userlabel = JavaFXUtil.getTextLabel("用    户    名:", 25, 50, 20);
  Button registerbutton = JavaFXUtil.getButton("注册", 100, 260, 90, 40, 20);
  Button backbutton = JavaFXUtil.getButton("返回", 210, 260, 90, 40, 20);

  private RegisterUI() {
    super();
    init();
  }

  public static RegisterUI getINSTANCE() {
    return INSTANCE;
  }

  private void init() {
    // 1.创建面板
    Pane pane = new Pane();
    // 1.1
    draw(pane);
    // 注册
    register();
    // 返回
    back();

    backMain();
    // 用户名失去焦点事件
    TextFieldAction(pane);
    // 密码框失去焦点事件
    PwdFieldAction();
  }

  /**
   * 绘制页面
   *
   * @param pane
   */
  private void draw(Pane pane) {
    pane.getChildren().add(userlabel);
    pane.getChildren().add(loginfield);
    pane.getChildren().add(pwdlabel);
    pane.getChildren().add(pwdfield);
    pane.getChildren().add(pwdlabels);
    pane.getChildren().add(pwdfields);
    pane.getChildren().add(registerbutton);
    pane.getChildren().add(backbutton);
    ProgressBar progressBar = new ProgressBar();

    // 2.创建一个场景
    Scene scene = new Scene(pane, 400, 300);
    scene.getStylesheets().add(RegisterUI.class.getResource("css/Register.css").toExternalForm());
    // 在舞台中设置场景
    this.setScene(scene);
    this.setResizable(false);
    this.setTitle("五子棋游戏");
  }

  /** 注册 */
  public void register() {
    registerbutton.setOnMouseClicked(
        event -> {
          doRegister();
        });
  }

  private void doRegister() {
    String username = loginfield.getText();
    String pwd = pwdfield.getText();
    String repwd = pwdfields.getText();
    // 1. 非空验证
    if ("".equals(username)) {
      JavaFXUtil.alert("warning", "亲爱的玩家", "用户名不能为空");
      return;
    }
    // 2.判断密码两次是否相同
    if ("".equals(pwd) || "".equals(repwd) || !pwd.equals(repwd)) {
      JavaFXUtil.alert("警告", "亲爱的玩家", "两次密码不一致");
      return;
    }
    if (!check()) {
      JavaFXUtil.alert("警告", "亲爱的玩家", "格式不正确");
      return;
    }
    // 3. 客户端向服务器发送注册的请求
    Socket socket = ClientSocket.getInstance();
    RegisterRequestMaeeage maeeage = new RegisterRequestMaeeage(username, pwd);
    try {
      NetUtil.sendMessage(socket, maeeage);
    } catch (Exception e) {
      e.printStackTrace();
    }
    /*    try {
      Object o = NetUtil.receiveMessage(socket);
      if (o instanceof RegisterReponseMessage) {
        RegisterReponseMessage rrm = (RegisterReponseMessage) o;
        System.out.println(rrm.getCode());
        if (rrm.getCode() == 0) {
          JavaFXUtil.alert("warning", "亲爱的玩家", "用户名重复");
        } else if (rrm.getCode() == 1) {
          JavaFXUtil.alert("success", "亲爱的玩家", "注册成功");
          LoginUI.getINSTANCE().show();
          this.hide();
        } else {
          JavaFXUtil.alert("success", "亲爱的玩家", "注册失败");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }
  /** 返回 */
  private void back() {
    backbutton.setOnMouseClicked(
        event -> {
          LoginUI.getINSTANCE().show();
          this.hide();
        });
  }
  /**
   * 正则校验
   *
   * @return
   */
  private boolean check() {
    String name = loginfield.getText();
    String password = pwdfield.getText();
    String password2 = pwdfields.getText();
    String namePatern = "^[a-zA-Z]{4,16}$";
    String pwdPatern = "^[A-Za-z0-9]{6,8}$";
    if (name != null
        && password != null
        && password.equals(password2)
        && name.matches(namePatern)
        && password.matches(pwdPatern)) {
      return true;
    } else {
      return false;
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
            LoginUI.getINSTANCE().show();
            this.hide();

          } else {
            event.consume();
          }
        });
  }

  private void setBackGround() {}

  /** 文本框失去焦点事件 */
  private void TextFieldAction(Pane pane) {
    loginfield
        .focusedProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                String namePatern = "^[a-zA-Z]{4,16}$";
                ObservableList<Node> list = pane.getChildren();
                String name = loginfield.getText();
                Text lbl = getText(pane, 1);
                if (newValue) {
                  lbl.setText("");
                } else {
                  if (name.matches(namePatern)) {
                    System.out.println("12");
                    lbl.setText("");
                  } else {

                  }
                }
              }
            });
  }

  private Text getText(Pane pane, double i) {
    Text lbl = new Text();
    lbl.setText("格式错误");
    lbl.setLayoutX(350);
    lbl.setLayoutY(50);
    Color color = Color.rgb(250, 1, 1, i);
    lbl.setFill(color);
    pane.getChildren().add(lbl);
    return lbl;
  }

  /** 密码框失去焦点事件 */
  private void PwdFieldAction() {
    pwdfield
        .focusedProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                if (newValue) {
                } else {
                  String pwdPatern = "^[A-Za-z0-9]{6,8}$";
                  String password = pwdfield.getText();
                  if (password.matches(pwdPatern)) {
                  } else {
                    // JavaFXUtil.alert("格式错误", "格式错误", "密码格式错误");
                  }
                }
              }
            });
  }
}
