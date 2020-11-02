package com.woniuxueyuan.client.view;

import com.woniuxueyuan.client.core.ClientReceiver;
import com.woniuxueyuan.common.util.JavaFXUtil;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * TODO 主页面是单单例的 Stage 舞台
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/26 16:52
 */
public class MainUI extends Stage {
  private static final MainUI INSTANCE = new MainUI();

  private MainUI() {
    super();
    init();
  }

  public static MainUI getINSTANCE() {
    return INSTANCE;
  }

  private void init() {
    // 1.创建面板
    Pane pane = new Pane();
    Image image1 = null;
    try {
      image1 =
          new Image(
              new FileInputStream(
                  "C:\\softwareInstall\\intellDEA2020\\programCode\\chess_client\\src\\com\\woniuxueyuan\\client\\view\\image\\bg10.gif"));
      BackgroundImage image =
          new BackgroundImage(
              image1, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null);
      pane.setBackground(new Background(image));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    // 1.1 单机版游戏
    Button btnSingle = JavaFXUtil.getButton("单机版", 50, 70, 140, 40, 18);
    pane.getChildren().add(btnSingle);
    btnSingle.setOnMouseClicked(
        e -> {
          GameUI.getInstance().show();
         // MainUI.getINSTANCE().hide();
        });
    // 1.2 网络版游戏
    Button btnNet = JavaFXUtil.getButton("网络版", 210, 70, 140, 40, 18);
    pane.getChildren().add(btnNet);
    btnNet.setOnMouseClicked(
        event -> {
          LoginUI.getINSTANCE().show();
          this.hide();
          ClientReceiver.listen();
        });
    // 1.3 人机大战
    Button btnAI = JavaFXUtil.getButton("人机对战", 50, 170, 140, 40, 18);
    pane.getChildren().add(btnAI);
    // 1.4 连连看
    Button btnOther = JavaFXUtil.getButton("连连看", 210, 170, 140, 40, 18);
    pane.getChildren().add(btnOther);
    // 2.创建一个场景
    Scene scene = new Scene(pane, 400, 300);
    // 在舞台中设置场景
    this.setScene(scene);
    this.setResizable(false);
    this.setTitle("五子棋游戏");

    this.setOnCloseRequest(e -> {

    });
  }
}
