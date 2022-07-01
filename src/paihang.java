import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;

public class paihang extends JFrame {
    private final JScrollPane scpDemo;
    private JTableHeader jth;
    private JTable tabDemo;
    private final JButton bt1;
    SQL sql_read=new SQL();

    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());// UI为当前系统风格
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public paihang() {
        super("排行榜"); // JFrame的标题名称
        this.setSize(615, 600); // 控制窗体大小
        this.setLayout(null); // 自定义布局
        this.setLocationRelativeTo(null); // 点击运行以后，窗体在屏幕的位置
        this.scpDemo = new JScrollPane();
        this.bt1 = new JButton("关闭");
        this.bt1.setBounds(250, 480, 100, 30);
        this.scpDemo.setBounds(10, 50, 580, 400); // 设置滚动框大小

        this.bt1.addActionListener(new ActionListener() { // 关闭按钮的响应
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭窗口
                dispose();
            }
        });
        // 将组件加入到窗体中
        add(this.scpDemo);
        add(this.bt1);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/expfinal?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
            String username = "user1";
            String passwords = "111222333";
            Connection conn = DriverManager.getConnection(url, username, passwords);
            String sql = "select * from leaderboard ORDER BY score DESC"; // 按分数排序
            PreparedStatement pre = conn.prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
            }
            rs = pre.executeQuery();
            // 将查询获得的记录数据，转换成适合生成JTable的数据形式
            Object[][] info = new Object[count][3]; // 创建一个二维数组
            String[] title = { "排名", "名字", "分数" };
            count = 0;
            while (rs.next()) {
                info[count][0] = count + 1;
                info[count][1] = rs.getString("name");
                info[count][2] = Integer.valueOf(rs.getInt("score"));
                count++;
            }
            // 创建JTable
            this.tabDemo = new JTable(info, title);
            // 设置表格行高
            this.tabDemo.setRowHeight(30);
            // 设置第一列宽度
            this.tabDemo.getColumnModel().getColumn(0).setPreferredWidth(20);
            // 设置第二列宽度
            this.tabDemo.getColumnModel().getColumn(1).setPreferredWidth(100);
            // 设置第三列宽度
            this.tabDemo.getColumnModel().getColumn(2).setPreferredWidth(100);
            // 设置表格字体
            this.tabDemo.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            // 显示表头
            this.jth = this.tabDemo.getTableHeader();
            // 设置表头字体
            this.jth.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            // 将JTable加入到带滚动条的面板中
            this.scpDemo.getViewport().add(tabDemo);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库连接错误", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库读取错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
