import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Game extends JPanel {
    enum State { // 游戏运行状态
        start, won, running, over
    }

    final Color[] colorTable = {
            new Color(0x7878FF), new Color(0xfafafa), new Color(0xD2F0FF), // 2048 0 2
            new Color(0xA0E6FF), new Color(0x78D7FF), new Color(0x29b6f6), // 4 8 16
            new Color(0x03A9DC), new Color(0x0387C8), new Color(0x1976d2), // 32 64 128
            new Color(0x2D55A5), new Color(0x2D4191), new Color(0x5064C8) // 256 512 1024
    };
    final static int target = 2048;
    static int highest; // 界面内最高值，当最高值等于target即2048时，游戏胜利
    static int score;
    private final Color gridColor = new Color(0x4f8ca8);
    private final Color emptyColor = new Color(0xb3e5fc);
    private final Color startColor = new Color(0xd6f2ff);
    private final Random random = new Random();
    private Tile[][] tiles; //存放每个方块里的数字，二维数组表示行列。
    private final int side = 4;
    private State gamestate = State.start;
    private boolean checkingAvailableMoveState;
    SQL sql = new SQL();

    public Game() {
        sql.connect();
        setPreferredSize(new Dimension(900, 700));
        setBackground(new Color(0xe1f5fe));
        setFont(new Font("SansSerif", Font.BOLD, 48));
        setFocusable(true); // 使组件可聚焦以监听键盘事件
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { // 单击鼠标开始游戏
                initGame();
                repaint();
            }
        });
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {// 监听键盘输入
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRight();
                        break;
                }
                repaint();
            }
        });
    }

    public void paintComponent(Graphics gg) { // 绘制组件方法，被repaint调用
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        drawGrid(g);
    }

    void initGame() {
        if (gamestate != State.running) {
            score = 0;
            highest = 0;
            gamestate = State.running;
            tiles = new Tile[side][side];
            addRandomTile(); // 在格子中随机添加一个2
            addRandomTile(); // 在格子中随机添加第二个2
        }
    }

    void drawGrid(Graphics2D g) { // 绘制背景等组件方法
        g.setColor(gridColor);
        g.fillRoundRect(200, 100, 499, 499, 15, 15);
        if (gamestate == State.running) { // 游戏运行中则绘制背景及方块
            for (int r = 0; r < side; r++) {
                for (int c = 0; c < side; c++) {
                    if (tiles[r][c] == null) {
                        g.setColor(emptyColor);
                        g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
                    } else {
                        drawTile(g, r, c);
                    }
                }
            }
            g.drawString("Score: " + score, 220, 80);
        } else {
            g.setColor(startColor);
            g.fillRoundRect(215, 115, 469, 469, 7, 7);
            g.setColor(gridColor.darker());
            g.setFont(new Font("SansSerif", Font.BOLD, 128));
            g.drawString("2048", 310, 270);
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            if (gamestate == State.won) {
                g.drawString("你成功啦!", 410, 350);
                g.setFont(new Font("微软雅黑", Font.BOLD, 72));
                g.drawString("最终得分: " + score, 180, 80);
            } else if (gamestate == State.over) {
                g.drawString("已经结束嘞", 400, 350);
                g.setFont(new Font("微软雅黑", Font.BOLD, 48));
                g.drawString("最终得分: " + score, 300, 420);
            }
            g.setColor(gridColor);
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            g.drawString("单击鼠标开始游戏", 370, 470);
            g.drawString("", 380, 530);
        }
    }

    void drawTile(Graphics2D g, int r, int c) {
        int value = tiles[r][c].getValue();
        g.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 1]); //设置背景颜色
        g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
        String s = String.valueOf(value);
        g.setColor(value < 128 ? colorTable[0] : colorTable[1]); // 设置数字颜色
        g.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 1].darker().darker());
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent(); // 获取字体的基线到字体的最高处的距离
        int dec = fm.getDescent(); // 获取字体的基线到字体的最低处的距离
        int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2; // 计算文字的x坐标
        int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2); // 计算文字的y坐标
        g.drawString(s, x, y); // 绘制数字
    }

    private void addRandomTile() { // 在随机位置添加新数字的方法
        int pos = random.nextInt(side * side); // 随机生成一个0到side*side-1的整数
        int row, col;
        do {
            pos = (pos + 1) % (side * side); // pos的值为0-15，转换为行列坐标
            row = pos / side; // 0-3
            col = pos % side; // 0-3
        } while (tiles[row][col] != null); // 如果这个位置已经有值，则重新随机
        int val = random.nextInt(10) == 0 ? 4 : 2; // 生成4的概率是1/10
        tiles[row][col] = new Tile(val);
    }

    private boolean move(int countDownFrom, int yIncr, int xIncr) { // 移动方法，yIncr为y方向的增量，xIncr为x方向的增量，countDownFrom用于确保在3个或以上同种方块成一行时，最外侧的两个方块优先进行合并
        boolean canMove = false; // 用于判断是否还有空格可移动
        for (int i = 0; i < side * side; i++) {
            int j = Math.abs(countDownFrom - i); //up,left为向左上方移动，此时j为0~15；down,right为向右下方移动，此时j为15~0
            int r = j / side; // 0~3或3~0
            int c = j % side; // 0~3或3~0
            if (tiles[r][c] == null) // 如果这个位置没有值，则跳过
                continue;
            int nextR = r + yIncr; // 下一个位置的行号
            int nextC = c + xIncr; // 下一个位置的列号
            while (nextR >= 0 && nextR < side && nextC >= 0 && nextC < side) {
                Tile next = tiles[nextR][nextC]; // 下一个位置的值
                Tile curr = tiles[r][c]; // 当前位置的值
                if (next == null) { //下一个位置为空则可以直接移过去
                    if (checkingAvailableMoveState) //当程序为检查状态时，不实际执行移动，而是直接返回true
                        return true;
                    tiles[nextR][nextC] = curr; // 将当前位置的值移动到下一个位置
                    tiles[r][c] = null; // 将当前位置的值置空
                    r = nextR; // 更新r和c
                    c = nextC; // 更新r和c
                    nextR += yIncr; // 更新下一个位置的行号
                    nextC += xIncr; // 更新下一个位置的列号
                    canMove = true;
                } else if (next.canMergeWith(curr)) {
                    if (checkingAvailableMoveState) //当程序为检查状态时，不实际执行移动，而是直接返回true
                        return true;
                    int value = next.mergeWith(curr);// 合并两个值
                    if (value > highest) // 如果合并后的值大于最高值，则更新最高值
                        highest = value;
                    score += value; // 加分
                    tiles[r][c] = null; // 将当前位置的值置空
                    canMove = true;
                    break;
                } else
                    break;
            }
        }
        if (canMove) {
            if (highest < target) { // 输赢判断
                clearMerged();
                addRandomTile(); // 补充新的方块
                if (!movesAvailable()) { // 检查是否还有空白方块可供移动
                    gamestate = State.over; // 游戏结束
                    sql.send(Menu.name, score); // 发送到数据库
                }
            } else if (highest == target)
                gamestate = State.won; // 游戏胜利
        }
        return canMove; // 未能移动则返回false
    }

    boolean moveUp() {
        return move(0, -1, 0);
    }

    boolean moveDown() {
        return move(side * side - 1, 1, 0); //15
    }

    boolean moveLeft() {
        return move(0, 0, -1);
    }

    boolean moveRight() {
        return move(side * side - 1, 0, 1);
    }

    void clearMerged() { // 清除方块属性(是否为已合并的方块)的方法
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }

    boolean movesAvailable() { // 检查是否还有空白方块可供移动的方法
        checkingAvailableMoveState = true; // 将程序标记为检查状态
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight(); // 假装各个方向各执行一次移动，实际并不会真的移动数字
        checkingAvailableMoveState = false;
        return hasMoves;
    }

}

class Tile {
    private boolean merged;
    private int value;

    Tile(int val) {
        value = val;
    }

    int getValue() {
        return value;
    }

    void setMerged(boolean m) {
        merged = m;
    }

    boolean canMergeWith(Tile other) { // 判断两个值是否可以合并
        return !merged && other != null && !other.merged && value == other.getValue();
    }

    int mergeWith(Tile other) { // 合并两个值
        if (canMergeWith(other)) {
            value *= 2;
            merged = true;
            return value;
        }
        return -1;
    }
}
