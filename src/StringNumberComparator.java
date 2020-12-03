import java.util.Comparator;

class StringNumberComparator implements Comparator<String> {


    public int compare(String strNumber1, String strNumber2) {

        //convert String to int first
        int number1 = Integer.parseInt( strNumber1 );
        int number2 = Integer.parseInt( strNumber2 );

        //compare numbers
        if( number1 > number2 ){
            return 1;
        }else if( number1 < number2 ){
            return -1;
        }else{
            return 0;
        }
    }

}