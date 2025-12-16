/**
 * WordCount class that stores a word and its count.
 */
package src.main.java;

public class WordCount implements Comparable<WordCount> {

    /* TODO: Use these variables */
    private String word;
    private int count;


    // TODO: implement accessors/getters
    public String getWord(){
        return word;
    }
    public int getNumValue(){
        return count;
    }
    public int setNumValue(int value){
        this.count = value;
        return count;
    }
    
    // TODO: implement constructor(s)
    public WordCount(){
        word = "";
        count = 0;
    }
    public WordCount(String specword){
        this.word = specword;
        count = 1;
    }
    public WordCount(String specword, int value){
        this.word = specword;
        this.count = value;
    }
    // TODO: implement toString()
    public String toString(){
        return String.format("%s : %d", word, count);
    }
    public int compareTo(WordCount other) {
        // TODO: implement the Comparable interface method
        int output = other.getNumValue() - this.count;
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
        if(output == 0){
            return this.getWord().compareTo(other.getWord());
        }
        return output;
    }
}
