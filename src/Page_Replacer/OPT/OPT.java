package Page_Replacer.OPT;

import Page_Replacer.Replacer;

import java.util.ArrayList;
import java.util.HashMap;

public class OPT extends Replacer {
    ArrayList<ArrayList<Integer>> frameStatus;
    ArrayList<int[]> checkerList;
    ArrayList<Integer> pageList;
    ArrayList<Integer> frame;
    int pageFault =0;
    int pageHit =0;
    int frameNum;
    int[] frameCount;

    public OPT(int frameNum, ArrayList<Integer> list){
        frameStatus = new ArrayList<ArrayList<Integer>>();
        checkerList = new ArrayList<>();
        frameCount = new int[frameNum];
        pageList = new ArrayList<>();
        frame = new ArrayList<>();
        this.frameNum = frameNum;
        for(int i : list){
            pageList.add(i);
        }
        for(int i=0;i<frameCount.length;i++)
            frameCount[i] = 0;
        exec();
    }

    private void exec(){
        for(int i = 0;i<frameNum;i++)
            checkerList.add(new int[]{i,0});
        while(!pageList.isEmpty()){
            int currentPage = pageList.remove(0);
            if(frame.contains(currentPage)){
                pageHit++;
                checkerList.add(new int[]{0,1});
            }else{
                pageFault++;
                if(frame.size() < frameNum) {
                    frame.add(currentPage);
                }else{ //교체 페이지 찾기
                    int farthestIndex = getFarthestIndex();
                    frame.set(farthestIndex,currentPage);
                    checkerList.add(new int[]{farthestIndex,0});
                }
            }
            saveFrameStatus();
        }
        System.out.printf("OPT >> pageHit : %3d , pageFault : %3d%n",pageHit,pageFault);
        frameStatus.stream().forEach(System.out::println);
    }

    private int getFarthestIndex(){
        HashMap<Integer,Integer> map = new HashMap<>();
        int index = 0;
        int farthestPage = -1;
        for(int i =0;i<frameNum;i++){
            map.put(i,pageList.indexOf(frame.get(i)));
            if(map.get(i) == -1){
                return i;
            }
            if(farthestPage < map.get(i)){
                index = i;
                farthestPage = map.get(i);
            }
        }
        return index;
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
