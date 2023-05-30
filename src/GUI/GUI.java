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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Page_Replacer.*;


public class GUI extends JFrame {
    private JPanel chartPanel;
    private int pageHit;
    private int pageFault;

    public GUI() {
        setTitle("Page Replacement Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
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
            requests[i] = Integer.valueOf(convert[i]);
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
    }

    private void setChartPanel(JPanel panel) {
        getContentPane().remove(chartPanel);
        chartPanel = panel;
        getContentPane().add(chartPanel, BorderLayout.CENTER);
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

