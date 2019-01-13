import java.util.*;
import edu.duke.*;
import java.io.*;
public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder slice = new StringBuilder();
        for(int i = whichSlice; i<message.length(); i += totalSlices){
            slice.append(message.charAt(i));
        }
        return slice.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for(int i = 0; i < klength; i++){
            String currSlice = sliceString(encrypted,i,klength);
            CaesarCracker cc = new CaesarCracker(mostCommon);
            int currKey = cc.getKey(currSlice);
            key[i] = currKey;
        }
        return key;
    }

    public void breakVigenere () {
        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        //codes for practice 1 with known language and key length:
        /*
        int [] key = tryKeyLength(encrypted,38,'e');
        VigenereCipher vc = new VigenereCipher(key);
        String decrypted = vc.decrypt(encrypted);
        System.out.println(decrypted.substring(0,100));
        */
       
        //codes for practice 2 with known language and unknown key length:
        /*
        FileResource dictionaryFile = new FileResource();
        HashSet<String> dictionary = readDictionary(dictionaryFile);
        String decrypted = breakForLanguage(encrypted,dictionary);
        System.out.println(decrypted.substring(0,100));
        */
        
        //codes for practice 3 with unknown language and unknown key length:
        DirectoryResource dr = new DirectoryResource();
        HashMap<String,HashSet<String>> map = new HashMap<String,HashSet<String>>();
        for(File f : dr.selectedFiles()){
            FileResource dictionaryFile = new FileResource(f);
            HashSet<String> dictionary = readDictionary(dictionaryFile);
            map.put(f.getName(),dictionary);
            System.out.println("finish reading " + f.getName());
        }
        breakForAllLangs(encrypted,map);
    }
    
    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> wordsList = new HashSet<String>();
        for(String s : fr.lines()){
            s = s.toLowerCase();
            wordsList.add(s);
        }
        return wordsList;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        int count = 0;
        for(String s : message.split("\\W+")){
            s = s.toLowerCase();
            if(dictionary.contains(s)){
                count ++;
            }
        }
        return count;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        int maxCount = 0;
        int keyLength = 0;
        String result = "";
        //add after the third practice:
        char mostCommon = mostCommonCharIn(dictionary);
        for(int i = 1; i <= 100; i++){
            int [] key = tryKeyLength(encrypted,i,mostCommon);
            VigenereCipher vc = new VigenereCipher(key);
            String decrypted = vc.decrypt(encrypted);
            int validWordsCount = countWords(decrypted, dictionary);
            if(validWordsCount > maxCount){
                maxCount = validWordsCount;
                result = decrypted;
                keyLength = i;
           }
        }
        System.out.println("maximum valid words counts: " + maxCount);
        System.out.println("key length: " + keyLength);
        return result;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        int [] alphCount = new int[26];
        for(String s : dictionary){
            s = s.toLowerCase();
            for(int i =0; i<s.length();i++){
                if(alphabet.indexOf(s.charAt(i))!= -1){
                    alphCount[alphabet.indexOf(s.charAt(i))] +=1;
                }
            }
        }
        int maxCount = 0;
        int maxIndex = 0;
        for(int i = 0; i < alphCount.length; i++){
            if (alphCount[i] > maxCount){
                maxCount = alphCount[i];
                maxIndex = i;
            }
        }
        return alphabet.charAt(maxIndex);
    }
    
    public void breakForAllLangs(String encrypted,HashMap<String,HashSet<String>> languages){
        int maxCount = 0;
        String lang = "";
        String decryptedForAll = "";
        for(String s : languages.keySet()){
            String decrypted = breakForLanguage(encrypted, languages.get(s));
            int validCounts = countWords(decrypted,languages.get(s));
            if(validCounts > maxCount){
                maxCount = validCounts;
                lang = s;
                decryptedForAll = decrypted;
            }
        }
        System.out.println(decryptedForAll);
        System.out.println("language: " + lang);
    }
}
