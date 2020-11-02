package com.woniuxueyuan.client.view;

import com.woniuxueyuan.common.entity.Chess;
import com.woniuxueyuan.common.util.JavaFXUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * TODO 单机版五子棋游戏界面
 *
 * @author 95741
 * @version 1.0
 * @date 2020/10/17 15:34
 */
public class GameUI extends Stage { // 应用程序窗口
  private static final GameUI INSTANCE = new GameUI();
  // 1. 创建一个面板
  Pane pane;
  String name;
  Button btn = JavaFXUtil.getButton("悔棋", 50, 690, 90, 30, 16);
  Button btn1 = JavaFXUtil.getButton("重新开始", 170, 690, 90, 30, 16);
  Button btn2 = JavaFXUtil.getButton("结束游戏", 290, 690, 90, 30, 16);
  Button btn3 = JavaFXUtil.getButton("打谱", 420, 690, 90, 30, 16);
  Button btn4 = JavaFXUtil.getButton("存档", 530, 690, 90, 30, 16);
  Button btn5 = JavaFXUtil.getButton("->", 650, 350, 50, 30, 16);
  Button btn6 = JavaFXUtil.getButton("<-", 0, 350, 50, 30, 16);
  int btnNum = 0;
  Label label = new Label();
  Text text = new Text(300, 20, "显示棋子位置");
  private int width = 700; // 宽度
  private int height = 740; // 高度
  private int ceil = 40; // 格子宽高
  private int size = 15; // 五子棋的规格
  private int margin = 50;
  private int num = 33;
  private boolean isBlack = true;
  private int tempX;
  private int tempY;
  private LinkedList<Chess> chessLinkedList = new LinkedList<>();
  private Stack<Chess> chessStack = new Stack<>();
  private boolean isClick = true;
  private int winStep = 0;
  private boolean flag = true;
  private Media _media;
  private MediaPlayer _mediaPlayer;
  private int index = 0; // 打谱临时坐标

  private GameUI() {
    super();
    init();
  }

  public static GameUI getInstance() {
    return INSTANCE;
  }

  public void init() {
    {
      backMain();
      pane = new Pane();
      // 2.创建一个场景
      Scene scene = new Scene(pane, width, height);
      pane.setBackground(new Background(new BackgroundFill(Color.rgb(243, 182, 116), null, null)));
      // 给舞台设置一个场景
      pane.getChildren().add(text);
      this.setScene(scene);
      label = JavaFXUtil.getTextLabel("玩家" + name, 0, 0, 100);
      label.setFont(new Font(25));
      pane.getChildren().add(label);
      // 关闭自适应 禁止窗口拉伸
      this.setResizable(false);
      // 设置标题
      this.setTitle("五子棋单机版");
      // 显示出主舞台
      this.show();
      // 1.1 绘制棋盘
      drawChessBoard();
      // 画按钮
      // drawLabel();
      // 1.2 给面板添加点击事件
      // 悔棋
      back();
      // 重新开始
      restart();
      // 退出
      end();
      // 打谱
      chessbook(this);
      // 存档
      saveChess(this);
      // 下棋
      drawChess();
    }
  }

