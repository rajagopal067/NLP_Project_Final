public class Solution {
    public String reverseWords(String s) {
        
        s = s.trim().replace("  ", " ");
        if( s.length() == 1){
       	return s.trim();
       }
       String combinedString = reverseWord(s.trim());
       StringBuilder reverseWords = new StringBuilder();
       for(String word : combinedString.split(" ")){
           reverseWords.append(reverseWord(word));
           reverseWords.append(" ");
           
       }
       
       return reverseWords.toString().trim();
       
   }
    
    private String reverseWord(String s){ s = s.trim();
    if(s.length() == 1) return s;
    StringBuilder result = new StringBuilder();
    String []words = s.split(" ");
    for(int i = words.length - 1;i >= 0 ; i--){
    if(!words[i].equals("")){
        result.append(words[i]).append(" ");
    }
    }
    return new String(result).trim();
    
}
    public static void main(String[] args) {
		System.out.println(new Solution().reverseWord(" a   b  "));
	}
    
}