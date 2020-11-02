package com.woniuxueyuan.client.core;

import com.woniuxueyuan.client.view.GameNetUI;
import com.woniuxueyuan.client.view.LoginUI;
import com.woniuxueyuan.client.view.RegisterUI;
import com.woniuxueyuan.common.message.LoginReponseMessage;
import com.woniuxueyuan.common.message.RegisterReponseMessage;
import com.woniuxueyuan.common.util.JavaFXUtil;
import com.woniuxueyuan.common.util.NetUtil;
import javafx.application.Platform;

import java.net.Socket;

/**
 * TODO 客户端用来接受服务器响应的类
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/30 16:05
 */
public class ClientReceiver extends Thread {

  private static ClientReceiver instance;

  private ClientReceiver() {
    super();
  }

  /** 监听服务器响应的线程 */
  public static void listen() {
    if (instance == null) {
      instance = new ClientReceiver();
      instance.start();
    }
  }

  @Override
  public void run() {
    // 一直等待处理服务器发送过来的响应
    for (; ; ) {
      // 获取当前客户端的socket（自己的程序)
      Socket socket = ClientSocket.getInstance();
      if (socket == null) { // 客户端连接服务器失败了
        instance = null;
        return;
      }
      try {
        Object o = NetUtil.receiveMessage(socket);
        processServerResponse(socket, o);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 根据服务器不同的响应来作出客户端不同的操作
   *
   * @param client
   * @param obj
   */
  private void processServerResponse(Socket client, Object obj) {
    Platform.runLater(
        () -> { // 在这个代码块中才能在普通类中运行JavaFX控件
          if (obj instanceof RegisterReponseMessage) { // 1. 注册响应
            RegisterReponseMessage message = (RegisterReponseMessage) obj;
            int code = message.getCode();
            if (code == 0) {
              JavaFXUtil.alert("注册结果", "尊敬的玩家", "您注册的用户名已经存在");
            } else if (code == 1) { // 注册成功后直接跳转到登录页面
              JavaFXUtil.alert("注册结果", "尊敬的玩家", "注册成功");
              RegisterUI.getINSTANCE().hide();
              LoginUI.getINSTANCE().show();
            } else {
              JavaFXUtil.alert("注册结果", "尊敬的玩家", "注册失败");
            }
          } else if (obj instanceof LoginReponseMessage) {
            LoginReponseMessage login = (LoginReponseMessage) obj;
            int code = login.getCode();
            if (code == 0) {
              JavaFXUtil.alert("warning", "尊敬的玩家", "您登录的用户名不存在");
            } else if (code == 1) {
              JavaFXUtil.alert("success", "尊敬的玩家", "登录成功");
              GameNetUI.getInstance().show();
              LoginUI.getINSTANCE().hide();
            } else {
              JavaFXUtil.alert("warning", "尊敬的玩家", "密码错误");
            }
          }
        });
  }
}
