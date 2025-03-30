import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class IndexFrame extends JFrame {
    private JTextField ipField;
    private JTextField portField;

    public IndexFrame() {
        initComponents();

    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // 创建主面板（带渐变背景）
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(0, 120, 255);
                Color endColor = Color.WHITE;
                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 设置楷体字体
        Font kaiFont;
        try {
            kaiFont = new Font("楷体", Font.PLAIN, 14);
        } catch (Exception e) {
            kaiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        }

        // 第一行：标题
        JLabel titleLabel = new JLabel("请进行连接");
        titleLabel.setFont(kaiFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);

        // 第二行：IP输入
        JPanel ipPanel = createInputPanel("IP", true);
        mainPanel.add(ipPanel);

        // 第三行：端口输入
        JPanel portPanel = createInputPanel("端口号", false);
        mainPanel.add(portPanel);

        // 第四行：按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton connectBtn = createStyledButton("连接", new Color(100, 200, 100));
        JButton closeBtn = createStyledButton("关闭", new Color(200, 100, 100));

        connectBtn.addActionListener(e -> tryConnect(ipField.getText(), portField.getText()));
        closeBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(connectBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeBtn);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JPanel createInputPanel(String labelText, boolean allowDot) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("楷体", Font.PLAIN, 14));
        panel.add(label);

        JTextField textField = new JTextField(15);
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

        if (labelText.equals("IP")) {
            ipField = textField;
        } else {
            portField = textField;
        }
        panel.add(textField);
        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("楷体", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setOpaque(true);
        button.setContentAreaFilled(false);

        // 鼠标交互效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
                button.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.setOpaque(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }
        });

        return button;
    }

    private void tryConnect(String ip, String port) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            // 创建 Socket 连接
            socket = new Socket(ip, Integer.parseInt(port));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 创建交互窗口并传递连接
            InteractFrame interactFrame = new InteractFrame();
            interactFrame.setConnection(socket, in, out);

            // 将交互窗口显示出来
            interactFrame.setVisible(true);
            //将连接窗口隐藏
            this.setVisible(false);

        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "无法找到主机: " + ip, "错误", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "I/O 异常: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口号无效，请输入正确的数字格式。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


}