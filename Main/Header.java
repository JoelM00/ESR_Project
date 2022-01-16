package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Header {

    public byte[] data;

    public static ArrayList<String> FLAGS = new ArrayList<>(Arrays.asList(
            /* 0 */ "FLAG_DATA",
            /* 1 */ "FLAG_KILL",
            /* 2 */ "FLAG_FLOOD",
            /* 3 */ "FLAG_ACTIVATE",
            /* 4 */ "FLAG_DEACTIVATE",
            /* 5 */ "FLAG_NEW_NEIGHBOURS",
            /* 6 */ "FLAG_ID" //for testing purposes
    ));


    public static int HEADER_SIZE = (int) Math.ceil((double)FLAGS.size()/8); //size in bytes

    public static HashMap<String, Integer> FLAG_POSITION = new HashMap<>();

    static {
        for(String flag : FLAGS) {
            FLAG_POSITION.put(flag,FLAG_POSITION.size());
        }
    }


    public Header(List<String> flags) {
        this.data = new byte[HEADER_SIZE];
        this.mark_flags(flags);
    }

    public void mark_flags(List<String> flags_to_mark){
        for (String flag : flags_to_mark) {
            int pos = FLAG_POSITION.get(flag);
            int byte_number =(int) Math.floor((double)pos/8);
            int real_pos = pos-8*byte_number;
            this.data[byte_number] |= 1 << real_pos;
        }
    }

    public static List<Integer> getHeaderFlags(byte[] data) {
        List<Integer> flags = new ArrayList<>();

        for(int n=0; n<FLAGS.size(); n++) {
            int byte_number =(int) Math.floor((double)n/8);
            int real_pos = n-8*byte_number;

            if (((data[byte_number] >> real_pos) & 1) == 1) {
                flags.add(n);
            }
        }
        return flags;
    }

    public List<Integer> getFlags() {
        return Header.getHeaderFlags(this.data);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            sb.insert(0,s1);
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        Header header = new Header(Arrays.asList("FLAG_DATA","c","FLAG_FLOOD","f"));

        System.out.println(header);

        System.out.println("at 0: " + header.getFlags());


    }

}
