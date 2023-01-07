public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        HLog.hInfo("msg -d점 -i번 -i 회 -i -s -b", 1.13, 1, 2, 3, "mesg", false);
        long end = System.currentTimeMillis();
        System.out.println("수행시간: " + (end - start) + " ms");
    }
}