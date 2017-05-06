import static java.lang.System.out;

public final class ClassAltered {

    private void methodVoid(String s) {
        String aax = s.toUpperCase();
        String aux = s.toUpperCase();
    }

    private static String upper(String s) {
        String aax = s.toUpperCase();
        String aux = s.toUpperCase();
        Logger.Log("upper", aux + aax);
        return aux + aax;
    }

    public static int count(String s) {
        int length = s.length();
        Logger.Log("count", length);
        return length;
    }

    public static int method(int x) {
        Logger.Log("method", x + 1);
        return x + 1;
    }

    public static Integer methodReturnedObject(int x) {
        Logger.Log("methodReturnedObject", Integer.valueOf(x + 1));
        return Integer.valueOf(x + 1);
    }
}
