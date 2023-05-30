package GUI;

import Page_Replacer.FIFO.FIFO;
import Page_Replacer.LRU.LRU;
import Page_Replacer.LRU_Clock.LRU_Clock;
import Page_Replacer.OPT.OPT;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Page_Replacer.*;


public class GUI extends JFrame {
    private JPanel chartPanel;
    private int pageHit;
    private int pageFault;
    private int frameNum;
    private JScrollPane scrollPane;
    private static final int width = 800, height = 600;

    public GUI() {
        setTitle("Page Replacement Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);

        // 차트를 보여줄 패널 생성
        chartPanel = new JPanel();

        // 페이지 개수 입력 필드
        JLabel pageLabel = new JLabel("프레임 개수:");
        JTextField pageField = new JTextField(10);

        // 요구 페이지 입력 필드
        JLabel requestLabel = new JLabel("요구 페이지:");
        JTextField requestField = new JTextField(10);

        // 알고리즘 선택 드롭다운 메뉴
        JLabel algorithmLabel = new JLabel("알고리즘:");
        JComboBox<String> algorithmComboBox = new JComboBox<>();
        algorithmComboBox.addItem("FIFO");
        algorithmComboBox.addItem("LRU");
        algorithmComboBox.addItem("Clock");
        algorithmComboBox.addItem("OPT");

        // 그래프 그리기 버튼
        JButton drawButton = new JButton("그래프 그리기");
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력값 가져오기
                int pageCount = Integer.parseInt(pageField.getText());
                int[] convertRequests = parseRequests(requestField.getText());
                int algorithm = algorithmComboBox.getSelectedIndex() + 1;

                if (pageCount < convertRequests.length) {
                    JOptionPane.showMessageDialog(GUI.this, "프레임 개수는 요구 페이지 개수보다 크거나 같아야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                frameNum = pageCount;
                ArrayList<Integer> requests = new ArrayList<>();
                for(int i : convertRequests)
                    requests.add(i);

                // 페이지 교체 알고리즘 실행
                runPageReplacementAlgorithm(algorithm, pageCount, requests);

                // 페이지 히트, 폴트 결과를 그래프로 그리기
                drawChart();
            }
        });

        // 컴포넌트를 프레임에 추가
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(pageLabel);
        inputPanel.add(pageField);
        inputPanel.add(requestLabel);
        inputPanel.add(requestField);
        inputPanel.add(algorithmLabel);
        inputPanel.add(algorithmComboBox);
        inputPanel.add(drawButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);
    }

    private int[] parseRequests(String input) {
        char[] convert = input.toCharArray();
        int[] requests = new int[input.length()];
        for(int i =0;i<convert.length;i++){
            requests[i] = Integer.valueOf(convert[i]-48);
        }

        return requests;
    }

    private void drawChart() {
        // 차트 데이터 생성
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Page Hit", pageHit);
        dataset.setValue("Page Fault", pageFault);

        // 차트 생성
        JFreeChart chart = ChartFactory.createPieChart("Page Hit & Fault Ratio", dataset, true, true, false);

        // 차트 옵션 설정
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("D2Coding", Font.PLAIN, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.7f);

        // 차트 패널 생성
        ChartPanel chartPanel = new ChartPanel(chart);

        // 기존 차트 패널을 제거하고 새로운 차트 패널로 교체
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMouseZoomable(true);

        // 차트 패널을 그래프 패널로 설정
        chartPanel.validate();
        chartPanel.repaint();
        setChartPanel(chartPanel);
    }

    private void runPageReplacementAlgorithm(int algorithm, int pageCount, ArrayList<Integer> requests) {
        Replacer pageReplacementAlgorithm;

        // 알고리즘 선택
        switch (algorithm) {
            case 1: // FIFO
                pageReplacementAlgorithm = new FIFO(pageCount, requests);
                break;
            case 2: // LRU
                pageReplacementAlgorithm = new LRU(pageCount, requests);
                break;
            case 3: // Clock
                pageReplacementAlgorithm = new LRU_Clock(pageCount, requests);
                break;
            case 4: // OPT
                pageReplacementAlgorithm = new OPT(pageCount, requests);
                break;
            default:
                throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }

        // 페이지 교체 알고리즘 실행
        int[] hitFault = pageReplacementAlgorithm.returnHit_Fault();
        pageHit = hitFault[0];
        pageFault = hitFault[1];

        drawProcess(pageReplacementAlgorithm.returnStatus(),requests);
    }

    private void setChartPanel(JPanel panel) {
        getContentPane().remove(chartPanel);
        chartPanel = panel;
        chartPanel.setPreferredSize(new Dimension(500, 350)); // 차트 패널의 크기 조정

        getContentPane().add(chartPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }


    private void drawProcess(ArrayList<ArrayList<Integer>> frameStatus, ArrayList<Integer> pageReferences) {
        // 테이블 데이터 생성
        int rowCount = frameStatus.size() + 1; // 프레임 상태 행 + 참조 페이지 행
        Object[][] data = new Object[rowCount][frameNum];

        // 프레임 상태 데이터 설정
        for (int i = 0; i < rowCount - 1; i++) {
            ArrayList<Integer> frames = frameStatus.get(i);
            for (int j = 0; j < frameNum; j++) {
                try {
                    data[i][j] = frames.get(j);
                }catch (IndexOutOfBoundsException e){

                }
            }
        }

        // 참조 페이지 데이터 설정
        for (int j = 0; j < frameNum; j++) {
            data[rowCount - 1][j] = pageReferences.get(j);
        }

        // 열 제목 생성
        Object[] columnNames = new Object[frameNum];
        for (int i = 0; i < frameNum; i++) {
            columnNames[i] = "프레임 " + (i + 1);
        }

        // JTable 생성
        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("D2Coding", Font.PLAIN, 15));


        if (scrollPane != null) {
            getContentPane().remove(scrollPane);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200)); // 스크롤 패널의 크기 조정

        TableColumnModel columnModel = table.getColumnModel();
        int columnWidth = 100; // 열의 크기를 원하는 값으로 지정
        for (int i = 0; i < frameNum; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(columnWidth);
        }

        getContentPane().add(scrollPane, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
}

