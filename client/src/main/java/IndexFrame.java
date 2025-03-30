import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IndexFrame {
    public IndexFrame(){
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建渐变背景面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 120, 255),
                        0, getHeight(), Color.WHITE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // 内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridLayout(4, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 设置楷体字体
        Font kaiFont = new Font("楷体", Font.PLAIN, 14);

        // 第一行：提示文字
        JLabel label = new JLabel("请进行连接", SwingConstants.CENTER);
        label.setFont(kaiFont);
        contentPanel.add(label);

        // 第二行：IP输入
        JPanel ipPanel = createInputPanel("IP:", true);
        contentPanel.add(ipPanel);

        // 第三行：端口输入
        JPanel portPanel = createInputPanel("端口号:", false);
        contentPanel.add(portPanel);

        // 第四行：按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton connectBtn = createStyledButton("连接");
        JButton closeBtn = createStyledButton("关闭");

        connectBtn.addActionListener(e -> tryConnect());
        closeBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(connectBtn);
        buttonPanel.add(closeBtn);
        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(mainPanel);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createInputPanel(String labelText, boolean allowDot) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("楷体", Font.PLAIN, 14));
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("楷体", Font.PLAIN, 14));

        // 设置输入限制
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (isValid(string, allowDot)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isValid(text, allowDot)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isValid(String text, boolean allowDot) {
                for (char c : text.toCharArray()) {
                    if (!Character.isDigit(c) && !(allowDot && c == '.')) {
                        return false;
                    }
                }
                return true;
            }
        });

        panel.add(label);
        panel.add(textField);
        return panel;
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("楷体", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 鼠标交互效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 220, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(180, 200, 240));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(200, 220, 255));
            }
        });

        return button;
    }

    private static void tryConnect() {
        //TODO 创建正式交互界面


    }
}
