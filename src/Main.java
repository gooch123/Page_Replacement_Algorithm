import java.util.*;
import java.util.stream.IntStream;

import Page_Replacer.FIFO.FIFO;
import Page_Replacer.LRU.LRU;
import Page_Replacer.LRU_Clock.LRU_Clock;
import Page_Replacer.OPT.OPT;

class Exec{
    ArrayList<Integer> pageList;
    Scanner sc;
    int frameNumber;
    int[] frame;
    int menu;

    public Exec(){
        pageList = new ArrayList<Integer>();
        System.out.print("프레임 개수를 입력해주세요 >> ");
        sc = new Scanner(System.in);
        frameNumber = sc.nextInt();
        frame = new int[frameNumber];
        while(true){
            sc = new Scanner(System.in);
            System.out.println("---------------------Menu----------------------\n" +
                    "(1) : 요구 페이지 입력 (2) : 랜덤 페이지 입력 (3) : 프레임 개수 설정 (0) : 종료\n+" +
                    "(4) : FIFO 실행 (5) : OPT 실행 (6) : LRU 실행 (7) : Clock 실행 (9) : testCase 입력 >> ");
            try {
                menu = sc.nextInt();
                if(menu == 0)
                    return;
            }catch (InputMismatchException e){
                break;
            }
            switch (menu){
                case 1:
                    inputPage();
                    break;
                case 2:
                    inputRandomPage();
                    break;
                case 3:
                    setFrameNumber();
                    break;
                case 4:
                    startFIFO();
                    break;
                case 5:
                    startOPT();
                    break;
                case 6:
                    startLRU();
                    break;
                case 7:
                    startClock();
                    break;
                case 9:
                    testCase();
                    break;
                default:
                    System.out.println("다시 입력해주세요");
                    break;
            }
        }
    }

    void inputPage(){
        int num;
        if(!pageList.isEmpty())
            pageList.clear();
        System.out.println("요구 페이지를 입력해주세요. 형식은 0~9 사이 숫자입니다 (종료 : 문자 또는 범위 밖의 수)");
        while (true){
            System.out.print(">> ");
            try {
                num = sc.nextInt();
            }catch (InputMismatchException e){
                break;
            }
            if(num >9 || num < 0)
                break;;
            pageList.add(num);
        }
        Integer[] showPage = pageList.stream().toArray(Integer[]::new);
        System.out.println("입력한 페이지 : " + Arrays.toString(showPage));
    }

    void inputRandomPage(){
        System.out.println("랜덤으로 페이지를 입력합니다.");
        IntStream randomStream = new Random().ints(0,10);
        int[] needPage = randomStream.limit(20).toArray();
        System.out.println(Arrays.toString(needPage));
        pageList.clear();
        for(int i : needPage)
            pageList.add(i);
    }

    void setFrameNumber(){
        System.out.println("프레임 개수를 설정해주세요");
        frameNumber = sc.nextInt();
        frame = new int[frameNumber];
    }

    void startFIFO(){
        new FIFO(frameNumber,pageList);
    }

    void startOPT(){
        new OPT(frameNumber,pageList);
    }

    void startLRU() {new LRU(frameNumber,pageList);}

    void startClock() {new LRU_Clock(frameNumber,pageList);}

    void testCase(){
        int[] caselist = {7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1};
        pageList.clear();
        for(int i : caselist)
            pageList.add(i);
    }
}

public class Main {
    public static void main(String[] args) {
        new Exec();
    }
}