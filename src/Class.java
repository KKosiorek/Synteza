import static java.lang.System.out;

public final class Class {
    private void methodVoid(String s) {
        String aax = s.toUpperCase();
        String aux = s.toUpperCase();
    }

    private static String upper(String s) {
        String aax = s.toUpperCase();
        String aux = s.toUpperCase();
        return aux + aax;
    }

    public static int count(String s) {
        int length = s.length();
        return length;
    }

    public static int method(int x) {
        return x+1;
    }

    public static Integer methodReturnedObject(int x){
        return Integer.valueOf(x+1);
    }
}

