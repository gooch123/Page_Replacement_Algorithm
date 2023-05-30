package LRU;

import java.util.ArrayList;

public class LRU {
    int frameNum;
    int pageFault = 0;
    int pageHit = 0;
    ArrayList<Integer> pageList,frame;
    int[] frameNotReferenceCount;

    public LRU(int frameNUm, ArrayList<Integer> list){
        this.frameNum = frameNUm;
        pageList = new ArrayList<>();
        frameNotReferenceCount = new int[frameNUm];
        frame = new ArrayList<>(frameNUm);
        for(int i : list)
            pageList.add(i);
        exec();
    }

    private void exec(){
        for(int currentPage : pageList){
            increaseNotReferenceCount();
            if(frame.contains(currentPage)){
                pageHit++;
                frameNotReferenceCount[frame.indexOf(currentPage)] = 0;
            }else{
                pageFault++;
                if(frame.size() < frameNum){
                    frame.add(currentPage);
                }else{
                    int oldestIndex = oldestReferenceFrame();
                    frame.set(oldestIndex,currentPage);
                    frameNotReferenceCount[oldestIndex] =0;
                }
            }
        }
        System.out.println(String.format("LRU >> pageHit : %3d , pageFault : %3d",pageHit,pageFault));
    }

    private int oldestReferenceFrame(){
        int index =0;
        int maxCount =0;
        for(int i=0;i<frameNum;i++){
            if(frameNotReferenceCount[i] > maxCount){
                maxCount = frameNotReferenceCount[i];
                index = i;
            }
        }
        return index;
    }

    private void increaseNotReferenceCount(){
        try {
            for(int i=0;i<frame.size();i++){
                int exceptionCheck = frame.get(i);
                frameNotReferenceCount[i]++;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            //frame 비어있음
        }
    }
}
