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
import javax.swing.table.DefaultTableCellRenderer;
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
    ArrayList<int[]> checkerList;
    ArrayList<Integer> pageList;

    public GUI() {
        pageList = new ArrayList<>();
        checkerList = new ArrayList<>();

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

                if (pageCount > convertRequests.length) {
                    JOptionPane.showMessageDialog(GUI.this, "프레임 개수는 요구 페이지 개수보다 작거나 같아야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                frameNum = pageCount;
                pageList.clear();
                for(int i : convertRequests)
                    pageList.add(i);

                // 페이지 교체 알고리즘 실행
                runPageReplacementAlgorithm(algorithm, pageCount, pageList);

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
        checkerList = pageReplacementAlgorithm.returnChecker();

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
        Color cellColor; // 셀 색깔

        // 열의 개수
        int columnCount = frameStatus.size() + 1; // 프레임 상태 열 + 참조 페이지 열

        // 행의 개수
        int rowCount = frameNum;

        // 테이블 데이터 생성
        Object[][] data = new Object[rowCount][columnCount];

        // 프레임 상태 데이터 설정
        for (int j = 0; j < columnCount-1; j++) {
            ArrayList<Integer> frames = frameStatus.get(j);
            for (int i = 0; i < rowCount; i++) {
                try {
                    data[i][j] = frames.get(i);
                }catch(IndexOutOfBoundsException e) {

                }
            }
        }

        // 행 제목 생성
        Object[] rowNames = new Object[pageReferences.size()];
        for(int i =0;i<pageReferences.size();i++){
            rowNames[i] = pageReferences.get(i);
        }

        // 기존 테이블 제거
        if (scrollPane != null) {
            getContentPane().remove(scrollPane);
        }

        // JTable 생성
        JTable table = new JTable(data, rowNames);
        table.setFont(new Font("D2Coding", Font.PLAIN, 15));

        CellRenderer renderer = new CellRenderer(frameNum, checkerList, pageList); // 셀렌더러
        table.setDefaultRenderer(Object.class, renderer);

        // 테이블을 포함하는 JScrollPane 생성
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200)); // 스크롤 패널의 크기 조정

        // 컴포넌트를 프레임에 추가
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

class CellRenderer extends DefaultTableCellRenderer {

    ArrayList<int[]> checker;
    ArrayList<Integer> page;
    int column;

    public CellRenderer(int frameNum, ArrayList<int[]> checker, ArrayList<Integer> page){
        this.column = frameNum;
        this.checker = new ArrayList<>();
        this.checker.addAll(checker);
        this.page = new ArrayList<>();
        this.page.addAll(page);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        for (int i = 0; i < page.size(); i++) {
            int targetRow = checker.get(i)[0];
            int targetColumn = i;
            int check = checker.get(i)[1];
            if (row == targetRow && column == targetColumn && check == 0) {
                cell.setBackground(Color.ORANGE);
                return cell;
            }
        }

//        int[][] targetCells = { {0, 5}, {1, 3}, {3, 7} };
//        for (int i = 0; i < targetCells.length; i++) {
//            int targetRow = targetCells[i][0];
//            int targetColumn = targetCells[i][1];
//            if (row == targetRow && column == targetColumn) {
//                cell.setBackground(Color.YELLOW);
//                return cell;
//            }
//        }

        cell.setBackground(table.getBackground());
        return cell;
    }


}
