import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
	
	private int totalVocab;
	private Map<String, Integer> posOccr = new HashMap<>();
    private Map<String, Integer> negOccr = new HashMap<>();
    private Map<Label,Integer> wordNumLabel = new HashMap<>();
    private Map<Label, Integer> docNumLabel = new HashMap<>();
	
	/**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        // TODO : Implement
    	this.totalVocab = v;
    	counter(trainData);
    }

    private void counter(List<Instance> trainData) {
    	if(trainData == null || trainData.size() == 0) {
    		return;
    	}
    	wordNumLabel.put(Label.POSITIVE, 0);
    	wordNumLabel.put(Label.NEGATIVE, 0);
    	docNumLabel.put(Label.POSITIVE, 0);
    	docNumLabel.put(Label.NEGATIVE, 0);
    	posOccr.clear();
    	negOccr.clear();
    	for(Instance s : trainData) {
    		if(s.label.equals(Label.POSITIVE)) { // positive instance
    			for(String p : s.words) {
    				if(posOccr.get(p) == null) {
    					posOccr.put(p, 1);
    				}else {
    					posOccr.replace(p, posOccr.get(p)+1);
    				}
    				wordNumLabel.replace(Label.POSITIVE, wordNumLabel.get(Label.POSITIVE)+1);
    			}
    			docNumLabel.replace(Label.POSITIVE, docNumLabel.get(Label.POSITIVE)+1);
    		}else if(s.label.equals(Label.NEGATIVE)) { // negative instance
    			for(String n : s.words) {
    				if(negOccr.get(n) == null) {
    					negOccr.put(n, 1);
    				}else {
    					negOccr.replace(n, negOccr.get(n)+1);
    				}
    				wordNumLabel.replace(Label.NEGATIVE, wordNumLabel.get(Label.NEGATIVE)+1);
    			}
    			docNumLabel.replace(Label.NEGATIVE, docNumLabel.get(Label.NEGATIVE)+1);
    		}
    	}
    }
    
    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        // TODO : Implement
    	counter(trainData);
    	return wordNumLabel;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        // TODO : Implement
    	counter(trainData);
    	return docNumLabel;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
    	if(label == Label.POSITIVE) {
    		return (double)docNumLabel.get(Label.POSITIVE)/(docNumLabel.get(Label.NEGATIVE) + docNumLabel.get(Label.POSITIVE));
    	}else {
    		return (double)docNumLabel.get(Label.NEGATIVE)/(docNumLabel.get(Label.NEGATIVE) + docNumLabel.get(Label.POSITIVE));
    	}
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
    	// (c_l(w)+e)/|V|e+∑c_l(v), e=1, |v| = totalVocab
    	int c_l_w = 0;
    	int sum_c_l_v = 0;
    	if(label == Label.POSITIVE) {
    		if(posOccr.get(word) == null) {
    			c_l_w = 0;
    		}else {
    			c_l_w = posOccr.get(word);
    		}
    		sum_c_l_v = wordNumLabel.get(Label.POSITIVE); 
    	}else {
    		if(negOccr.get(word) == null) {
    			c_l_w = 0;
    		}else {
    			c_l_w = negOccr.get(word);
    		}
    		sum_c_l_v = wordNumLabel.get(Label.NEGATIVE);
    	}
    	double prob = (double)(c_l_w + 1)/(totalVocab + sum_c_l_v);
        return prob;
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
    	// positive: logP(l=p)+∑logP(w_i|l=p)
    	// choose max from p and n --> set label
    	
    	ClassifyResult result = new ClassifyResult();
    	result.logProbPerLabel = new HashMap<>();
    	// 
    	double pos = Math.log(p_l(Label.POSITIVE));
    	double neg = Math.log(p_l(Label.NEGATIVE));
    	for(String w : words) {
    		pos += Math.log(p_w_given_l(w, Label.POSITIVE));
    		neg += Math.log(p_w_given_l(w, Label.NEGATIVE));
    	}
    	result.logProbPerLabel.put(Label.POSITIVE, pos);
    	result.logProbPerLabel.put(Label.NEGATIVE, neg);
    	if(pos < neg) {
    		result.label = Label.NEGATIVE;
    	}else {
    		result.label = Label.POSITIVE;
    	}
        return result;
    }


}
