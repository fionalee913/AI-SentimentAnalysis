import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
	
	
	public static List<List<Instance>> split(List<Instance> trainData, int k){
		List<List<Instance>> kFolds = new ArrayList<List<Instance>>();
		int size = (trainData.size()/k);
		int head = 0;
		int tail = size-1;
		while(tail < trainData.size()) {
			List<Instance> list = new ArrayList<>();
			for(int i = head; i <= tail; i++) {
				list.add(trainData.get(i));
			}
			kFolds.add(list);
			head = tail + 1;
			tail = head + size - 1;
			
		}
		return kFolds;
	}
	
	public static double compute(Classifier clf, List<Instance> list) {
		int total = list.size();
		int correct = 0;
		for(Instance i : list) {
			if(clf.classify(i.words).label.equals(i.label)) {
				correct++;
			}
		}
		return (double)correct/total;
	}
	
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        // TODO : Implement
    	if(k < 2 || k > trainData.size()) {
    		return 0;
    	}
    	List<List<Instance>> kFolds = CrossValidation.split(trainData, k);
    	double sum = 0.0;
    	for(int i = 0; i < kFolds.size(); i++) {
    		ArrayList<Instance> train = new ArrayList<>();
    		for(int j = 0; j < kFolds.size(); j++) {
    			if(j != i) {
    				train.addAll(kFolds.get(j));
    			}
    		}
    		clf.train(train, v);
    		sum += CrossValidation.compute(clf, kFolds.get(i));
    	}
        return (sum/k);
    }
}
