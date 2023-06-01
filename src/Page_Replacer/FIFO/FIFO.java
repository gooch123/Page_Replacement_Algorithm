package Page_Replacer.FIFO;

import Page_Replacer.Replacer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FIFO extends Replacer {
    ArrayList<Integer> pageList,frame;
    ArrayList<ArrayList<Integer>> frameStatus;
    Queue<Integer> q;
    int pageFault = 0;
    int frameNum;
    int pageHit =0;
    int frameIndex = 0;
    ArrayList<int[]> checkerList;

    public FIFO(int frameNum, ArrayList<Integer> list){
        checkerList = new ArrayList<>();
        frameStatus = new ArrayList<>();
        pageList = new ArrayList<>();
        frame = new ArrayList<>();
        q = new LinkedList<>();
        this.frameNum = frameNum;
        for(int i : list)
            pageList.add(i);
        exec();
    }

    private void exec(){
        for(int currentPage : pageList){
            if(frame.contains(currentPage)){
                pageHit++;
                int columnNum = frameIndex % frameNum;
                checkerList.add(new int[]{columnNum, 1});
            }else{
                pageFault++;
                int columnNum = frameIndex % frameNum;
                checkerList.add(new int[]{columnNum, 0});
                if(frame.size() < frameNum){
                    frame.add(currentPage);
                }else{
                    frame.set(frameIndex % frameNum,currentPage);
                }
                frameIndex++;
            }
            saveFrameStatus();
        }
        System.out.printf("FIFO >> pageHit : %3d ,pageFault : %3d%n",pageHit, pageFault);
//        frameStatus.stream().forEach(System.out::println);
        for(int[] i : checkerList)
            System.out.printf("열 : %d , 체커 : %d%n",i[0],i[1]);
    }

    private void saveFrameStatus(){
        ArrayList<Integer> saver = new ArrayList<>();
        for(int page : frame){
            saver.add(page);
        }
        frameStatus.add(saver);
    }

    @Override
    public ArrayList<ArrayList<Integer>> returnStatus() {
        return frameStatus;
    }

    @Override
    public ArrayList<int[]> returnChecker(){
        return checkerList;
    }

    @Override
    public int[] returnHit_Fault() {
        int[] hit_fault = {pageHit,pageFault};
        return hit_fault;
    }
}
