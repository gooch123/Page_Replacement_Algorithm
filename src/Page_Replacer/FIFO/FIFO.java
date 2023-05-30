package Page_Replacer.FIFO;

import Page_Replacer.Replacer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FIFO extends Replacer {
    ArrayList<Integer> pageList;
    Queue<Integer> q;
    int pageFault = 0;
    int frameNum;
    int pageHit =0;

    public FIFO(int frameNum, ArrayList<Integer> list){
        pageList = new ArrayList<>();
        q = new LinkedList<>();
        this.frameNum = frameNum;
        for(int i : list)
            pageList.add(i);
        exec();
    }

    private void exec(){
        for(int i : pageList){
            if(!q.contains(i)){
                pageFault++;
                if(q.size() == frameNum){ // 큐가 가득차면
                    q.poll();
                    q.add(i);
                }else
                    q.add(i);
            }else{
                pageHit++;
            }
        }
        System.out.println(String.format("FIFO >> pageHit : %3d ,pageFault : %3d",pageHit, pageFault));
    }

    @Override
    public int[] returnHit_Fault() {
        int[] hit_fault = {pageHit,pageFault};
        return hit_fault;
    }
}