  private void backMain() {
    this.setOnCloseRequest(
        event -> {
          AlertType alertAlertType;
          Alert alert = new Alert(AlertType.CONFIRMATION);
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

  private void drawChessBoard() {
    // 1.1 面板上画线
    for (int i = margin; i <= margin + size * ceil; i += ceil) {
      Line line = new Line(margin, i, margin + size * ceil, i);
      pane.getChildren().add(line);
      //
      Line line1 = new Line(i, margin, i, margin + size * ceil);
      pane.getChildren().add(line1);
    }
    btn.setDisable(true);
  }

  /** 下棋 */
  private void drawChess() {
    Random random = new Random();
    pane.setOnMouseClicked(
        e -> {
          // 获取鼠标在面板的X Y坐标
          double sceceY = e.getSceneY();
          double sceceX = e.getSceneX();
          int minScore = margin - ceil / 2;
          int maxScore = margin + ceil * size + ceil / 2;
          if (sceceX <= minScore || sceceX > maxScore || sceceY <= minScore || sceceY > maxScore) {
          } else if (isClick) {
            deleteButton("->", "<-");
            sceceX = Math.round((sceceX - margin) / ceil) * ceil + margin;
            sceceY = Math.round((sceceY - margin) / ceil) * ceil + margin;
            tempX = (int) (sceceX - margin) / ceil;
            tempY = (int) (sceceY - margin) / ceil;
            Color color = isBlack ? Color.BLACK : Color.WHITE;
            Chess chess = new Chess(tempX, tempY, color);
            if (!chessLinkedList.contains(chess)) {
              draw(chess);
              boolean a = isWin(tempX, tempY, color);
              if (a) {
                // JavaFXUtil.alert("游戏结束", "亲爱的玩家", "恭喜" + (isBlack ? "黑棋" : "白棋") + "获胜");
                isClick = false;
                drawWinChess();
              }

              isBlack = !isBlack;
            }
            if (chessStack.isEmpty()) {
              btn.setDisable(true);
            } else {
              btn.setDisable(false);
            }
          }
        });

    pane.setOnMouseMoved(
        e -> {
          text.setFont(Font.font(25));
          // 获取鼠标在面板的X Y坐标
          int red = random.nextInt(255);
          int green = random.nextInt(255);
          int blue = random.nextInt(255);
          double sceceY = e.getSceneY();
          double sceceX = e.getSceneX();
          int minScore = margin - ceil / 2;
          int maxScore = margin + ceil * size + ceil / 2;
          if (sceceX <= minScore || sceceX > maxScore || sceceY <= minScore || sceceY > maxScore) {
            text.setText("出界了出界了");
            text.setFill(Color.rgb(red, green, blue));
            label.setTextFill(Color.rgb(red, green, blue));
          } else {
            sceceX = Math.round((sceceX - margin) / ceil) * ceil + margin;
            sceceY = Math.round((sceceY - margin) / ceil) * ceil + margin;
            int tempx = (int) (sceceX - margin) / ceil + 1;
            int tempy = (int) (sceceY - margin) / ceil + 1;
            text.setText("当前位置:" + "(" + tempx + "," + tempy + ")");
            text.setFill(Color.rgb(red, green, blue));
            label.setTextFill(Color.rgb(red, green, blue));
          }
        });
  }

  /**
   * 判断是否有棋子
   *
   * @param x
   * @param y
   * @param color
   * @return
   */
  private boolean isPresent(int x, int y, Color color) {
    for (Chess chess : chessLinkedList) {
      if (x == chess.getX() && y == chess.getY() && chess.getColor() == color) {
        return true;
      }
    }
    return false;
  }

  /**
   * 五子棋判断输赢的核心算法
   *
   * @param x
   * @param y
   * @param color
   * @return
   */
  private boolean isWin(int x, int y, Color color) {
    // 情形一水平获胜
    int leftnum = 0;
    int rightnum = 0;
    int topnum = 0;
    int bottonnum = 0;
    int rightabovenum = 0;
    int leftbelownum = 0;
    int leftabovenum = 0;
    int rightbelownum = 0;
    int xtemp = x;
    int ytemp = y;

    while (isPresent(xtemp - 1, y, color)) {
      xtemp--;
      leftnum++;
    }
    if (leftnum >= 4) return true;
    xtemp = x;
    while (isPresent(xtemp + 1, y, color)) {
      xtemp++;
      rightnum++;
    }
    if (rightnum >= 4 || rightnum + leftnum >= 4) return true;

    while (isPresent(x, ytemp - 1, color)) {
      ytemp--;
      bottonnum++;
    }
    if (topnum >= 4) return true;

    ytemp = y;
    while (isPresent(x, ytemp + 1, color)) {
      ytemp++;
      topnum++;
    }
    if (bottonnum >= 4 || bottonnum + topnum >= 4) return true;

    xtemp = x;
    ytemp = y;
    while (isPresent(xtemp + 1, ytemp + 1, color)) {
      xtemp++;
      ytemp++;
      rightabovenum++;
    }
    if (rightabovenum >= 4) return true;

    xtemp = x;
    ytemp = y;
    while (isPresent(xtemp - 1, ytemp - 1, color)) {
      xtemp--;
      ytemp--;
      leftbelownum++;
    }
    if (leftbelownum >= 4 || leftbelownum + rightabovenum >= 4) return true;

    xtemp = x;
    ytemp = y;
    while (isPresent(xtemp + 1, ytemp - 1, color)) {
      xtemp++;
      ytemp--;
      rightbelownum++;
    }
    if (rightbelownum >= 4) return true;

    xtemp = x;
    ytemp = y;
    while (isPresent(xtemp - 1, ytemp + 1, color)) {
      xtemp--;
      ytemp++;
      leftabovenum++;
    }
    if (leftabovenum >= 4 || leftabovenum + rightbelownum >= 4) return true;
    return false;
  }

  /* private void drawLabel() {
    label.setLayoutX(250);
    label.setLayoutY(0);
    label.setPrefSize(400, 50);
    label.setFont(new Font(20));
    label.setText("(" + 0 + "," + 0 + ")");
    pane.getChildren().add(label);
  }*/

  private void back() {

    pane.getChildren().add(btn);
    num++;
    btn.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            deleteButton("<-", "->");
            if (!chessStack.isEmpty()) {
              if (winStep == 0) {
                pane.getChildren().remove(pane.getChildren().size() - 1);
              } else {
                pane.getChildren()
                    .remove(pane.getChildren().size() - winStep - 1, pane.getChildren().size());
                winStep = 0;
              }
              chessLinkedList.remove(chessStack.pop());
              isBlack = !isBlack;
              isClick = true;
            }
            if (chessStack.isEmpty()) {
              btn.setDisable(true);
            } else {
              btn.setDisable(false);
            }
          }
        });
  }

