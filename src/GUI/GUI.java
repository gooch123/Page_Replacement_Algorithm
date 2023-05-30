package GUI;

import Page_Replacer.FIFO.FIFO;
import Page_Replacer.LRU.LRU;
import Page_Replacer.LRU_Clock.LRU_Clock;
import Page_Replacer.OPT.OPT;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
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

    public GUI() {
        setTitle("Page Replacement Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        // 차트를 보여줄 패널 생성
        chartPanel = new JPanel();

        // 페이지 개수 입력 필드
        JLabel pageLabel = new JLabel("페이지 개수:");
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
                for(int i =0;i<convertRequests.length;i++){
                    requests.add(convertRequests[i]);
                }

                // 페이지 교체 알고리즘 실행
                runPageReplacementAlgorithm(algorithm, pageCount, requests);
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
        char[] inputChar = input.toCharArray();
        int[] requests = new int[inputChar.length];
        for(int i =0;i<inputChar.length;i++){
            try {
                requests[i] = Integer.valueOf(inputChar[i]);
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 값이 포함된 경우 오류 처리
                e.printStackTrace();
                // 혹은 다른 오류 처리 방식을 선택할 수 있습니다.
            }
        }

        return requests;
    }


    private void drawChart(int pageHit, int pageFault) {
        // 차트 데이터 생성
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("페이지 히트", pageHit);
        dataset.setValue("페이지 폴트", pageFault);

        // 차트 생성
        JFreeChart chart = ChartFactory.createPieChart("페이지 히트 및 폴트 비율", dataset, true, true, false);

        // 차트 스타일 설정
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}"));
        plot.setSectionPaint("페이지 히트", Color.GREEN);
        plot.setSectionPaint("페이지 폴트", Color.RED);

        // 차트 패널 초기화
        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout());

        // 차트 패널에 차트 추가
        ChartPanel chartPanel = new ChartPanel(chart);

        // 차트 패널 갱신
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void runPageReplacementAlgorithm(int algorithm, int pageCount, ArrayList<Integer> requests) {
        Replacer pageReplacementAlgorithm;

        // 알고리즘 선택
        switch (algorithm) {
            case 1: // Page_Replacer.OPT.OPT.FIFO
                pageReplacementAlgorithm = new FIFO(pageCount,requests);
                break;
            case 2: // Page_Replacer.OPT.OPT.LRU
                pageReplacementAlgorithm = new LRU(pageCount,requests);
                break;
            case 3: // Clock
                pageReplacementAlgorithm = new LRU_Clock(pageCount,requests);
                break;
            case 4: // Page_Replacer.OPT.OPT
                pageReplacementAlgorithm = new OPT(pageCount,requests);
                break;
            default:
                throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }

        // 페이지 교체 알고리즘 실행
        int pageHit = pageReplacementAlgorithm.returnHit_Fault()[0];
        int pageFault = pageReplacementAlgorithm.returnHit_Fault()[1];


        // 페이지 히트, 폴트 결과를 그래프로 그리기
        drawChart(pageHit, pageFault);
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
