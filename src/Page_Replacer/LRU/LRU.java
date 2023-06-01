package Page_Replacer.LRU;

import Page_Replacer.Replacer;

import java.util.ArrayList;

public class LRU extends Replacer {
    int frameNum;
    int pageFault = 0;
    int pageHit = 0;
    ArrayList<Integer> pageList,frame;
    ArrayList<ArrayList<Integer>> frameStatus;
    ArrayList<int[]> checkerList;
    int[] frameNotReferenceCount;

    public LRU(int frameNUm, ArrayList<Integer> list){
        frameStatus = new ArrayList<ArrayList<Integer>>();
        checkerList = new ArrayList<>();
        this.frameNum = frameNUm;
        pageList = new ArrayList<>();
        frameNotReferenceCount = new int[frameNUm];
        frame = new ArrayList<>(frameNUm);
        for(int i : list)
            pageList.add(i);
        exec();
    }

    private void exec(){
        for (int i =0;i<frameNum;i++)
            checkerList.add(new int[]{i,0});
        for(int currentPage : pageList){
            increaseNotReferenceCount();
            if(frame.contains(currentPage)){
                pageHit++;
                frameNotReferenceCount[frame.indexOf(currentPage)] = 0;
                checkerList.add(new int[]{0,1});
            }else{
                pageFault++;
                if(frame.size() < frameNum){
                    frame.add(currentPage);
                }else{
                    int oldestIndex = oldestReferenceFrame();
                    frame.set(oldestIndex,currentPage);
                    frameNotReferenceCount[oldestIndex] =0;
                    checkerList.add(new int[]{oldestIndex,0});
                }
            }
            saveFrameStatus();
        }
        System.out.println(String.format("LRU >> pageHit : %3d , pageFault : %3d",pageHit,pageFault));
        frameStatus.stream().forEach(System.out::println);
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

    private void saveFrameStatus(){
        ArrayList<Integer> saver = new ArrayList<>();
        for(int page : frame){
            saver.add(page);
        }
        frameStatus.add(saver);
    }

    @Override
    public ArrayList<int[]> returnChecker() {
        return checkerList;
    }

    @Override
    public ArrayList<ArrayList<Integer>> returnStatus() {
        return frameStatus;
    }

    @Override
    public int[] returnHit_Fault() {
        int[] hit_fault = {pageHit,pageFault};
        return hit_fault;
    }
}
