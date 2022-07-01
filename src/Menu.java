import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

public class Menu extends JFrame {
    private JFrame f1;
    private JPanel p1;
    private JLabel l1;
    private JTextField t_name;
    private JButton b_paihang, b_start;
    ActionListener listener_start;
    public static String name = "null";
    {
       try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());// 设置UI为当前系统风格
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Menu() {
        setLayout(null);
        this.f1 = new JFrame("2048");
        this.f1.setSize(550, 90            );
        this.f1.setResizable(false);
        this.f1.setLocation(460, 400);
        this.p1 = new JPanel();
        this.p1.setBackground(new Color(0xb3e5fc));
        this.l1 = new JLabel("请输入姓名:");
        this.l1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        this.l1.setPreferredSize(new DimensionUIResource(85, 30));
        this.t_name = new JTextField(10);
        this.t_name.setFont(new Font("微软雅黑", Font.BOLD, 20));
        this.b_paihang = new JButton("排行");
        this.b_start = new JButton("开始");
        // 设置按钮尺寸
        this.b_paihang.setPreferredSize(new Dimension(100, 35));
        this.b_start.setPreferredSize(new Dimension(100, 35));
        // 添加组件
        this.f1.add(p1);
        this.p1.add(l1);
        this.p1.add(t_name);
        this.p1.add(b_start);
        this.p1.add(b_paihang);
        this.b_paihang.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new paihang();
            }
        });
        this.f1.setVisible(true);
        listener_start = new Start();
        this.b_start.addActionListener(listener_start);
        this.b_start.addMouseListener(new MouseAdapter() { // 两按钮用不同的监听器多少有点乱搞
            public void mouseClicked(MouseEvent e) {
                name = t_name.getText(); // 将文本框内的人名存入name
                f1.dispose(); // 关闭menu窗口
            }
        });
    }
}