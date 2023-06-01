package Page_Replacer;

import java.util.ArrayList;

public abstract class Replacer {
    public abstract int[] returnHit_Fault();
    public abstract ArrayList<ArrayList<Integer>> returnStatus();
    public abstract ArrayList<int[]> returnChecker();
}
