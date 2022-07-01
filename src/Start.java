import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Start implements ActionListener {

    public void actionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setTitle("2048");
                f.setResizable(false); // 禁止改变窗口大小
                f.add(new Game(), BorderLayout.CENTER);
                f.pack(); // 窗口大小自适应
                f.setLocationRelativeTo(null); // 窗口置于屏幕中央
                f.setVisible(true);
            }
        });
    }
}