/** 
 * WordFreq.java
 * 
 * Encapsulates the word and its relative frequency in an object
 * 
 * Provided for the student
 */

package src.main.java;

public class WordFreq implements Comparable<WordFreq>{
    private String word;
    private double freq;

    public WordFreq(String word, double freq) {
        this.word = word;
        this.freq = freq;
    }

    public String getWord() {
        return this.word;
    }

    public double getFreq() {
        return this.freq;
    }

    public String toString() {
        String f1 = "%12s : %.2f";
        return String.format(f1, word, freq); 
    }
    public int compareTo(WordFreq other) {
        // TODO: implement the Comparable interface method

        // if(output == 0){
        //     int index = 0;
        //     while(index < other.getWord().length() && index < this.getWord().length()){
        //         if(other.getWord().charAt(index) != this.getWord().charAt(index)){
        //             output = this.getWord().charAt(index) - other.getWord().charAt(index);
        //             break;
        //         }
        //         index ++;
        //     }
        // }
        if(this.getFreq() == other.getFreq()){
            return this.getWord().compareTo(other.getWord());
        }
        return (int) (100000 * (other.getFreq() - this.getFreq()));
    }
}
