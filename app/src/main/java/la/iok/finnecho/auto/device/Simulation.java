package la.iok.finnecho.auto.device;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Finn on 2016/9/18 0018.
 */
public class Simulation {
    private static boolean isRoot = false;
    private static Process process = null;
    private static DataOutputStream os = null;
    private static DataInputStream is = null;

    public static boolean getRoot() {
        try {
            process = Runtime.getRuntime().exec("su\n");
            os = new DataOutputStream(process.getOutputStream());
            os.flush();
            is = new DataInputStream(process.getInputStream());
        } catch (Exception e) {
            Log.e("Finn", "the device is not rooted,  error message： " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static boolean sendScreenClick(int x, int y){
        if (!isRoot) {
            isRoot = getRoot();
        }
        try {
            os.write("chmod 777 /dev/input/event1\n".getBytes());
            os.write("sendevent /dev/input/event1 0 4 144\n".getBytes());
            os.write("sendevent /dev/input/event1 0 5 2\n".getBytes());
            os.write("sendevent /dev/input/event1 0 4 84165\n".getBytes());
            os.write("sendevent /dev/input/event1 0 5 426793538\n".getBytes());
            os.write("sendevent /dev/input/event1 3 57 13228\n".getBytes());
            os.write(("sendevent /dev/input/event1 3 53 " + x +"\n").getBytes());
            os.write(("sendevent /dev/input/event1 3 54 " + y +"\n").getBytes());
            os.write("sendevent /dev/input/event1 0 0 0\n".getBytes());
            os.write("sendevent /dev/input/event1 0 4 84165\n".getBytes());
            os.write("sendevent /dev/input/event1 0 5 494475576\n".getBytes());
            os.write("sendevent /dev/input/event1 3 57 -1\n".getBytes());
            os.write("sendevent /dev/input/event1 0 0 0\n".getBytes());
            os.flush();
        } catch (IOException e) {
            Log.e("Finn", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