  private void restart() {
    pane.getChildren().add(btn1);
    num++;
    btn1.setOnAction(
        event -> {
          clean();
          winStep = 0;
          btn.setDisable(true);
        });
  }

  private void end() {
    pane.getChildren().add(btn2);
    num++;
    btn2.setOnAction(
        event -> {
          Alert alert = new Alert(AlertType.CONFIRMATION);
          alert.setTitle("退出游戏");
          alert.setHeaderText("你是否要退出?");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {

            System.exit(0);

          } else {
            event.consume();
          }
        });
  }

  private void chessbook(Stage stage) {
    pane.getChildren().add(btn3);
    num++;
    btn3.setOnMouseClicked(
        event -> {
          FileChooser chooser = new FileChooser();
          File file = chooser.showOpenDialog(stage);
          // 验证文件的格式
          if (file == null || !file.getName().endsWith(".csv")) {
            JavaFXUtil.alert("打谱提示", "亲爱的的玩家", "选择必须是csv格式的文件");
            return;
          }
          // 开始读取棋谱 使用io流来读取文件数据
          ArrayList<Chess> list = new ArrayList<>();
          index = 0; // 每换一个棋谱 索引为零 重新开始打谱
          try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file)); ) {
            bufferedReader.readLine();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
              String[] strings = line.split(",");
              Color color = Color.BLACK.toString().equals(strings[2]) ? Color.BLACK : Color.WHITE;
              list.add(
                  new Chess(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), color));
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
          clean();
          //
          AlertType alertAlertType;
          Alert alert = new Alert(AlertType.CONFIRMATION);
          alert.setTitle("打谱提示");
          alert.setTitle("打谱提示");
          alert.setContentText("您确定要单步打谱吗");
          Optional<ButtonType> optionalButtonType = alert.showAndWait();
          if (optionalButtonType.get() == ButtonType.OK) {
            btn.setDisable(true);
            pane.getChildren().add(btn6);
            pane.getChildren().add(btn5);
            btn5.setOnMouseClicked(
                e2 -> {
                  boolean flag = false;
                  if (index <= list.size() - 1 && !flag) {
                    Chess chess = list.get(index++);
                    draw(chess);
                    isBlack = list.get(index - 1).getColor() == Color.WHITE;
                  } else {
                    JavaFXUtil.alert("打谱提示", "亲爱的的玩家", "打谱结束");
                    flag = !flag;
                  }
                  if (flag) {
                    deleteButton("->", "<-");
                  }
                });
            btn6.setOnAction(
                e3 -> {
                  if (index > 0) {
                    chessLinkedList.removeLast();
                    chessStack.pop();
                    pane.getChildren().remove(pane.getChildren().size() - 1);
                    isBlack = list.get(index).getColor() == Color.WHITE;
                  } else {
                    JavaFXUtil.alert("打谱提示", "亲爱的的玩家", "没棋子");
                  }
                });

          } else {
            btn.setDisable(false);
            for (int i = 0; i < list.size(); i++) {
              draw(list.get(i));
            }
            isBlack = list.get(list.size() - 1).getColor() == Color.WHITE;
          }
        });
  }

  private void saveChess(Stage stage) {
    pane.getChildren().add(btn4);
    num++;
    btn4.setOnMouseClicked(
        e -> {
          deleteButton("->", "<-");
          FileChooser chooser = new FileChooser();
          File file = chooser.showSaveDialog(stage);
          // 空棋谱验证
          if (chessLinkedList.isEmpty()) {
            JavaFXUtil.alert("保存提示", "亲爱的的玩家", "不能保存空棋谱");
            return;
          }
          // 文件格式验证。必须是csv格式结尾
          if (file == null || !file.getName().endsWith(".csv")) {
            JavaFXUtil.alert("保存提示", "亲爱的的玩家", "存档必须是csv格式的文件");
            return;
          }
          //
          try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file)); ) {
            bufferedWriter.write("X坐标,Y坐标,颜色");
            bufferedWriter.newLine();
            for (Chess chess : chessLinkedList) {
              bufferedWriter.write(chess.getX() + "," + chess.getY() + "," + chess.getColor());
              bufferedWriter.newLine();
            }
            JavaFXUtil.alert("保存提示", "亲爱的的玩家", "保存棋谱成功");
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }
          // 将集合中的棋子按照落子的顺序依次写入到csv文件中
        });
  }

  private void drawWinChess() {
    Random random = new Random();
    int leftnum = 0;
    int rightnum = 0;
    int topnum = 0;
    int bottonnum = 0;
    int rightabovenum = 0;
    int leftbelownum = 0;
    int leftabovenum = 0;
    int rightbelownum = 0;
    int xtemp = tempX;
    int ytemp = tempY;
    Color color = isBlack ? Color.BLACK : Color.WHITE;
    Chess chess = new Chess(tempX, tempY, color);
    // 先判断横着的
    while (isPresent(xtemp - 1, ytemp, color)) {
      xtemp--;
      leftnum++;
    }
    xtemp = tempX;
    while (isPresent(xtemp + 1, ytemp, color)) {
      xtemp++;
      rightnum++;
    }
    if (rightnum + leftnum >= 4) {
      int red = random.nextInt(255);
      int green = random.nextInt(255);
      int blue = random.nextInt(255);
      while (xtemp >= tempX - leftnum) {
        if (ytemp == tempY && xtemp == tempX) {
        } else {
          Circle circle =
              new Circle(
                  xtemp * ceil + margin, ytemp * ceil + margin, 15, Color.rgb(red, green, blue));
          pane.getChildren().add(circle);
          winStep++;
        }
        xtemp--;
      }
    }
    // 竖着的
    xtemp = tempX;
    ytemp = tempY;
    while (isPresent(xtemp, ytemp - 1, color)) {
      ytemp--;
      bottonnum++;
    }
    ytemp = tempY;
    while (isPresent(xtemp, ytemp + 1, color)) {
      ytemp++;
      topnum++;
    }
    if (bottonnum >= 4 || bottonnum + topnum >= 4) {
      int red = random.nextInt(255);
      int green = random.nextInt(255);
      int blue = random.nextInt(255);
      while (ytemp >= tempY - bottonnum) {
        if (ytemp == tempY && xtemp == tempX) {

        } else {
          Circle circle =
              new Circle(
                  xtemp * ceil + margin, ytemp * ceil + margin, 15, Color.rgb(red, green, blue));
          pane.getChildren().add(circle);
          winStep++;
        }
        ytemp--;
      }
    }
    // 左斜着
    xtemp = tempX;
    ytemp = tempY;
    while (isPresent(xtemp + 1, ytemp - 1, color)) {
      ytemp--;
      xtemp++;
      rightbelownum++;
    }
    xtemp = tempX;
    ytemp = tempY;
    while (isPresent(xtemp - 1, ytemp + 1, color)) {
      ytemp++;
      xtemp--;
      leftabovenum++;
    }
    if (leftabovenum + rightbelownum >= 4) {
      int red = random.nextInt(255);
      int green = random.nextInt(255);
      int blue = random.nextInt(255);
      while (ytemp >= tempY - rightbelownum) {
        if (ytemp == tempY && xtemp == tempX) {
        } else {
          Circle circle =
              new Circle(
                  xtemp * ceil + margin, ytemp * ceil + margin, 15, Color.rgb(red, green, blue));
          pane.getChildren().add(circle);
          winStep++;
        }
        ytemp--;
        xtemp++;
      }
    }
    // 右斜着
    xtemp = tempX;
    ytemp = tempY;
    while (isPresent(xtemp + 1, ytemp + 1, color)) {
      ytemp++;
      xtemp++;
      rightabovenum++;
    }
    xtemp = tempX;
    ytemp = tempY;
    while (isPresent(xtemp - 1, ytemp - 1, color)) {
      ytemp--;
      xtemp--;
      leftbelownum++;
    }
    if (leftbelownum + rightabovenum >= 4) {
      int red = random.nextInt(255);
      int green = random.nextInt(255);
      int blue = random.nextInt(255);
      while (ytemp <= tempY + rightabovenum) {
        if (ytemp == tempY && xtemp == tempX) {
        } else {
          Circle circle =
              new Circle(
                  xtemp * ceil + margin, ytemp * ceil + margin, 15, Color.rgb(red, green, blue));
          pane.getChildren().add(circle);
          winStep++;
        }
        ytemp++;
        xtemp++;
      }
    }
  }

  private void draw(Chess chess) {
    if (chess == null) return;
    int X = chess.getX() * ceil + margin;
    int Y = chess.getY() * ceil + margin;
    Color color = chess.getColor();
    pane.getChildren().add(new Circle(X, Y, 15, color));
    chessStack.push(chess);
    chessLinkedList.add(chess);
    tempX = chess.getX();
    tempY = chess.getY();
    btn.setDisable(false);
    if (isWin(chess.getX(), chess.getY(), color)) {
      JavaFXUtil.alert(
          "游戏结束", "亲爱的玩家", "恭喜" + (chess.getColor() == Color.BLACK ? "黑棋" : "白棋") + "获胜");
      isClick = false;
      winStep = 0;
      btn.setDisable(false);
      isBlack = chess.getColor() == Color.BLACK;
      drawWinChess();
    }
  }

  private void clean() {
    {
      deleteButton("->", "<-");
      ObservableList<Node> children = pane.getChildren();
      for (int i = children.size() - 1; i >= 0; i--) {
        if (children.get(i) instanceof Circle) {
          pane.getChildren().remove(i);
        } else {
          break;
        }
      }
      // 也是删除棋子
      // pane.getChildren().remove(num, pane.getChildren().size());
      // lamda 简化形式
      // pane.getChildren().removeIf(t -> t instanceof Circle);
      isBlack = true;
      isClick = true;
      chessStack.clear();
      chessLinkedList.clear();
      btn.setDisable(true);
    }
  }

  /**
   * 删除面板上的任意子元素
   *
   * @param names
   */
  public void deleteButton(String... names) {
    if (names == null || names.length == 0) return;
    ObservableList<Node> list = pane.getChildren();
    for (String name : names) { // 获取到需要删除的按钮名称
      for (int i = list.size() - 1; i >= 0; i--) { // 集合的删除：Bug
        Node node = list.get(i);
        if (node instanceof Button) {
          Button btn = (Button) node;
          if (btn.getText().equals(name)) { // 找到了与删除名称相同的按钮
            list.remove(i);
          }
        }
      }
    }
  }
}
