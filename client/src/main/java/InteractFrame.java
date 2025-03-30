import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class InteractFrame extends JFrame {

    // 新增样式常量
    private static final Color SERVER_COLOR = Color.RED;
    private static final Color CLIENT_COLOR = Color.BLACK;
    // 修改为JTextPane以支持彩色文本
    private JTextPane historyPane;
    private StyledDocument doc;
    private JTextField inputField;

    // TCP通信相关变量
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public InteractFrame() {
        initializeUI();
    }

    private void initializeUI() {
        // 初始化文本区域样式
        historyPane = new JTextPane();
        doc = historyPane.getStyledDocument();
        historyPane.setEditable(false);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(historyPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // 主面板使用BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 历史记录区域
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部输入面板
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // 带占位符的输入框
        inputField = new JTextField();
        inputField.setColumns(30);
        setPlaceholder(inputField, "请输入命令");

        // 发送按钮
        JButton sendBtn = new JButton("发送");
        sendBtn.setFont(new Font("楷体", Font.BOLD, 14));

        // 按钮点击事件
        // 修改发送按钮事件
        sendBtn.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                appendHistory("客户端: " + message, CLIENT_COLOR); // 保持黑色
                inputField.setText("");
            }
        });

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        //展示窗口
        setVisible(true);
    }

    // 实现文本框占位符功能
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    // 添加历史记录（线程安全）
    public void appendHistory(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 创建样式
                Style style = historyPane.addStyle("ColorStyle", null);
                StyleConstants.setForeground(style, color);

                // 追加文本
                doc.insertString(doc.getLength(), text + "\n", style);

                // 自动滚动到底部
                historyPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    // TCP消息发送方法（空实现）
    private void sendMessage(String message) {
        // 实际TCP通信逻辑
        try {
            // 示例代码（需根据实际连接初始化）：
            // out.println(message);
        } catch (Exception e) {
            appendHistory("发送失败: " + e.getMessage(), CLIENT_COLOR);
        }
    }

    // 设置TCP连接（供主窗口调用）
    public void setConnection(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        // 可以在此启动消息接收线程
        new Thread(this::receiveMessages).start();
    }

    // 接收消息方法
    private void receiveMessages() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                appendHistory("服务端: " + response, SERVER_COLOR); // 使用红色显示
            }
        } catch (Exception e) {
            appendHistory("连接中断: " + e.getMessage(), SERVER_COLOR);
        }
    }

    // 显式释放资源
    private void releaseResources() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}