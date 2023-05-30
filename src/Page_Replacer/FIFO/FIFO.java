package Page_Replacer.FIFO;

import Page_Replacer.Replacer;

import java.util.ArrayList;
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

    public FIFO(int frameNum, ArrayList<Integer> list){
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
            }else{
                pageFault++;
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
        frameStatus.stream().forEach(System.out::println);
    }

    private void saveFrameStatus(){
        ArrayList<Integer> saver = new ArrayList<>();
        for(int page : frame){
            saver.add(page);
        }
        frameStatus.add(saver);
    }

    @Override
    public int[] returnHit_Fault() {
        int[] hit_fault = {pageHit,pageFault};
        return hit_fault;
    }
}
