package Page_Replacer.LRU_Clock;

import Page_Replacer.Replacer;

import java.util.ArrayList;

public class LRU_Clock extends Replacer {
    int frameNum, pageHit=0, pageFault=0, bitPointer = 0;
    boolean[] referenceBit;
    ArrayList<Integer> pageList;
    ArrayList<Integer> frame;

    public LRU_Clock(int frameNum, ArrayList<Integer> list){
        this.frameNum = frameNum;
        referenceBit = new boolean[frameNum];
        frame = new ArrayList<>();
        pageList = new ArrayList<>();
        pageList.addAll(list);
        exec();
    }

    private void exec(){
        for(int currentPage : pageList){
            if(frame.contains(currentPage)){
                pageHit++;
                referenceBit[frame.indexOf(currentPage)] = true;
            }else{
                pageFault++;
                if(frame.size() < frameNum){
                    frame.add(currentPage);
                }else{ // 페이지 교체 수행
                    frame.set(findNeverReferencedBit(),currentPage);
                    bitPointer++;
                }
            }
        }
        System.out.printf("Clock >> pageHit : %d , pageFault : %d%n",pageHit,pageFault);
    }

    private int findNeverReferencedBit(){
        int index = 0;
        while (true){
            if(!referenceBit[bitPointer % frameNum]){
                index = bitPointer % frameNum;
                break;
            }else{
                referenceBit[bitPointer % frameNum] = false;
                bitPointer++;
            }
        }
        return index;
    }

    @Override
    public int[] returnHit_Fault() {
        int[] hit_fault = {pageHit,pageFault};
        return hit_fault;
    }
}